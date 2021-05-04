package io.github.cemartin01.graphmapper;

import com.google.common.collect.ImmutableList;
import io.github.cemartin01.graphmapper.mock.CateringNodeLabel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MappingGraphTest {

    @Test
    public void mappingGraphIsBuilt() {

        MappingGraph<Void> mappingGraph = MappingGraph.of(
                Node.<Void>builder()
                        .child(Node.<Void>builder()
                                .label(CateringNodeLabel.MEAL)
                                .child(Node.of(CateringNodeLabel.RECIPE))
                                .child(Node.of(CateringNodeLabel.MEAL_TYPE))
                                .build())
                        .build()
        );

        Node<Void> root = mappingGraph.getRoot();

        assertNotNull(root);
        assertEquals(Node.RootNodeLabel.ROOT, root.getLabel());

        List<Node<Void>> children = root.getChildren();

        assertNotNull(children);

        Node<Void> mealNode = children.get(0);
        assertEquals(CateringNodeLabel.MEAL, mealNode.getLabel());

        children = mealNode.getChildren();

        assertEquals(CateringNodeLabel.RECIPE, children.get(0).getLabel());
        assertEquals(CateringNodeLabel.MEAL_TYPE, children.get(1).getLabel());
    }

    @Test
    public void nodeIsFoundByPath() {

        MappingGraph<Void> mappingGraph = MappingGraph.of(
                Node.<Void>builder()
                        .child(Node.<Void>builder()
                                .label(CateringNodeLabel.MEAL)
                                .child(Node.of(CateringNodeLabel.RECIPE))
                                .child(Node.of(CateringNodeLabel.MEAL_TYPE))
                                .build())
                        .build()
        );

        Optional<Node<Void>> nodeOptional = mappingGraph.getNodeByPath(ImmutableList.of(
                CateringNodeLabel.MEAL,
                CateringNodeLabel.MEAL_TYPE
        ));

        assertTrue(nodeOptional.isPresent());
        assertEquals(CateringNodeLabel.MEAL_TYPE, nodeOptional.get().getLabel());

    }

    @Test
    public void nodeIsNotFoundDueToItIsMissing() {

        MappingGraph<Void> mappingGraph = MappingGraph.of(
                Node.<Void>builder()
                        .child(Node.<Void>builder()
                                .label(CateringNodeLabel.MEAL)
                                .child(Node.of(CateringNodeLabel.RECIPE))
                                .child(Node.of(CateringNodeLabel.MEAL_TYPE))
                                .build())
                        .build()
        );

        Optional<Node<Void>> nodeOptional = mappingGraph.getNodeByPath(ImmutableList.of(
                CateringNodeLabel.MEAL,
                CateringNodeLabel.PROVIDER
        ));

        assertFalse(nodeOptional.isPresent());

    }

}
