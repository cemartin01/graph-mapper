package io.github.cemartin01.graphmapper;

import io.github.cemartin01.graphmapper.mock.CateringNodeLabel;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    public void nodeIsCreated() {

        Node<Void> node1 = Node.of(CateringNodeLabel.MEAL);
        assertEquals(CateringNodeLabel.MEAL, node1.getLabel());
        assertNull(node1.getMetadata());
        assertTrue(node1.getChildren().isEmpty());

        Node<Void> node2 = Node.<Void>builder()
                .label(CateringNodeLabel.MEAL)
                .child(Node.of(CateringNodeLabel.RECIPE))
                .build();

        assertEquals(CateringNodeLabel.MEAL, node2.getLabel());
        assertNull(node2.getMetadata());
        assertEquals(1, node2.getChildren().size());

        Node<String> node3 = Node.of(CateringNodeLabel.MEAL, "My Node",
                Collections.singletonList(Node.<String>builder()
                        .label(CateringNodeLabel.RECIPE)
                        .metadata("My Nested Node")
                        .build())
        );
        assertEquals(CateringNodeLabel.MEAL, node3.getLabel());
        assertEquals("My Node", node3.getMetadata());
        assertEquals(1, node3.getChildren().size());

    }

}
