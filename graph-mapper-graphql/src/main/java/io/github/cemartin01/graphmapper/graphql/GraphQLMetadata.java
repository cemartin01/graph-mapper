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
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

/**
 * Metadata of a mapping graph node that contains GraphQL-java specific data as associated fields.
 *
 * It provides a way to instantiate object arguments that have been previously set up using
 * {@link io.github.cemartin01.graphmapper.graphql.argument.ArgumentBindingContext#addBinding}
 */
@Getter
@AllArgsConstructor
public class GraphQLMetadata {

    private final List<Field> fields;

    private final DataFetchingEnvironment env;

    private final GraphQLMappingFactory factory;

    /*
    getScalarArgument(String name)
     */

    /**
     * Obtains a new instance of a given class based on an argument of a given name.
     * @param name name of an argument associated with GrahpQL-java field
     * @param argClass class that should be used to bind the argument and it values
     * @return Optional value, empty if argument is missing
     */
    public <T> Optional<T> getObjectArgument(String name, Class<T> argClass) {
        return fields.get(0).getArguments().stream()
                .filter(arg -> arg.getName().equals(name))
                .findFirst()
                .map(arg -> factory.instantiateArgument(arg, argClass, env));
    }

}
