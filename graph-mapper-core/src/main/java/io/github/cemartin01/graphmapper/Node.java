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

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * A node that describes a reference.
 *
 * It's a vertex of {@link io.github.cemartin01.graphmapper.MappingGraph}.
 *
 * Every node must be labeled by an implementation of {@link io.github.cemartin01.graphmapper.NodeLabel}.
 * It's typed to determine which class is used to model metadata. In cases when metadata are not needed, it's
 * possible to use Void for null values.
 */
@Getter
public class Node<T> {

   private final NodeLabel label;

   private final List<Node<T>> children;

   private final T metadata;

   @Builder
   private Node(NodeLabel label, @Singular("child") List<Node<T>> children, T metadata) {
      this.label = label == null ? RootNodeLabel.ROOT : label;
      this.children = ImmutableList.copyOf(children);
      this.metadata = metadata;
   }

   public static <U> Node<U> of(NodeLabel label, U metadata, List<Node<U>> children) {
      return new Node<>(label, ImmutableList.copyOf(children), metadata);
   }

   public static Node<Void> of(NodeLabel label) {
      return new Node<>(label, ImmutableList.of(), null);
   }

   /**
    * Default label for a root of graph
    */
   public enum RootNodeLabel implements NodeLabel {
      ROOT;

      @Override
      public String getName() {
         return "root";
      }

   }

}