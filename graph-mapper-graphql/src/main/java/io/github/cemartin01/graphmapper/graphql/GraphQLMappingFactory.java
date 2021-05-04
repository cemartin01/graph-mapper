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

import graphql.language.*;
import graphql.schema.DataFetchingEnvironment;
import io.github.cemartin01.graphmapper.MappingGraph;
import io.github.cemartin01.graphmapper.Node;
import io.github.cemartin01.graphmapper.NodeLabel;
import io.github.cemartin01.graphmapper.exception.GraphMapperException;
import io.github.cemartin01.graphmapper.graphql.argument.ArgumentBindingContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory for a GraphQL mapping.
 */
public class GraphQLMappingFactory {

   private final Map<String, NodeLabel> nodeLabelMap = new HashMap<>();

   private final ArgumentBindingContext bindingFactory;

   public GraphQLMappingFactory(NodeLabel[] nodeLabels, ArgumentBindingContext bindingFactory) {
      fillNodeLabelMap(Arrays.asList(nodeLabels));
      this.bindingFactory = bindingFactory;
   }

   public GraphQLMappingFactory(List<NodeLabel> nodeLabels, ArgumentBindingContext bindingFactory) {
      fillNodeLabelMap(nodeLabels);
      this.bindingFactory = bindingFactory;
   }

   private void fillNodeLabelMap(List<NodeLabel> nodeLabels) {
      for(NodeLabel label: nodeLabels) {
         nodeLabelMap.put(label.getName(), label);
      }
   }

   <T> T instantiateArgument(Argument argument, Class<T> clazz, DataFetchingEnvironment env) {
      return bindingFactory.instantiateArgument(argument, clazz, env);
   }

   /**
    * Builds instance of GraphQL mapping that contains a mapping graph based on passed Data fetching environment.
    * @param env Data fetching environment of current GraphQL query or mutation
    * @return instance of GraphQL mapping
    */
   public GraphQLMapping getMapping(DataFetchingEnvironment env) {
      RawNode root = RawNode.of(Node.RootNodeLabel.ROOT, getRootNodeChildren(env.getField(), env), Collections.singletonList(env.getField()));
      RawMappingGraph rawGraph = RawMappingGraph.of(root);
      MappingGraph<GraphQLMetadata> graph = rawGraph.getMappingGraph(env, this);
      return new GraphQLMapping(graph);
   }

   private List<RawNode> getRootNodeChildren(Field parentField, DataFetchingEnvironment env) {
      return parentField.getSelectionSet().getSelections().stream()
               .map(selection -> getChildNodes(selection, env))
               .flatMap(Collection::stream)
               .collect(Collectors.toList());
   }

   private List<RawNode> getChildNodes(Selection<?> parentSelection, DataFetchingEnvironment env) {
      List<RawNode> nodes = null;
      if (parentSelection instanceof Field) {
         Field parentField = (Field)parentSelection;
         NodeLabel currentLabel = nodeLabelMap.get(parentField.getName());
         if (currentLabel != null) {
            List<RawNode> children = newNodeList();
            parentField.getSelectionSet().getSelections().forEach(selection -> {
               children.addAll(getChildNodes(selection, env));
            });
            nodes = newNodeList();
            nodes.add(RawNode.of(currentLabel, children, Collections.singletonList(parentField)));
         } else {
            nodes = Collections.emptyList();
         }
      } else if (parentSelection instanceof InlineFragment) {
         InlineFragment inlineFragment = (InlineFragment) parentSelection;
         nodes = newNodeList();
         for (Selection<?> selection : inlineFragment.getSelectionSet().getSelections()) {
            nodes.addAll(getChildNodes(selection, env));
         }
      } else if (parentSelection instanceof FragmentSpread) {
         FragmentSpread fragmentSpread = (FragmentSpread) parentSelection;
         FragmentDefinition fragmentDefinition = env.getFragmentsByName().get(fragmentSpread.getName());
         if (fragmentDefinition == null) {
            throw new GraphMapperException("Fragment definition for " + fragmentSpread.getName() + " is missing");
         }
         nodes = newNodeList();
         for (Selection<?> selection : fragmentDefinition.getSelectionSet().getSelections()) {
            nodes.addAll(getChildNodes(selection, env));
         }
      }
      return nodes;
   }

   private List<RawNode> newNodeList() {
      return new ArrayList<>(3);
   }

}