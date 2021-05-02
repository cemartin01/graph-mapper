package io.github.cemartin01.graphmapper.graphql.mock;

import io.github.cemartin01.graphmapper.NodeLabel;

public enum GraphQLTestNodeLabel implements NodeLabel {

    MEAL("meal"),

    CHILD_A("childA"),
    CHILD_B("childB"),
    CHILD_A_A("childA_A"),
    CHILD_A_B("childA_B"),
    CHILD_B_A("childC_A");

    private final String name;

    GraphQLTestNodeLabel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
