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

/**
 * A component that provides an interface to configure graph mapper behaviour
 *
 * It's possible to define 3 ways of class relationships during mapping:
 * <p>
 *     <ul>
 *         <li>class hierarchy mapping</li>
 *         <li>interface mapping</li>
 *         <li>typescript like union mapping</li>
 *     </ul>
 * </p>
 *
 * The conditional fields (references) of mapped classes are defined by {@link #addMapping} method.
 *
 * The mappers of unconditional fields are added by {@link #addMapper} method.
 */
public class GraphMapperContext {

   //Target class, list of RelationshipTemplates
   protected final Map<Class<?>, List<ReferenceTemplate>> referenceMap = new HashMap<>();

   //Target class
   protected final Map<Class<?>, ClassNode> classTreeNodes = new HashMap<>();

   protected final Map<Class<?>, Function> mappers = new HashMap<>(17, 0.3f);

   @Getter
   protected final UnproxyFunction unproxyFunction;

   public GraphMapperContext(UnproxyFunction unproxyFunction) {
      this.unproxyFunction = unproxyFunction;
   }

   public <T> void addMapper(Function<Object, T> mapper, Class<T> targetClass) {
      mappers.put(targetClass, mapper);
   }

   public <T, S> Binding<T, S> addMapping(Class<T> targetClass, Class<S> sourceClass) {
      return new Binding<>(targetClass, sourceClass);
   }

   /**
    * Defines a class hierarchy to properly handle conditional mapping in the scope of class hierarchy
    * @param targetClass base target class
    * @param sourceClass base source class
    * @param classNodes list of subclasses
    */
   public void defineClassHierarchy(Class<?> targetClass, Class<?> sourceClass, ClassNode...classNodes) {
      ClassTree tree = ClassTree.of(targetClass, sourceClass, classNodes);
      classTreeNodes.put(targetClass, tree.getRoot());
      populateClassTrees(tree.getRoot());
   }

   /**
    * Defines interface based mapping
    * @param targetInterface a interface for target classes
    * @param sourceClass an abstract source class
    * @param classNodes list of subclasses
    * @throws GraphMapperInitializationException if validation of classes fails
    */
   public void defineInterface(Class<?> targetInterface, Class<?> sourceClass, ClassNode...classNodes)
            throws GraphMapperInitializationException {
      if (!targetInterface.isInterface()) {
         throw new GraphMapperInitializationException(targetInterface + " is not an interface");
      }
      if (!Modifier.isAbstract(sourceClass.getModifiers())) {
         throw new GraphMapperInitializationException(sourceClass + " is not an abstract class");
      }
      defineClassHierarchy(targetInterface, sourceClass, classNodes);
   }

   /**
    * Defines a typescript like union mapping.
    * @param targetInterface empty interface representing union in Java
    * @param sourceClass abstract source class
    * @param classNodes list of subclasses
    * @throws GraphMapperInitializationException if validation of classes fails
    */
   public void defineUnion(Class<?> targetInterface, Class<?> sourceClass, ClassNode...classNodes)
            throws GraphMapperInitializationException {
      if (!targetInterface.isInterface()) {
         throw new GraphMapperInitializationException(targetInterface + " is not an interface");
      }
      if (targetInterface.getMethods().length != 0) {
         throw new GraphMapperInitializationException(targetInterface + " declares a method, so it is not a GraphQL union model");
      }
      if (!Modifier.isAbstract(sourceClass.getModifiers())) {
         throw new GraphMapperInitializationException(sourceClass + " is not an abstract class");
      }
      defineClassHierarchy(targetInterface, sourceClass, classNodes);
   }

