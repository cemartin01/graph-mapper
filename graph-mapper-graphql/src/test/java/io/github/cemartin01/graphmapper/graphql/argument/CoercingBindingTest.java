package io.github.cemartin01.graphmapper.graphql.argument;

import com.google.common.collect.ImmutableList;
import graphql.language.*;
import graphql.scalar.GraphqlBooleanCoercing;
import graphql.scalar.GraphqlFloatCoercing;
import graphql.scalar.GraphqlIntCoercing;
import graphql.scalar.GraphqlStringCoercing;
import graphql.schema.Coercing;
import io.github.cemartin01.graphmapper.graphql.mock.dto.ArrayCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealCriteria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CoercingBindingTest {

    @Test
    public void intIsSet() {
        BiConsumer<MealCriteria, Integer> setter = (input, integer) -> input.setMaxNutritionValue(integer);
        Coercing c = new GraphqlIntCoercing();
        ValueBindings.CoercingBinding<MealCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        MealCriteria input = new MealCriteria();
        binding.setByValue(input, new IntValue(BigInteger.valueOf(10L)));
        assertEquals(10, input.getMaxNutritionValue());
    }

    @Test
    public void intListIsSet() {
        BiConsumer<ArrayCriteria, List<Integer>> setter = (input, integers) -> input.setMaxNutritionValue(integers);
        Coercing c = new GraphqlIntCoercing();
        ValueBindings.CoercingBinding<ArrayCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        ArrayCriteria input = new ArrayCriteria();
        ArrayValue arrayValue = new ArrayValue(ImmutableList.of(
                new IntValue(BigInteger.valueOf(10L)),
                new IntValue(BigInteger.valueOf(20L)))
        );
        binding.setListByValue(input, arrayValue);
        assertEquals(10, input.getMaxNutritionValue().get(0));
        assertEquals(20, input.getMaxNutritionValue().get(1));
    }


    @Test
    public void booleanIsSet() {
        BiConsumer<MealCriteria, Boolean> setter = (input, bool) -> input.setActive(bool);
        Coercing c = new GraphqlBooleanCoercing();
        ValueBindings.CoercingBinding<MealCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        MealCriteria input = new MealCriteria();
        binding.setByValue(input, new BooleanValue(true));
        assertEquals(true, input.getActive());
    }

    @Test
    public void stringIsSet() {
        BiConsumer<MealCriteria, String> setter = (input, string) -> input.setNameContains(string);
        Coercing c = new GraphqlStringCoercing();
        ValueBindings.CoercingBinding<MealCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        MealCriteria input = new MealCriteria();
        binding.setByValue(input, new StringValue("ABC"));
        assertEquals("ABC", input.getNameContains());
    }

    @Test
    public void floatIsSet() {
        BiConsumer<MealCriteria, Double> setter = (input, floatValue) -> input.setPopularity(floatValue);
        Coercing c = new GraphqlFloatCoercing();
        ValueBindings.CoercingBinding<MealCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        MealCriteria input = new MealCriteria();
        binding.setByValue(input, new FloatValue(BigDecimal.valueOf(20L)));
        assertEquals(20D, input.getPopularity());
    }

    @Test
    public void nullIsSet() {
        BiConsumer<MealCriteria, Double> setter = (input, floatValue) -> input.setPopularity(floatValue);
        Coercing c = new GraphqlFloatCoercing();
        ValueBindings.CoercingBinding<MealCriteria> binding = new ValueBindings.CoercingBinding<>(setter, c);

        MealCriteria input = new MealCriteria();
        binding.setByValue(input, NullValue.newNullValue().build());
        assertNull(input.getPopularity());
    }


}
