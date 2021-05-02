package io.github.cemartin01.graphmapper.graphql.argument;

import com.google.common.collect.ImmutableList;
import graphql.language.ArrayValue;
import graphql.language.EnumValue;
import io.github.cemartin01.graphmapper.graphql.mock.dto.EnumBindingInput;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealGroup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumBindingTest {

    @Test
    public void enumIsSet() {
        BiConsumer<EnumBindingInput, MealGroup> setter = (input, mealGroup) -> input.setMealGroup(mealGroup);
        Function<String, MealGroup> enumFactory = (name) -> MealGroup.valueOf(name);
        ValueBindings.EnumBinding<EnumBindingInput> binding = new ValueBindings.EnumBinding<>(setter, enumFactory);

        EnumBindingInput input = new EnumBindingInput();
        binding.setByValue(input, new EnumValue("SOUP"));
        assertEquals(MealGroup.SOUP, input.getMealGroup());
    }

    @Test
    public void enumListIsSet() {
        BiConsumer<EnumBindingInput, List<MealGroup>> setter = (input, mealGroups) -> input.setMealGroups(mealGroups);
        Function<String, MealGroup> enumFactory = (name) -> MealGroup.valueOf(name);
        ValueBindings.EnumBinding<EnumBindingInput> binding = new ValueBindings.EnumBinding<>(setter, enumFactory);

        EnumBindingInput input = new EnumBindingInput();
        ArrayValue arrayValue = new ArrayValue(ImmutableList.of(new EnumValue("SOUP"), new EnumValue("LUNCH")));
        binding.setListByValue(input, arrayValue);
        assertEquals(MealGroup.SOUP, input.getMealGroups().get(0));
        assertEquals(MealGroup.LUNCH, input.getMealGroups().get(1));
    }

}
