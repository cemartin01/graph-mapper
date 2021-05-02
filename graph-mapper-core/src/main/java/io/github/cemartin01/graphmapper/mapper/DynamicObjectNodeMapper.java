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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

/**
 * Object Node Mapper for a Class Hierarchy
 */
@RequiredArgsConstructor
public class DynamicObjectNodeMapper implements NodeMapper {

   private final GraphMapperContext ctx;

   private final Function getter;

   private final Map<Class<?>, ClassMapping> classMappings;

   @Override
   public Object map(Object parentEntity) throws InvocationTargetException, IllegalAccessException {
      Object currentEntity = getter.apply(parentEntity);
      if (currentEntity == null) {
         return null;
      }
      Object unwrappedEntity = ctx.unproxy(currentEntity);
      ClassMapping classMapping = classMappings.get(unwrappedEntity.getClass());
      Object dto = ctx.map(unwrappedEntity, classMapping.getDtoClass());
      for (Reference child: classMapping.getReferences()) {
         child.getSetter().accept(dto, child.getNodeMapper().map(unwrappedEntity));
      }
      return dto;
   }

}