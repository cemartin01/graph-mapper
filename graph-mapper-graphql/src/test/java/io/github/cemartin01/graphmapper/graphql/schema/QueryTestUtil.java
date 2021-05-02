package io.github.cemartin01.graphmapper.graphql.schema;

import graphql.cachecontrol.CacheControl;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionId;
import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.normalized.NormalizedQueryTree;
import graphql.normalized.NormalizedQueryTreeFactory;
import lombok.val;
import org.dataloader.DataLoaderRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static graphql.execution.ExecutionContextBuilder.newExecutionContextBuilder;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryTestUtil {

    public static ExecutionContext produce(String queryFilename, String operationName,
                                           Map<String, Object> variableMap) {

        val schema = SchemaLoader.loadSchema();
        val query = QueryLoader.loadQuery(queryFilename);

        val operationDefinition = new OperationDefinition(operationName);
        val executionId = ExecutionId.from("001");
        val dataLoaderRegistry = new DataLoaderRegistry();
        val cacheControl = CacheControl.newCacheControl();

        return newExecutionContextBuilder()
                .root("root")
                .context("context")
                .executionId(executionId)
                .operationDefinition(operationDefinition)
                .document(query)
                .variables(variableMap)
                .graphQLSchema(schema)
                .fragmentsByName(new HashMap<>())
                .dataLoaderRegistry(dataLoaderRegistry)
                .cacheControl(cacheControl)
                .build();
    }

    public static MergedField getMergedField(ExecutionContext ctx) {

        NormalizedQueryTree tree = NormalizedQueryTreeFactory.createNormalizedQuery(
                ctx.getGraphQLSchema(), ctx.getDocument(), null, new HashMap<>()
        );

        Optional<MergedField> possibleMergedField = tree.getNormalizedFieldToMergedField().values().stream()
                .filter(field -> field.getName().equals(ctx.getOperationDefinition().getName()))
                .findFirst();

        assertTrue(possibleMergedField.isPresent());

        return possibleMergedField.get();
    }

}
