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
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ListNodeMapper extends CollectionNodeMapper implements NodeMapper {

   private final GraphMapperContext ctx;

   private final Function getter;

   private final Class<?> targetClass;

   private final List<Reference> references;

   @Override
   public Object map(Object parentSource) throws InvocationTargetException, IllegalAccessException {
      List<?> currentSourceList = (List<?>) getter.apply(parentSource);
      return mapToList(ctx, currentSourceList, targetClass, references);
   }

}