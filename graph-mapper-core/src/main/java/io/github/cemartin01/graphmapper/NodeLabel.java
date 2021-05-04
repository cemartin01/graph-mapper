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

/**
 * Label for Mapping graph nodes.
 *
 * The name should reflect a field name of a mapped entity and DTO.
 * For example: Label for OrderEntity.items has name "items"
 *
 * Every {@link io.github.cemartin01.graphmapper.Node} must be labeled by an instance of node label
 * interface implementation.
 *
 * It's recommended to use an Enum to model the labels.
 */
public interface NodeLabel {

   String getName();

}