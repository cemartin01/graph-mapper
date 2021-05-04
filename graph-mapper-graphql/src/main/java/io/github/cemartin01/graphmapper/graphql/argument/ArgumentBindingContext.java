/*
 * Copyright 2021 cemartin01 (https://github.com/cemartin01).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.cemartin01.graphmapper.graphql.argument;

import graphql.language.Argument;
import graphql.language.ObjectValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import graphql.scalar.GraphqlIDCoercing;
import graphql.schema.Coercing;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.RuntimeWiring;
import io.github.cemartin01.graphmapper.exception.GraphMapperException;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Context of GraphQL-java argument (input) binding.
 *
 * Provides a way to set up classes that may be instantiated out of arguments.
 *
 * Useful for use cases when it's needed to process arguments of nested types in the process of fetching the data.
 *
 * For example:
 * Order {
 *     ...
 *     items(criteria: {page: 1, pageSize:50}) {
 *         ...
 *     }
 * }
 */
public class ArgumentBindingContext {

    private final Map<Class<?>, Coercing> coercingMap = new HashMap<>();

    private final Map<Class<?>, ArgumentBinding<?>> bindingMap = new HashMap<>();

    public ArgumentBindingContext(List<GraphQLScalarType> scalarTypes) {
        for (GraphQLScalarType scalarType : scalarTypes) {
            addScalar(scalarType);
        }
    }

    public ArgumentBindingContext(RuntimeWiring wiring) {
        setCustomScalars(wiring);
    }

    private void setCustomScalars(RuntimeWiring wiring) {
        wiring.getScalars().entrySet().forEach(entry -> {
            addScalar(entry.getValue());
        });
    }

    private void addScalar(GraphQLScalarType scalarType) {
        Coercing c = scalarType.getCoercing();
        if (c.getClass().equals(GraphqlIDCoercing.class)) {
            coercingMap.put(Object.class, c);
        } else {
            for (Method method : c.getClass().getMethods()) {
                if (method.getName().equals("parseValue") && !method.getReturnType().equals(Object.class)) {
                    coercingMap.put(method.getReturnType(), c);
                }
            }
        }
    }

    /**
     * Initializes the binding of a given class.
     *
     * It's possible to instantiate such a class by
     * {@link io.github.cemartin01.graphmapper.graphql.GraphQLMetadata#getObjectArgument} method
     * @param clazz class that is supposed to represent a GraphQL-java argument
     * @throws GraphMapperInitializationException if it's not possible to set up the binding
     */
    public <T> void addBinding(Class<T> clazz) throws GraphMapperInitializationException {

        Map<String, ValueBindings.AbstractBinding<T>> propertyMap = new HashMap<>();
        Supplier<T> constructor = createConstructor(clazz);

        for (Method method: clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                Class<?> fieldType = method.getParameterTypes()[0];

                BiConsumer setter = createSetterMethod(method);
                if (fieldType.equals(List.class)) {
                    ParameterizedType list = (ParameterizedType) method.getGenericParameterTypes()[0];
                    Class<?> listItemType = (Class<?>)list.getActualTypeArguments()[0];
                    propertyMap.put(getFieldName(method.getName()), resolveBinding(clazz, listItemType, setter));
                } else {
                    propertyMap.put(getFieldName(method.getName()), resolveBinding(clazz, fieldType, setter));
                }
            }
        }

        bindingMap.put(clazz, ArgumentBinding.<T>builder()
                .clazz(clazz)
                .constructor(constructor)
                .propertyMap(propertyMap)
                .build());
    }

    private <T> ValueBindings.AbstractBinding<T> resolveBinding(Class<T> clazz, Class<?> fieldType, BiConsumer setter)
            throws GraphMapperInitializationException {

        if (fieldType.isEnum()) {
            return new ValueBindings.EnumBinding<>(setter, createEnumFactory(fieldType));
        } else {
            Coercing c = coercingMap.get(fieldType);
            if (c != null) {
                return new ValueBindings.CoercingBinding<>(setter, c);
            } else {
                ArgumentBinding binding = bindingMap.get(fieldType);
                if (binding != null) {
                    return new ValueBindings.NestedInputBinding<>(setter, binding);
                } else {
                    throw new GraphMapperInitializationException("Type " + fieldType + " is not a scalar and nor a class binding");
                }
            }
        }

    }

    private <T> Supplier<T> createConstructor(Class<T> clazz) throws GraphMapperInitializationException {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle getterHandle = lookup.unreflectConstructor(constructor);
            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "get",
                    MethodType.methodType(Supplier.class),
                    MethodType.methodType(Object.class),
                    getterHandle,
                    MethodType.methodType(constructor.getDeclaringClass())
            );
            return (Supplier<T>) callSite.getTarget().invokeExact();
        } catch (Throwable t) {
            throw new GraphMapperInitializationException("Could not create lambda for non-argument public constructor " + clazz, t);
        }
    }

    private BiConsumer createSetterMethod(Method setter) throws GraphMapperInitializationException {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle setterHandle = lookup.unreflect(setter);
            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    setterHandle,
                    setterHandle.type()
            );
            return (BiConsumer) callSite.getTarget().invokeExact();
        } catch (Throwable t) {
            throw new GraphMapperInitializationException("Could not create lambda for setter " + setter);
        }
    }

    private Function createEnumFactory(Class<?> enumClass) throws GraphMapperInitializationException {
        try {
            Method factoryMethod = enumClass.getMethod("valueOf", String.class);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle getterHandle = lookup.unreflect(factoryMethod);
            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    getterHandle,
                    MethodType.methodType(factoryMethod.getReturnType(), String.class)
            );
            return (Function) callSite.getTarget().invokeExact();
        } catch (Throwable t) {
            throw new GraphMapperInitializationException("Could not create factory method for enum " + enumClass);
        }
    }

    private String getFieldName(String setterName) {
        String fieldName = setterName.substring(3);
        String firstLetterLowerCased = fieldName.substring(0,1).toLowerCase();
        if (fieldName.length() > 1) {
            return firstLetterLowerCased + fieldName.substring(1);
        } else {
            return firstLetterLowerCased;
        }
    }

    public <T> T instantiateArgument(Argument argument, Class<T> clazz, DataFetchingEnvironment env) {
        ArgumentBinding binding = bindingMap.get(clazz);
        if (binding == null) {
            throw new GraphMapperException("Binding for class " + clazz + " missing");
        }
        Value value = argument.getValue();
        if (value instanceof ObjectValue) {
            return (T) binding.instantiateByValue((ObjectValue) value);
        } else if (value instanceof VariableReference) {
            return (T) binding.instantiate(env.getVariables().get(((VariableReference)value).getName()));
        } else {
            return null;
        }
    }

}
