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
package io.github.cemartin01.graphmapper.mapper;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.cemartin01.graphmapper.NodeLabel;
import io.github.cemartin01.graphmapper.classtree.ClassNode;
import io.github.cemartin01.graphmapper.classtree.ClassTree;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class GraphMapperContext {

   //DTO class, list of RelationshipTemplates
   protected final Map<Class<?>, List<ReferenceTemplate>> referenceMap = new HashMap<>();

   //DTO class, list of DTO classes
   protected final Map<Class<?>, ClassNode> classTreeNodes = new HashMap<>();

   protected final Map<Class<?>, Function> mappers = new HashMap<>(17, 0.3f);

   @Getter
   protected final UnproxyFunction unproxyFunction;

   public GraphMapperContext(UnproxyFunction unproxyFunction) {
      this.unproxyFunction = unproxyFunction;
   }

   public <DTO> void addMapper(Function<Object, DTO> mapper, Class<DTO> dtoClass) {
      mappers.put(dtoClass, mapper);
   }

   public <DTO, E> Binding<DTO, E> addMapping(Class<DTO> dtoClass, Class<E> entityClass) {
      return new Binding<>(dtoClass, entityClass);
   }

   public void defineClassHierarchy(Class<?> dtoClass, Class<?> entityClass, ClassNode...classNodes) {
      ClassTree tree = ClassTree.of(dtoClass, entityClass, classNodes);
      classTreeNodes.put(dtoClass, tree.getRoot());
      populateClassTrees(tree.getRoot());
   }

   public void defineInterface(Class<?> dtoInterface, Class<?> entityClass, ClassNode...classNodes)
            throws GraphMapperInitializationException {
      if (!dtoInterface.isInterface()) {
         throw new GraphMapperInitializationException(dtoInterface + " is not an interface");
      }
      if (!Modifier.isAbstract(entityClass.getModifiers())) {
         throw new GraphMapperInitializationException(entityClass + " is not an abstract class");
      }
      defineClassHierarchy(dtoInterface, entityClass, classNodes);
   }

   public void defineUnion(Class<?> dtoInterface, Class<?> entityClass, ClassNode...classNodes)
            throws GraphMapperInitializationException {
      if (!dtoInterface.isInterface()) {
         throw new GraphMapperInitializationException(dtoInterface + " is not an interface");
      }
      if (dtoInterface.getMethods().length != 0) {
         throw new GraphMapperInitializationException(dtoInterface + " declares a method, so it is not a GraphQL union model");
      }
      if (!Modifier.isAbstract(entityClass.getModifiers())) {
         throw new GraphMapperInitializationException(entityClass + " is not an abstract class");
      }
      defineClassHierarchy(dtoInterface, entityClass, classNodes);
   }

   private void populateClassTrees(ClassNode currentNode) {
      currentNode.getChildren().forEach(node -> {
         if (!node.getChildren().isEmpty()) {
            classTreeNodes.put(node.getDtoClass(), node);
            populateClassTrees(node);
         }
      });
   }

   public ClassNode getClassNode(Class<?> clazz) {
      return classTreeNodes.get(clazz);
   }

   public Object unproxy(Object object) {
      return unproxyFunction.unproxy(object);
   }

   public Object map(Object source, Class<?> destinationClass) {
      return mappers.get(destinationClass).apply(source);
   }

   public List<ReferenceTemplate> getReferenceTemplates(Class<?> clazz) {
      return referenceMap.get(clazz);
   }

   @RequiredArgsConstructor
   public class Binding<DTO, E> {

      protected final Class<DTO> parentDTOClass;
      protected final Class<E> parentEntityClass;

      public <V> Binding<DTO, E> bind(NodeLabel nodeLabel) throws GraphMapperInitializationException {

         String capitalizedFieldName = getCapitalizedFieldName(nodeLabel);

         Method setter = findMethod(parentDTOClass, "set" + capitalizedFieldName);
         Class<?> childDTOClass = setter.getParameterTypes()[0];
         BiConsumer setterLambda = createSetterMethod(setter);

         Method getter = findMethod(parentEntityClass, "get" + capitalizedFieldName);
         Function getterLambda = createGetterMethod(getter);

         referenceMap.computeIfAbsent(parentDTOClass, clazz -> new ArrayList<>())
                  .add(new ReferenceTemplate(nodeLabel, setterLambda, new NodeMapperTemplate(getterLambda, childDTOClass,
                           NodeMapperTemplate.ReferenceType.OBJECT)));
         return this;
      }

      public <V> Binding<DTO, E> bindList(NodeLabel nodeLabel) throws GraphMapperInitializationException {

         return bindCollection(nodeLabel, NodeMapperTemplate.ReferenceType.LIST);

      }

      public <V> Binding<DTO, E> bindSet(NodeLabel nodeLabel) throws GraphMapperInitializationException {

         return bindCollection(nodeLabel, NodeMapperTemplate.ReferenceType.SET);

      }

      private <V> Binding<DTO, E> bindCollection(NodeLabel nodeLabel,
                                                 NodeMapperTemplate.ReferenceType referenceType
      ) throws GraphMapperInitializationException {

         String capitalizedFieldName = getCapitalizedFieldName(nodeLabel);

         Method setter = findMethod(parentDTOClass, "set" + capitalizedFieldName);
         ParameterizedType list = (ParameterizedType) setter.getGenericParameterTypes()[0];
         Class<?> childDTOClass = (Class<?>)list.getActualTypeArguments()[0];
         BiConsumer setterLambda = createSetterMethod(setter);

         Method getter = findMethod(parentEntityClass, "get" + capitalizedFieldName);
         Function getterLambda = createGetterMethod(getter);

         referenceMap.computeIfAbsent(parentDTOClass, clazz -> new ArrayList<>())
                  .add(new ReferenceTemplate(nodeLabel, setterLambda,
                           new NodeMapperTemplate(getterLambda, childDTOClass, referenceType)));

         return this;

      }

      private Method findMethod(Class<?> clazz, String name) throws GraphMapperInitializationException {
         return Arrays.stream(clazz.getMethods())
                  .filter(method -> method.getName().equals(name))
                  .findFirst()
                  .orElseThrow(() -> new GraphMapperInitializationException("Method " + name + " not found in " + clazz));
      }

      private Function createGetterMethod(Method getter) throws GraphMapperInitializationException {
         try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle getterHandle = lookup.unreflect(getter);
            CallSite callSite = LambdaMetafactory.metafactory(
                     lookup,
                     "apply",
                     MethodType.methodType(Function.class),
                     MethodType.methodType(Object.class, Object.class),
                     getterHandle,
                     MethodType.methodType(getter.getReturnType(), parentEntityClass)
            );
            return (Function) callSite.getTarget().invokeExact();
         } catch (Throwable t) {
            throw new GraphMapperInitializationException("Could not create lambda for getter " + getter);
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

      private String getCapitalizedFieldName(NodeLabel nodeLabel) {
         return nodeLabel.getName().substring(0,1).toUpperCase() +
                  (nodeLabel.getName().length() > 1 ? nodeLabel.getName().substring(1) : "");
      }

   }

}