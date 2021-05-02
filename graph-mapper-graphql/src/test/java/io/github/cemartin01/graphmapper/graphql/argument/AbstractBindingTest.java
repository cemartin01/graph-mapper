package io.github.cemartin01.graphmapper.graphql.argument;

import graphql.scalar.GraphqlIntCoercing;
import graphql.schema.Coercing;
import io.github.cemartin01.graphmapper.graphql.mock.dto.ArrayCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealCriteria;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractBindingTest {

    @Test
    public void intBasedOnVariableIsSet() {
        BiConsumer<MealCriteria, Integer> setter = (input, integer) -> input.setMaxNutritionValue(integer);
        Coercing c = new GraphqlIntCoercing();
        ValueBindings.CoercingBinding<MealCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        MealCriteria input = new MealCriteria();
        binding.set(input, Integer.valueOf(10));
        assertEquals(10, input.getMaxNutritionValue());
    }

    @Test
    public void intListBasedOnVariableIsSet() {
        BiConsumer<ArrayCriteria, List<Integer>> setter = (input, integers) -> input.setMaxNutritionValue(integers);
        Coercing c = new GraphqlIntCoercing();
        ValueBindings.CoercingBinding<ArrayCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        ArrayCriteria input = new ArrayCriteria();
        List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(10));
        list.add(Integer.valueOf(20));

        binding.setList(input, list);
        assertEquals(10, input.getMaxNutritionValue().get(0));
        assertEquals(20, input.getMaxNutritionValue().get(1));
    }

}
