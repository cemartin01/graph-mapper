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
package io.github.cemartin01.graphmapper.graphql.argument;

import graphql.language.ArrayValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.Value;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@Builder
class ArgumentBinding<T> {

    private final Class<T> clazz;
    private final Supplier<T> constructor;
    private final Map<String, ValueBindings.AbstractBinding<T>> propertyMap;

    public T instantiateByValue(ObjectValue objectValue) {
        T object = constructor.get();
        for (ObjectField objectField : objectValue.getObjectFields()) {
            ValueBindings.AbstractBinding<T> binding = propertyMap.get(objectField.getName());
            if (binding == null) {
                continue;
            }
            Value currentValue = objectField.getValue();
            if (currentValue instanceof ArrayValue) {
                binding.setListByValue(object, (ArrayValue) currentValue);
            } else {
                binding.setByValue(object, currentValue);
            }
        }
        return object;
    }

    public T instantiate(Object variable) {
        T object = constructor.get();
        if (variable instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) variable;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                ValueBindings.AbstractBinding<T> binding = propertyMap.get(entry.getKey());
                if (binding == null) {
                    continue;
                }
                if (entry.getValue() instanceof List) {
                    binding.setList(object, (List) entry.getValue());
                } else {
                    binding.set(object, entry.getValue());
                }
            }
        }
        return object;
    }

}
