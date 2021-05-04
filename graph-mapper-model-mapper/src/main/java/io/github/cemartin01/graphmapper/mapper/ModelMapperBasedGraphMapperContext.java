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
   public <DTO, E> ModelMapperBinding<DTO, E> addMapping(Class<DTO> dtoClass, Class<E> entityClass) {
      return new ModelMapperBinding<>(dtoClass, entityClass);
   }

   public Object map(Object source, Class<?> destinationClass) {
      return modelMapper.map(source, destinationClass);
   }

   public class ModelMapperBinding<DTO, E> extends Binding<DTO, E> {

      public ModelMapperBinding(Class<DTO> parentDTOClass, Class<E> parentEntityClass) {
         super(parentDTOClass, parentEntityClass);
      }

      /**
       * {@inheritDoc}
       * @param getterFun setter of a DTO class to skip
       * @param setterFun getter of an entity class to skip
       */
      public <V> ModelMapperBinding<DTO, E> bind(NodeLabel nodeLabel, SourceGetter<E> getterFun,
                                      DestinationSetter<DTO, V> setterFun
      ) throws GraphMapperInitializationException {
         super.bind(nodeLabel);
         skipMapping(getterFun, setterFun);
         return this;
      }

      /**
       * {@inheritDoc}
       * @param getterFun setter of a DTO class to skip
       * @param setterFun getter of an entity class to skip
       */
      public <V> ModelMapperBinding<DTO, E> bindList(NodeLabel nodeLabel, SourceGetter<E> getterFun,
                                          DestinationSetter<DTO, V> setterFun
      ) throws GraphMapperInitializationException {
         super.bindList(nodeLabel);
         skipMapping(getterFun, setterFun);
         return this;
      }

      /**
       * {@inheritDoc}
       * @param getterFun setter of a DTO class to skip
       * @param setterFun getter of an entity class to skip
       */
      public <V> ModelMapperBinding<DTO, E> bindSet(NodeLabel nodeLabel, SourceGetter<E> getterFun,
                                          DestinationSetter<DTO, V> setterFun
      ) throws GraphMapperInitializationException {
         super.bindSet(nodeLabel);
         skipMapping(getterFun, setterFun);
         return this;
      }

      /**
       * Applies model-mapper conditional mapping
       * @param dtoClass a super class of a current DTO class
       * @param entityClass a super class of a current entity class
       */
      public void includeBaseBinding(Class<? super DTO> dtoClass, Class<? super E> entityClass) {
         modelMapper.typeMap(parentEntityClass, parentDTOClass)
                 .includeBase(entityClass, dtoClass);
      }

      private <V> void skipMapping(SourceGetter<E> getterFun, DestinationSetter<DTO, V> setterFun) {
         modelMapper.typeMap(parentEntityClass, parentDTOClass).addMappings(
                 m -> m.when(skipMappingFunction).map(getterFun, setterFun)
         );
      }

   }

}