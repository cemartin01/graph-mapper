package io.github.cemartin01.graphmapper.graphql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import graphql.execution.ExecutionContext;
import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.SelectionSet;
import io.github.cemartin01.graphmapper.MappingGraph;
import io.github.cemartin01.graphmapper.Node;
import io.github.cemartin01.graphmapper.NodeLabel;
import io.github.cemartin01.graphmapper.graphql.argument.ArgumentBindingContext;
import io.github.cemartin01.graphmapper.graphql.mock.GraphQLTestNodeLabel;
import io.github.cemartin01.graphmapper.graphql.schema.GraphQLSchemaBasedTest;
import io.github.cemartin01.graphmapper.graphql.schema.GraphQLSchemaNodeLabel;
import io.github.cemartin01.graphmapper.graphql.schema.QueryTestUtil;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static graphql.schema.DataFetchingEnvironmentImpl.newDataFetchingEnvironment;
import static org.junit.jupiter.api.Assertions.*;

public class GraphQLMappingFactoryTest {

    @Test
    public void mappingFactoryIsInitialized() throws IllegalAccessException, NoSuchFieldException {

        ArgumentBindingContext argumentBindingContext = new ArgumentBindingContext(ImmutableList.of());
        GraphQLMappingFactory factory = new GraphQLMappingFactory(GraphQLTestNodeLabel.values(), argumentBindingContext);

        java.lang.reflect.Field bindingMapField = GraphQLMappingFactory.class.getDeclaredField("bindingFactory");
        bindingMapField.setAccessible(true);
        assertSame(argumentBindingContext, bindingMapField.get(factory));

        java.lang.reflect.Field nodeLabelMapField = GraphQLMappingFactory.class.getDeclaredField("nodeLabelMap");
        nodeLabelMapField.setAccessible(true);
        Map<String, NodeLabel> nodeLabelMap = (Map<String, NodeLabel>) nodeLabelMapField.get(factory);
        assertEquals(6, nodeLabelMap.size());

        assertEquals(GraphQLTestNodeLabel.MEAL, nodeLabelMap.get(GraphQLTestNodeLabel.MEAL.getName()));
        assertEquals(GraphQLTestNodeLabel.CHILD_A, nodeLabelMap.get(GraphQLTestNodeLabel.CHILD_A.getName()));
        assertEquals(GraphQLTestNodeLabel.CHILD_B, nodeLabelMap.get(GraphQLTestNodeLabel.CHILD_B.getName()));
        assertEquals(GraphQLTestNodeLabel.CHILD_A_A, nodeLabelMap.get(GraphQLTestNodeLabel.CHILD_A_A.getName()));
        assertEquals(GraphQLTestNodeLabel.CHILD_A_B, nodeLabelMap.get(GraphQLTestNodeLabel.CHILD_A_B.getName()));
        assertEquals(GraphQLTestNodeLabel.CHILD_B_A, nodeLabelMap.get(GraphQLTestNodeLabel.CHILD_B_A.getName()));
    }

    @Test
    @GraphQLSchemaBasedTest
    public void mergedFieldIsResolved() {

        ExecutionContext executionContext = QueryTestUtil.produce(
                "findMeal_array_criteria",
                "findMealByArrayCriteria",
                ImmutableMap.of()
        );

        MergedField field = QueryTestUtil.getMergedField(executionContext);

        assertNotNull(field);
    }

    @Test
    @GraphQLSchemaBasedTest
    public void mappingBasedOn_findMeal_simple_isBuilt() {

        ExecutionContext executionContext = QueryTestUtil.produce(
                "findMeal_simple",
                "findMeal",
                ImmutableMap.of()
        );

        val env = newDataFetchingEnvironment(executionContext)
                .mergedField(QueryTestUtil.getMergedField(executionContext))
                .build();

        GraphQLMappingFactory factory = new GraphQLMappingFactory(GraphQLSchemaNodeLabel.values(), null);

        GraphQLMapping mapping = factory.getMapping(env);

        assertNotNull(mapping);

        MappingGraph<GraphQLMetadata> mappingGraph = mapping.getMappingGraph();

        assertEquals(Node.RootNodeLabel.ROOT, mappingGraph.getRoot().getLabel());
        assertEquals(1, mappingGraph.getRoot().getChildren().size());
        assertEquals(1, mappingGraph.getRoot().getMetadata().getFields().size());
        assertSame(env, mappingGraph.getRoot().getMetadata().getEnv());
        assertSame(env.getField(), mappingGraph.getRoot().getMetadata().getFields().get(0));

        Node<GraphQLMetadata> recipe = mappingGraph.getRoot().getChildren().get(0);

        assertEquals(GraphQLSchemaNodeLabel.RECIPE, recipe.getLabel());
        assertEquals(0, recipe.getChildren().size());
        assertEquals(1, recipe.getMetadata().getFields().size());
        assertSame(env, recipe.getMetadata().getEnv());

        Field recipeField = env.getField().getSelectionSet().getChildren().stream()
                .filter(node -> node instanceof Field)
                .map(node -> (Field)node)
                .filter(childField -> childField.getName().equals(GraphQLSchemaNodeLabel.RECIPE.getName()))
                .findFirst()
                .get();

        assertSame(recipeField, recipe.getMetadata().getFields().get(0));
    }

