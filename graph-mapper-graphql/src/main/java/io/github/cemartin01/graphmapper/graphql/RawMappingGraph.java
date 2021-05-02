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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import io.github.cemartin01.graphmapper.MappingGraph;
import io.github.cemartin01.graphmapper.Node;

import lombok.Getter;

/**
 * For internal use only
 */
@Getter
class RawMappingGraph {

   private final RawNode root;

   private boolean cleared = false;

   private final Map<String, List<RawNode>> redundantNodes = new HashMap<>(17);

   private RawMappingGraph(final RawNode root) {
      this.root = root;
   }

   public static RawMappingGraph of(RawNode root) {
      return new RawMappingGraph(root);
   }

   /**
    * Cleans the redundant nodes
    * @return MappingGraph without redundant nodes
    */
   public MappingGraph<GraphQLMetadata> getMappingGraph(DataFetchingEnvironment env, GraphQLMappingFactory factory) {
      if (!cleared) {
         cleanRedundantNodes(root);
         cleared = true;
      }
      return buildMappingGraph(env, factory);
   }

   private void cleanRedundantNodes(RawNode cleanedNode) {
      if (cleanedNode.getChildren().size() > 1) {

         cleanedNode.getChildren().forEach(child -> {
            redundantNodes.computeIfAbsent(child.getLabel().getName(), name -> new ArrayList<>(1))
                     .add(child);
         });
         cleanedNode.getChildren().clear();
         cleanedNode.getChildren().addAll(redundantNodes.values().stream().map(nodes -> {
            if (nodes.size() > 1) {
               List<RawNode> allNodeChildren = nodes.stream().flatMap(node -> node.getChildren().stream())
                        .collect(Collectors.toList());
               List<Field> fields = nodes.stream().flatMap(n -> n.getFields().stream())
                       .collect(Collectors.toList());
               return RawNode.of(nodes.get(0).getLabel(), allNodeChildren, fields);
            } else {
               return nodes.get(0);
            }
         }).collect(Collectors.toList()));
      }

      redundantNodes.clear();
      cleanedNode.getChildren().forEach(this::cleanRedundantNodes);
   }

   private MappingGraph<GraphQLMetadata> buildMappingGraph(DataFetchingEnvironment env, GraphQLMappingFactory factory) {
      return MappingGraph.of(Node.of(root.getLabel(), new GraphQLMetadata(root.getFields(), env, factory), getNodeChildren(root, env, factory)));
   }

   private List<Node<GraphQLMetadata>> getNodeChildren(RawNode parentNode, DataFetchingEnvironment env, GraphQLMappingFactory factory) {
      return parentNode.getChildren().stream()
               .map(rawNode -> Node.of(rawNode.getLabel(), new GraphQLMetadata(rawNode.getFields(), env, factory), getNodeChildren(rawNode, env, factory)))
               .collect(Collectors.toList());
   }

}