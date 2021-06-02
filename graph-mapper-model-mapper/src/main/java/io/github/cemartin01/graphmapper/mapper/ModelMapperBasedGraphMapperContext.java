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

import io.github.cemartin01.graphmapper.NodeLabel;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;
import lombok.Getter;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.SourceGetter;

/**
 * {@inheritDoc}
 *
 * Model-mapper extension of the context.
 * It associates binding of conditional fields and model-mapper conditional mapping.
 */
public class ModelMapperBasedGraphMapperContext extends GraphMapperContext {

   @Getter
   private final ModelMapper modelMapper;

   private final Condition<?, ?> skipMappingFunction = (context) -> false;

   public ModelMapperBasedGraphMapperContext(ModelMapper modelMapper, UnproxyFunction unproxyFunction) {
      super(unproxyFunction);
      this.modelMapper = modelMapper;
   }

   /**
    * {@inheritDoc}
    */
   public <T, S> ModelMapperBinding<T, S> addMapping(Class<T> targetClass, Class<S> sourceClass) {
      return new ModelMapperBinding<>(targetClass, sourceClass);
   }

   public Object map(Object source, Class<?> targetClass) {
      return modelMapper.map(source, targetClass);
   }

   public class ModelMapperBinding<T, S> extends Binding<T, S> {

      public ModelMapperBinding(Class<T> parentTargetClass, Class<S> parentSourceClass) {
         super(parentTargetClass, parentSourceClass);
      }

      /**
       * {@inheritDoc}
       * @param getterFun setter of a target class to skip
       * @param setterFun getter of a source class to skip
       */
      public <V> ModelMapperBinding<T, S> bind(NodeLabel nodeLabel, SourceGetter<S> getterFun,
                                               DestinationSetter<T, V> setterFun
      ) throws GraphMapperInitializationException {
         super.bind(nodeLabel);
         skipMapping(getterFun, setterFun);
         return this;
      }

      /**
       * {@inheritDoc}
       * @param getterFun setter of a target class to skip
       * @param setterFun getter of a source class to skip
       */
      public <V> ModelMapperBinding<T, S> bindList(NodeLabel nodeLabel, SourceGetter<S> getterFun,
                                                   DestinationSetter<T, V> setterFun
      ) throws GraphMapperInitializationException {
         super.bindList(nodeLabel);
         skipMapping(getterFun, setterFun);
         return this;
      }

      /**
       * {@inheritDoc}
       * @param getterFun setter of a target class to skip
       * @param setterFun getter of a source class to skip
       */
      public <V> ModelMapperBinding<T, S> bindSet(NodeLabel nodeLabel, SourceGetter<S> getterFun,
                                                  DestinationSetter<T, V> setterFun
      ) throws GraphMapperInitializationException {
         super.bindSet(nodeLabel);
         skipMapping(getterFun, setterFun);
         return this;
      }

      /**
       * Applies model-mapper conditional mapping
       * @param targetClass a super class of a current target class
       * @param sourceClass a super class of a current source class
       */
      public void includeBaseBinding(Class<? super T> targetClass, Class<? super S> sourceClass) {
         modelMapper.typeMap(parentSourceClass, parentTargetClass)
                 .includeBase(sourceClass, targetClass);
      }

      private <V> void skipMapping(SourceGetter<S> getterFun, DestinationSetter<T, V> setterFun) {
         modelMapper.typeMap(parentSourceClass, parentTargetClass).addMappings(
                 m -> m.when(skipMappingFunction).map(getterFun, setterFun)
         );
      }

   }

}