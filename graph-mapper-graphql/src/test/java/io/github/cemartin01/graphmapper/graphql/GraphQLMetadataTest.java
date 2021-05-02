package io.github.cemartin01.graphmapper.graphql;

import com.google.common.collect.ImmutableList;
import graphql.language.Argument;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealCriteria;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphQLMetadataTest {

    @Test
    public void objectArgumentIsReturned() {

        GraphQLMappingFactory factory = mock(GraphQLMappingFactory.class);

        Field field = mock(Field.class);
        Argument argument = mock(Argument.class);
        when(argument.getName()).thenReturn("myArg");
        when(field.getArguments()).thenReturn(ImmutableList.of(argument));

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);

        when(factory.instantiateArgument(argument, MealCriteria.class, env)).thenReturn(new MealCriteria());

        GraphQLMetadata metadata = new GraphQLMetadata(ImmutableList.of(field), env, factory);

        Optional<MealCriteria> mealCriteriaOptional = metadata.getObjectArgument("myArg", MealCriteria.class);

        assertTrue(mealCriteriaOptional.isPresent());
    }

    @Test
    public void objectArgumentIsNotFound() {

        GraphQLMappingFactory factory = mock(GraphQLMappingFactory.class);
        Field field = mock(Field.class);
        when(field.getArguments()).thenReturn(ImmutableList.of());
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);

        GraphQLMetadata metadata = new GraphQLMetadata(ImmutableList.of(field), env, factory);

        Optional<MealCriteria> mealCriteriaOptional = metadata.getObjectArgument("myArg", MealCriteria.class);

        assertFalse(mealCriteriaOptional.isPresent());
    }

}
