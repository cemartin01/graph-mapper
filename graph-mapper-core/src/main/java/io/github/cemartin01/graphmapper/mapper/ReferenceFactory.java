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

import java.util.List;
import java.util.Map;

import io.github.cemartin01.graphmapper.exception.GraphMapperException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class ReferenceFactory {

   private final GraphMapperContext ctx;

   public Reference getReference(ReferenceTemplate template, Map<Class<?>, ClassMapping> classMappings) {
      NodeMapper nodeMapper;
      NodeMapperTemplate nodeMapperTemplate = template.getNodeMapperTemplate();
      if (classMappings.size() == 1) {
         List<Reference> rootReferences = classMappings.get(RootMapping.class).getReferences();
         nodeMapper = buildNodeMapper(nodeMapperTemplate, rootReferences);
      } else {
         nodeMapper = buildDynamicNodeMapper(nodeMapperTemplate, classMappings);
      }
      return new Reference(template.getSetter(), nodeMapper);
   }

   private NodeMapper buildNodeMapper(NodeMapperTemplate nodeMapperTemplate, List<Reference> rootReferences) {
      switch (nodeMapperTemplate.getReferenceType()) {
         case OBJECT: return new ObjectNodeMapper(ctx, nodeMapperTemplate.getGetter(),
                  nodeMapperTemplate.getTargetClass(), rootReferences);
         case LIST: return new ListNodeMapper(ctx, nodeMapperTemplate.getGetter(),
                  nodeMapperTemplate.getTargetClass(), rootReferences);
         case SET: return new SetNodeMapper(ctx, nodeMapperTemplate.getGetter(),
                  nodeMapperTemplate.getTargetClass(), rootReferences);
         default: throw new GraphMapperException("Unknown reference type");
      }
   }

   private NodeMapper buildDynamicNodeMapper(NodeMapperTemplate nodeMapperTemplate,
                                             Map<Class<?>, ClassMapping> classMappings) {
      switch (nodeMapperTemplate.getReferenceType()) {
         case OBJECT: return new DynamicObjectNodeMapper(ctx, nodeMapperTemplate.getGetter(), classMappings);
         case LIST:  return new HeterogeneousListNodeMapper(ctx, nodeMapperTemplate.getGetter(), classMappings);
         case SET: return new HeterogeneousSetNodeMapper(ctx, nodeMapperTemplate.getGetter(), classMappings);
         default: throw new GraphMapperException("Unknown reference type");
      }
   }

}