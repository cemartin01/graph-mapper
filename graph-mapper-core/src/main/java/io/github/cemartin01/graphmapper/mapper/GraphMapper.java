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

import io.github.cemartin01.graphmapper.exception.GraphMapperException;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * GraphMapper is a component built for a specific mapping use case.
 * It's stateless and thread-safe.
 */
@AllArgsConstructor
public class GraphMapper<T> {

   private final GraphMapperContext ctx;

   private final Class<T> targetClass;

   private final Map<Class<?>, ClassMapping> classMappings;

   /**
    * Maps given source to given target class or any of target subclasses.
    * @param source source to be mapped
    * @throws GraphMapperException if mapping fails
    * @return new Instance of target class.
    */
   @SuppressWarnings({"unchecked"})
   public T map(Object source) {
      try {
         if (classMappings.size() == 1) {
            Object target = ctx.map(source, targetClass);
            for (Reference node: classMappings.get(RootMapping.class).getReferences()) {
               node.getSetter().accept(target, node.getNodeMapper().map(source));
            }
            return (T) target;
         } else {
            ClassMapping mapping = classMappings.get(source.getClass());
            Object target = ctx.map(source, mapping.getTargetClass());
            for (Reference node: mapping.getReferences()) {
               node.getSetter().accept(target, node.getNodeMapper().map(source));
            }
            return (T) target;
         }
      } catch (Throwable e) {
         throw new GraphMapperException("Mapping failed for instance of " + source.getClass(), e);
      }
   }

}