    @Test
    @GraphQLSchemaBasedTest
    public void mappingBasedOn_findMeal_fragmentSpread_isBuilt() {
        ExecutionContext executionContext = QueryTestUtil.produce(
                "findMeal_fragmentSpread",
                "findMeal",
                ImmutableMap.of()
        );

        FragmentDefinition fragmentDefinition = FragmentDefinition.newFragmentDefinition()
                .selectionSet(SelectionSet.newSelectionSet()
                        .selection(Field.newField()
                                .name("recipe")
                                .selectionSet(SelectionSet.newSelectionSet()
                                        .selection(Field.newField()
                                                .name("id")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        val env = newDataFetchingEnvironment(executionContext)
                .mergedField(QueryTestUtil.getMergedField(executionContext))
                .fragmentsByName(ImmutableMap.<String, FragmentDefinition>builder()
                        .put("MealWithRecipe", fragmentDefinition)
                        .build())
                .build();

        GraphQLMappingFactory factory = new GraphQLMappingFactory(GraphQLSchemaNodeLabel.values(), null);
        GraphQLMapping mapping = factory.getMapping(env);

        MappingGraph<GraphQLMetadata> mappingGraph = mapping.getMappingGraph();

        assertEquals(Node.RootNodeLabel.ROOT, mappingGraph.getRoot().getLabel());
        assertEquals(1, mappingGraph.getRoot().getChildren().size());

        Node<GraphQLMetadata> recipe = mappingGraph.getRoot().getChildren().get(0);

        assertEquals(GraphQLSchemaNodeLabel.RECIPE, recipe.getLabel());
        assertEquals(0, recipe.getChildren().size());
        assertEquals(2, recipe.getMetadata().getFields().size());
        assertEquals("recipe", recipe.getMetadata().getFields().get(0).getName());
        assertEquals("recipe", recipe.getMetadata().getFields().get(1).getName());
    }

    @Test
    @GraphQLSchemaBasedTest
    public void mappingBasedOn_findMeal_inlineFragment_isBuilt() {
        ExecutionContext executionContext = QueryTestUtil.produce(
                "findMeal_inlineFragment",
                "findMeal",
                ImmutableMap.of()
        );

        val env = newDataFetchingEnvironment(executionContext)
                .mergedField(QueryTestUtil.getMergedField(executionContext))
                .build();

        GraphQLMappingFactory factory = new GraphQLMappingFactory(GraphQLSchemaNodeLabel.values(), null);
        GraphQLMapping mapping = factory.getMapping(env);

        MappingGraph<GraphQLMetadata> mappingGraph = mapping.getMappingGraph();

        assertEquals(Node.RootNodeLabel.ROOT, mappingGraph.getRoot().getLabel());
        assertEquals(1, mappingGraph.getRoot().getChildren().size());

        Node<GraphQLMetadata> recipe = mappingGraph.getRoot().getChildren().get(0);

        assertEquals(GraphQLSchemaNodeLabel.RECIPE, recipe.getLabel());
        assertEquals(0, recipe.getChildren().size());
        assertEquals(4, recipe.getMetadata().getFields().size());
        assertEquals("recipe", recipe.getMetadata().getFields().get(0).getName());
        assertEquals("recipe", recipe.getMetadata().getFields().get(1).getName());
        assertEquals("recipe", recipe.getMetadata().getFields().get(2).getName());
        assertEquals("recipe", recipe.getMetadata().getFields().get(3).getName());
    }

}
