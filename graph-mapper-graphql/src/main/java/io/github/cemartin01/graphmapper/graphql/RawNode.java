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
package io.github.cemartin01.graphmapper.graphql;

import graphql.language.Field;
import io.github.cemartin01.graphmapper.NodeLabel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * For internal use only.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class RawNode {

   private final NodeLabel label;

   /**
    * Must be mutable due to applied algorithm of searching the duplicates
    * TODO fix the algorithm RawMappingGraph.cleanRedundantNodes
    */
   private final List<RawNode> children;

   private final List<Field> fields;

   /**
    * @param children - Must be mutable due to applied algorithm of searching the duplicates
    */
   public static RawNode of(NodeLabel label, List<RawNode> children, List<Field> fields) {
      return new RawNode(label, children, fields);
   }

}