   private void populateClassTrees(ClassNode currentNode) {
      currentNode.getChildren().forEach(node -> {
         if (!node.getChildren().isEmpty()) {
            classTreeNodes.put(node.getTargetClass(), node);
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

   public Object map(Object source, Class<?> targetClass) {
      return mappers.get(targetClass).apply(source);
   }

   public List<ReferenceTemplate> getReferenceTemplates(Class<?> clazz) {
      return referenceMap.get(clazz);
   }

   /**
    * Describes a binding between a target class and an source class.
    *
    * Provides a way to further bind source class getter and target class setter together to a given node label.
    *
    * @param <T> A target class
    * @param <S> A source class
    */
   @RequiredArgsConstructor
   public class Binding<T, S> {

      protected final Class<T> parentTargetClass;
      protected final Class<S> parentSourceClass;

      /**
       * Binds a source class getter and target class setter based on node label name. The field will be mapped conditionally.
       * The field must be a reference, it will be mapped to a reference.
       *
       * @param nodeLabel node label of which the name is used to resolve getter and setter name
       * @return The same instance of binding to enable fluent calls
       * @throws GraphMapperInitializationException if it's not possible to resolve getter or setter method
       */
      public Binding<T, S> bind(NodeLabel nodeLabel) throws GraphMapperInitializationException {

         String capitalizedFieldName = getCapitalizedFieldName(nodeLabel);

         Method setter = findMethod(parentTargetClass, "set" + capitalizedFieldName);
         Class<?> childTargetClass = setter.getParameterTypes()[0];
         BiConsumer setterLambda = createSetterMethod(setter);

         Method getter = findMethod(parentSourceClass, "get" + capitalizedFieldName);
         Function getterLambda = createGetterMethod(getter);

         referenceMap.computeIfAbsent(parentTargetClass, clazz -> new ArrayList<>())
                  .add(new ReferenceTemplate(nodeLabel, setterLambda, new NodeMapperTemplate(getterLambda, childTargetClass,
                           NodeMapperTemplate.ReferenceType.OBJECT)));
         return this;
      }

      /**
       * Binds a source class getter and target class setter based on node label name. The field will be mapped conditionally.
       * The field must be a list reference, it will be mapped to a list reference.
       *
       * @param nodeLabel node label of which the name is used to resolve getter and setter name
       * @return The same instance of binding to enable fluent calls
       * @throws GraphMapperInitializationException if it's not possible to resolve getter or setter method
       */
      public Binding<T, S> bindList(NodeLabel nodeLabel) throws GraphMapperInitializationException {

         return bindCollection(nodeLabel, NodeMapperTemplate.ReferenceType.LIST);

      }

      /**
       * Binds a source class getter and target class setter based on node label name. The field will be mapped conditionally.
       * The field must be a set reference, it will be mapped to a list reference.
       *
       * @param nodeLabel node label of which the name is used to resolve getter and setter name
       * @return The same instance of binding to enable fluent calls
       * @throws GraphMapperInitializationException if it's not possible to resolve getter or setter method
       */
      public Binding<T, S> bindSet(NodeLabel nodeLabel) throws GraphMapperInitializationException {

         return bindCollection(nodeLabel, NodeMapperTemplate.ReferenceType.SET);

      }

      private Binding<T, S> bindCollection(NodeLabel nodeLabel,
                                           NodeMapperTemplate.ReferenceType referenceType
      ) throws GraphMapperInitializationException {

         String capitalizedFieldName = getCapitalizedFieldName(nodeLabel);

         Method setter = findMethod(parentTargetClass, "set" + capitalizedFieldName);
         ParameterizedType list = (ParameterizedType) setter.getGenericParameterTypes()[0];
         Class<?> childTargetClass = (Class<?>)list.getActualTypeArguments()[0];
         BiConsumer setterLambda = createSetterMethod(setter);

         Method getter = findMethod(parentSourceClass, "get" + capitalizedFieldName);
         Function getterLambda = createGetterMethod(getter);

         referenceMap.computeIfAbsent(parentTargetClass, clazz -> new ArrayList<>())
                  .add(new ReferenceTemplate(nodeLabel, setterLambda,
                           new NodeMapperTemplate(getterLambda, childTargetClass, referenceType)));

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
                     MethodType.methodType(getter.getReturnType(), parentSourceClass)
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