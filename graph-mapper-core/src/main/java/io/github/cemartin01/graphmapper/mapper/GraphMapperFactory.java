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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.cemartin01.graphmapper.MappingGraph;
import io.github.cemartin01.graphmapper.Node;
import io.github.cemartin01.graphmapper.classtree.ClassNode;

import lombok.AllArgsConstructor;

/**
 * Factory for new GraphMapper instances.
 */
@AllArgsConstructor
public class GraphMapperFactory {

   private final GraphMapperContext ctx;

   private final ReferenceFactory referenceFactory;

   public GraphMapperFactory(final GraphMapperContext ctx) {
      this.ctx = ctx;
      this.referenceFactory = new ReferenceFactory(ctx);
   }

   /**
    * Creates a GraphMapper that maps given source to given target class or any of target subclasses.
    * If you want to map a heterogeneous collection, pass the parent of of class hierarchy.
    * @param mappingGraph template for Graph Mapper
    * @param rootTargetClass root of a target class hierarchy
    * @param <T> target class typing
    * @return GraphMapper typed to target class
    */
   public <T> GraphMapper<T> getGraphMapper(MappingGraph<?> mappingGraph, Class<T> rootTargetClass) {
      return new GraphMapper<>(ctx, rootTargetClass, getChildren(mappingGraph.getRoot(), rootTargetClass));
   }

   private Map<Class<?>, ClassMapping> getChildren(Node<?> parentNode, Class<?> rootTargetClass) {
      Map<Class<?>, ClassMapping> mappings;

      ClassMapping rootMapping = getChildrenForClass(Collections.emptyList(), parentNode, rootTargetClass);

      ClassNode classNode = ctx.getClassNode(rootTargetClass);

      if (classNode == null) {
         mappings = Collections.singletonMap(RootMapping.class, rootMapping);
      } else {
         mappings = new HashMap<>();
         populateClassMappings(mappings, parentNode, classNode, rootMapping.getReferences());
      }

      return Collections.unmodifiableMap(mappings);
   }

   private void populateClassMappings(Map<Class<?>, ClassMapping> mappings, Node<?> node,
                                      ClassNode parentClassNode, List<Reference> superReferences) {
      for (ClassNode classNode: parentClassNode.getChildren()) {
         ClassMapping subclassMapping = getChildrenForClass(superReferences, node, classNode.getTargetClass());
         mappings.put(classNode.getSourceClass(), subclassMapping);
         populateClassMappings(mappings, node, classNode, subclassMapping.getReferences());
      }
   }

   private ClassMapping getChildrenForClass(List<Reference> superReferences, Node<?> parentNode,
                                            Class<?> currentTargetClass) {
      List<Reference> references = new ArrayList<>(superReferences);
      List<ReferenceTemplate> templates = ctx.getReferenceTemplates(currentTargetClass);
      if (templates != null) {
         parentNode.getChildren().forEach(node -> {
            Optional<ReferenceTemplate> selected = templates.stream()
                     .filter(t -> t.nodeLabel == node.getLabel())
                     .findFirst();
            if (selected.isPresent()) {
               ReferenceTemplate template = selected.get();
               Map<Class<?>, ClassMapping> classMappings = getChildren(node, template.getNodeMapperTemplate().getTargetClass());
               Reference reference = referenceFactory.getReference(template, classMappings);
               references.add(reference);
            }
         });
      }

      return new ClassMapping(currentTargetClass, references);
   }

}