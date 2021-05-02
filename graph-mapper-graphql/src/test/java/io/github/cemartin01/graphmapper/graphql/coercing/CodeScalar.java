package io.github.cemartin01.graphmapper.graphql.coercing;

import graphql.schema.GraphQLScalarType;

public class CodeScalar {

    public static final GraphQLScalarType CODE_SCALAR = GraphQLScalarType.newScalar().name("Code").description("Code Scalar").coercing(new GraphqlCodeCoercing()).build();

    public static GraphQLScalarType get() {
        return CODE_SCALAR;
    }

}
