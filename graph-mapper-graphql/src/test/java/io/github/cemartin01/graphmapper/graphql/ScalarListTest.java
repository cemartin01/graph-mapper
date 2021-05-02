package io.github.cemartin01.graphmapper.graphql;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScalarListTest {

    @Test
    public void allScalarsAreReturned() {
        ScalarList list = new ScalarList(new GraphQLScalarType[]{Scalars.GraphQLByte});
        assertEquals(6, list.getAllScalars().size());
    }

}
