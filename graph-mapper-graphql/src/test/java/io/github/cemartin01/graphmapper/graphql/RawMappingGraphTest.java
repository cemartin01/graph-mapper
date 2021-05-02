package io.github.cemartin01.graphmapper.graphql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import graphql.language.Field;
import io.github.cemartin01.graphmapper.MappingGraph;
import io.github.cemartin01.graphmapper.Node;
import io.github.cemartin01.graphmapper.graphql.mock.GraphQLTestNodeLabel;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.github.cemartin01.graphmapper.graphql.mock.GraphQLTestNodeLabel.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RawMappingGraphTest {

    @Test
    public void rawMappingGraphIsBuilt() {

        Field rootField = Field.newField().build();
        Field mealField = Field.newField().build();

        RawMappingGraph graph = RawMappingGraph.of(
                RawNode.of(Node.RootNodeLabel.ROOT,
                        ImmutableList.of(
                                RawNode.of(GraphQLTestNodeLabel.MEAL, Collections.emptyList(), ImmutableList.of(mealField))
                        ), ImmutableList.of(rootField)
                )
        );

        RawNode root = graph.getRoot();
        assertNotNull(root);
        assertEquals(Node.RootNodeLabel.ROOT, root.getLabel());
        assertEquals(1, root.getFields().size());
        assertEquals(1, root.getChildren().size());
        assertSame(rootField, root.getFields().get(0));
        RawNode mealNode = root.getChildren().get(0);
        assertNotNull(mealNode);
        assertEquals(GraphQLTestNodeLabel.MEAL, mealNode.getLabel());
        assertTrue(mealNode.getChildren().isEmpty());
        assertEquals(1, mealNode.getFields().size());
        assertSame(mealField, mealNode.getFields().get(0));

    }

    @Test
    public void nodesAreMerged() {

        Field rootField = Field.newField().name("rootField").build();

        Field childA_field = Field.newField().name("childA_field").build();
        Field childA_dup_field = Field.newField().name("childA_dup_field").build();
        Field childB_field = Field.newField().name("childB_field").build();

        Field childA_A_field = Field.newField().name("childA_A_field").build();
        Field childA_A_dup_field = Field.newField().name("childA_A_dup_field").build();
        Field childA_B_field = Field.newField().name("childA_B_field").build();

        Field childA_dup_childA_B_field = Field.newField().name("childA_dup_childA_B_field").build();
        Field childA_dup_childA_B_dup_field = Field.newField().name("childA_dup_childA_B_dup_field").build();
        Field childA_dup_childA_A_field = Field.newField().name("childA_dup_childA_A_field").build();

        Field childB_A_field = Field.newField().build();

        RawNode childARawNode = RawNode.of(CHILD_A, Lists.newArrayList(
                RawNode.of(CHILD_A_A, Lists.newArrayList(), Lists.newArrayList(childA_A_field)),
                RawNode.of(CHILD_A_A, Lists.newArrayList(), Lists.newArrayList(childA_A_dup_field)),
                RawNode.of(CHILD_A_B, Lists.newArrayList(), Lists.newArrayList(childA_B_field))
        ), Lists.newArrayList(childA_field));

        RawNode childARawNodeDuplicate = RawNode.of(CHILD_A, Lists.newArrayList(
                RawNode.of(CHILD_A_B, Lists.newArrayList(), Lists.newArrayList(childA_dup_childA_B_field)),
                RawNode.of(CHILD_A_B, Lists.newArrayList(), Lists.newArrayList(childA_dup_childA_B_dup_field)),
                RawNode.of(CHILD_A_A, Lists.newArrayList(), Lists.newArrayList(childA_dup_childA_A_field))
                ),
                Lists.newArrayList(childA_dup_field));

        RawNode childBRawNode = RawNode.of(GraphQLTestNodeLabel.CHILD_B, Lists.newArrayList(
                RawNode.of(GraphQLTestNodeLabel.CHILD_B_A, Lists.newArrayList(), Lists.newArrayList(childB_A_field))
        ), Lists.newArrayList(childB_field));

        RawMappingGraph rawMappingGraph =  RawMappingGraph.of(
                RawNode.of(Node.RootNodeLabel.ROOT,
                        Lists.newArrayList(childARawNode, childARawNodeDuplicate, childBRawNode),
                        Lists.newArrayList(rootField)
                )
        );

        MappingGraph<GraphQLMetadata> mappingGraph = rawMappingGraph.getMappingGraph(null, null);

        //ASSERTIONS

        Node<GraphQLMetadata> root = mappingGraph.getRoot();
        assertEquals(Node.RootNodeLabel.ROOT, root.getLabel());
        assertEquals(2, root.getChildren().size());
        assertEquals(1, root.getMetadata().getFields().size());
        assertEquals(rootField, root.getMetadata().getFields().get(0));

        //Child A

        Node<GraphQLMetadata> childA = root.getChildren().stream()
                .filter(child -> child.getLabel().equals(CHILD_A))
                .findFirst()
                .orElse(null);

        assertNotNull(childA);
        assertEquals(CHILD_A, childA.getLabel());
        assertEquals(2, childA.getChildren().size());
        assertEquals(2, childA.getMetadata().getFields().size());
        assertTrue(childA.getMetadata().getFields().stream().anyMatch(childA_field::equals));
        assertTrue(childA.getMetadata().getFields().stream().anyMatch(childA_dup_field::equals));

        //Child A children

        Node<GraphQLMetadata> childAA = childA.getChildren().stream()
                .filter(child -> child.getLabel().equals(CHILD_A_A))
                .findFirst()
                .orElse(null);

        assertNotNull(childAA);
        assertEquals(CHILD_A_A, childAA.getLabel());
        assertEquals(0, childAA.getChildren().size());
        assertEquals(3, childAA.getMetadata().getFields().size());
        assertTrue(childAA.getMetadata().getFields().stream().anyMatch(childA_A_field::equals));
        assertTrue(childAA.getMetadata().getFields().stream().anyMatch(childA_A_dup_field::equals));
        assertTrue(childAA.getMetadata().getFields().stream().anyMatch(childA_dup_childA_A_field::equals));

        Node<GraphQLMetadata> childAB = childA.getChildren().stream()
                .filter(child -> child.getLabel().equals(CHILD_A_B))
                .findFirst()
                .orElse(null);

        assertNotNull(childAB);
        assertEquals(CHILD_A_B, childAB.getLabel());
        assertEquals(0, childAB.getChildren().size());
        assertEquals(3, childAB.getMetadata().getFields().size());
        assertTrue(childAB.getMetadata().getFields().stream().anyMatch(childA_B_field::equals));
        assertTrue(childAB.getMetadata().getFields().stream().anyMatch(childA_dup_childA_B_field::equals));
        assertTrue(childAB.getMetadata().getFields().stream().anyMatch(childA_dup_childA_B_dup_field::equals));

        //Child B

        Node<GraphQLMetadata> childB = root.getChildren().stream()
                .filter(child -> child.getLabel().equals(CHILD_B))
                .findFirst()
                .orElse(null);

        assertNotNull(childB);
        assertEquals(CHILD_B, childB.getLabel());
        assertEquals(1, childB.getChildren().size());
        assertEquals(1, childB.getMetadata().getFields().size());
        assertEquals(childB_field, childB.getMetadata().getFields().get(0));

        //Child B children

        Node<GraphQLMetadata> childBA = childB.getChildren().stream()
                .filter(child -> child.getLabel().equals(CHILD_B_A))
                .findFirst()
                .orElse(null);

        assertNotNull(childBA);
        assertEquals(CHILD_B_A, childBA.getLabel());
        assertEquals(0, childBA.getChildren().size());
        assertEquals(1, childBA.getMetadata().getFields().size());
        assertEquals(childB_A_field, childBA.getMetadata().getFields().get(0));

    }

}
