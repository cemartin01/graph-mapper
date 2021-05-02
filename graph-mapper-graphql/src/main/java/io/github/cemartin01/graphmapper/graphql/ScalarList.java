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

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScalarList {

    private final List<GraphQLScalarType> scalars = new ArrayList<>();

    public ScalarList(GraphQLScalarType[] customScalarTypes) {
        scalars.add(Scalars.GraphQLBoolean);
        scalars.add(Scalars.GraphQLInt);
        scalars.add(Scalars.GraphQLFloat);
        scalars.add(Scalars.GraphQLString);
        scalars.add(Scalars.GraphQLID);
        scalars.addAll(Arrays.asList(customScalarTypes));
    }

    public List<GraphQLScalarType> getAllScalars() {
        return scalars;
    }

}
