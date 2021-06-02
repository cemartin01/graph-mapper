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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CollectionNodeMapper {

   protected List<Object> mapToList(GraphMapperContext ctx, Collection<?> sourceCollection, Class<?> targetClass,
                                    List<Reference> references) throws InvocationTargetException, IllegalAccessException {
      if (sourceCollection == null) {
         return null;
      }
      List<Object> targetList = new ArrayList<>();
      for (Object currentSource: sourceCollection) {
         if (currentSource == null) {
            targetList.add(null);
            continue;
         }
         Object unwrappedSource = ctx.unproxy(currentSource);
         Object target = ctx.map(unwrappedSource, targetClass);
         for (Reference reference: references) {
            reference.getSetter().accept(target, reference.getNodeMapper().map(unwrappedSource));
         }
         targetList.add(target);
      }
      return targetList;
   }

}