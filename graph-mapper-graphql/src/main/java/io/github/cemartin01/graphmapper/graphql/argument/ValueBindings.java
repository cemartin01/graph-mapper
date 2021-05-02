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

import graphql.language.*;
import graphql.schema.Coercing;
import io.github.cemartin01.graphmapper.exception.GraphMapperException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

class ValueBindings {

    @AllArgsConstructor
    public static abstract class AbstractBinding<T> {
        protected final BiConsumer setter;

        public void set(T object, Object value) {
            setter.accept(object, value);
        }

        public void setList(T object, List list) {
            setter.accept(object, list);
        }

        public abstract void setByValue(T object, Value value);
        public abstract void setListByValue(T object, ArrayValue arrayValue);
    }

    public static class NestedInputBinding<T> extends AbstractBinding<T> {

        private final ArgumentBinding argumentBinding;

        public NestedInputBinding(BiConsumer setter, ArgumentBinding argumentBinding) {
            super(setter);
            this.argumentBinding = argumentBinding;
        }

        @Override
        public void set(T object, Object value) {
            setter.accept(object, argumentBinding.instantiate(value));
        }

        @Override
        public void setList(T object, List list) {
            List targetList = new ArrayList();
            for (Object item : list) {
                targetList.add(argumentBinding.instantiate(item));
            }
            setter.accept(object, targetList);
        }

        @Override
        public void setByValue(T object, Value value) {
            setter.accept(object, argumentBinding.instantiateByValue((ObjectValue) value));
        }

        @Override
        public void setListByValue(T object, ArrayValue arrayValue) {
            List targetList = new ArrayList();
            for (Value value : arrayValue.getValues()) {
                targetList.add(argumentBinding.instantiateByValue((ObjectValue) value));
            }
            setter.accept(object, targetList);
        }

    }

    public static class EnumBinding<T> extends AbstractBinding<T> {

        private final Function enumFactoryMethod;

        public EnumBinding(BiConsumer setter, Function enumFactoryMethod) {
            super(setter);
            this.enumFactoryMethod = enumFactoryMethod;
        }

        public void setByValue(T object, Value value) {
            setter.accept(object, getEnumValue(value));
        }

        @Override
        public void setListByValue(T object, ArrayValue arrayValue) {
            List list = new ArrayList<>();
            arrayValue.getValues().forEach(itemValue -> {
                Object e = getEnumValue(itemValue);
                if (e != null) {
                    list.add(e);
                }
            });
            setter.accept(object, list);
        }

        private Object getEnumValue(Value value) {
            if (value instanceof EnumValue) {
                return enumFactoryMethod.apply(((EnumValue) value).getName());
            } else {
                return null;
            }
        }

    }

    public static class CoercingBinding<T> extends AbstractBinding<T> {
        private final Coercing coercing;

        public CoercingBinding(BiConsumer setter, Coercing coercing) {
            super(setter);
            this.coercing = coercing;
        }

        @Override
        public void setByValue(T object, Value value) {
            setter.accept(object, getParsedValue(value));
        }

        @Override
        public void setListByValue(T object, ArrayValue arrayValue) {
            List list = new ArrayList<>();
            arrayValue.getValues().forEach(itemValue -> {
                Object e = getParsedValue(itemValue);
                if (e != null) {
                    list.add(e);
                }
            });
            setter.accept(object, list);
        }

        public Object getParsedValue(Value value) {
            if (value instanceof StringValue) {
                return coercing.parseValue(((StringValue) value).getValue());
            } else if (value instanceof IntValue) {
                return coercing.parseValue(((IntValue) value).getValue());
            } else if (value instanceof BooleanValue) {
                return coercing.parseValue(((BooleanValue) value).isValue());
            } else if (value instanceof FloatValue) {
                return coercing.parseValue(((FloatValue) value).getValue());
            } else if (value instanceof NullValue){
                return null;
            } else {
                throw new GraphMapperException("Unsupported value " + value.getClass());
            }
        }

    }

}
