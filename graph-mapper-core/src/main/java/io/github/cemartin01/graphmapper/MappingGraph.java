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
package io.github.cemartin01.graphmapper;

import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Specification for a GraphMapper instance
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MappingGraph<T> {

   private final Node<T> root;

   public static <U> MappingGraph<U> of(Node<U> root) {
      return new MappingGraph<>(root);
   }

   public Optional<Node<T>> getNodeByPath(List<NodeLabel> nodeLabels) {
      return getNodeByPath(root, nodeLabels);
   }

//   public MappingGraph<T> getSubgraph(List<NodeLabel> path) {
//
//   }

//   public MappingGraph<T> getSubgraphByTemplate() {
//
//   }

   public static <U> Optional<Node<U>> getNodeByPath(Node<U> rootNode, List<NodeLabel> nodeLabels) {
      if (nodeLabels.isEmpty()) {
         return Optional.of(rootNode);
      }
      return searchNode(rootNode, nodeLabels, 0);
   }

   private static <U> Optional<Node<U>> searchNode(Node<U> rootNode, List<NodeLabel> nodeLabels, int level) {
      for (Node<U> node: rootNode.getChildren()) {
         if (nodeLabels.get(level).equals(node.getLabel())) {
            if (level == nodeLabels.size() - 1) {
               return Optional.of(node);
            } else {
               return searchNode(node, nodeLabels, level + 1);
            }
         }
      }
      return Optional.empty();
   }

}