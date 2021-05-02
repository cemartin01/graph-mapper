package io.github.cemartin01.graphmapper.graphql.schema;

import io.github.cemartin01.graphmapper.NodeLabel;

public enum GraphQLSchemaNodeLabel implements NodeLabel {

    MEAL("meal"),
    RECIPE("recipe");

    private final String name;

    GraphQLSchemaNodeLabel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
