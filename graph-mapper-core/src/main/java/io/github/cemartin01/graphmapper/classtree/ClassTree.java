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
package io.github.cemartin01.graphmapper.classtree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassTree {

   public final ClassNode root;

   public static ClassTree of(final Class<?> dtoClass, final Class<?> entityClass, ClassNode...subClasses) {
      return new ClassTree(ClassNode.of(dtoClass, entityClass, subClasses));
   }

}