package io.github.cemartin01.graphmapper.graphql.argument;

import com.google.common.collect.ImmutableList;
import graphql.language.ArrayValue;
import graphql.language.IntValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import io.github.cemartin01.graphmapper.graphql.mock.dto.ArrayCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealCriteria;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ArgumentBindingTest {

    @Test
    public void propertyIsSetByValue() {
        MealCriteria mealCriteria = new MealCriteria();
        Supplier<MealCriteria> constructor = () -> mealCriteria;

        ValueBindings.CoercingBinding<MealCriteria> binding = mock(ValueBindings.CoercingBinding.class);
        Map<String, ValueBindings.AbstractBinding<MealCriteria>> bindings = new HashMap<>();
        bindings.put("maxNutritionValue", binding);

        //Builder test
        ArgumentBinding<MealCriteria> argumentBinding = ArgumentBinding.<MealCriteria>builder()
                .constructor(constructor)
                .propertyMap(bindings)
                .clazz(MealCriteria.class)
                .build();

        ObjectField maxNutritionValueField = new ObjectField("maxNutritionValue", new IntValue(BigInteger.valueOf(10L)));
        ObjectValue objectValue = new ObjectValue(ImmutableList.of(maxNutritionValueField));
        assertSame(mealCriteria, argumentBinding.instantiateByValue(objectValue));
        Mockito.verify(binding, times(1)).setByValue(mealCriteria, maxNutritionValueField.getValue());
    }

    @Test
    public void listIsSetByValue() {
        ArrayCriteria arrayCriteria = new ArrayCriteria();
        Supplier<ArrayCriteria> constructor = () -> arrayCriteria;

        ValueBindings.CoercingBinding<ArrayCriteria> binding = mock(ValueBindings.CoercingBinding.class);
        Map<String, ValueBindings.AbstractBinding<ArrayCriteria>> bindings = new HashMap<>();
        bindings.put("maxNutritionValue", binding);

        ArgumentBinding<ArrayCriteria> argumentBinding = new ArgumentBinding<>(ArrayCriteria.class, constructor, bindings);
        ObjectField maxNutritionValueField = new ObjectField("maxNutritionValue", new ArrayValue(ImmutableList.of()));
        ObjectValue objectValue = new ObjectValue(ImmutableList.of(maxNutritionValueField));
        assertSame(arrayCriteria, argumentBinding.instantiateByValue(objectValue));
        Mockito.verify(binding, times(1)).setListByValue(arrayCriteria, (ArrayValue) maxNutritionValueField.getValue());
    }

    @Test
    public void unknownValueIsIgnored() {
        ArrayCriteria arrayCriteria = new ArrayCriteria();
        Supplier<ArrayCriteria> constructor = () -> arrayCriteria;

        ArgumentBinding<ArrayCriteria> argumentBinding = new ArgumentBinding<>(ArrayCriteria.class, constructor, new HashMap<>());
        ObjectField maxNutritionValueField = new ObjectField("maxNutritionValue", new ArrayValue(ImmutableList.of()));
        ObjectValue objectValue = new ObjectValue(ImmutableList.of(maxNutritionValueField));
        assertSame(arrayCriteria, argumentBinding.instantiateByValue(objectValue));
    }

    @Test
    public void propertyIsSet() {
        MealCriteria mealCriteria = new MealCriteria();
        Supplier<MealCriteria> constructor = () -> mealCriteria;

        ValueBindings.CoercingBinding<MealCriteria> binding = mock(ValueBindings.CoercingBinding.class);
        Map<String, ValueBindings.AbstractBinding<MealCriteria>> bindings = new HashMap<>();
        bindings.put("maxNutritionValue", binding);

        ArgumentBinding<MealCriteria> argumentBinding = new ArgumentBinding<>(MealCriteria.class, constructor, bindings);

        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("maxNutritionValue", Integer.valueOf(10));

        assertSame(mealCriteria, argumentBinding.instantiate(variableMap));
        Mockito.verify(binding, times(1)).set(mealCriteria, Integer.valueOf(10));
    }

    @Test
    public void listIsSet() {
        ArrayCriteria arrayCriteria = new ArrayCriteria();
        Supplier<ArrayCriteria> constructor = () -> arrayCriteria;

        ValueBindings.CoercingBinding<ArrayCriteria> binding = mock(ValueBindings.CoercingBinding.class);
        Map<String, ValueBindings.AbstractBinding<ArrayCriteria>> bindings = new HashMap<>();
        bindings.put("maxNutritionValue", binding);

        ArgumentBinding<ArrayCriteria> argumentBinding = new ArgumentBinding<>(ArrayCriteria.class, constructor, bindings);

        List list = ImmutableList.of(Integer.valueOf(10));
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("maxNutritionValue", list);

        assertSame(arrayCriteria, argumentBinding.instantiate(variableMap));
        Mockito.verify(binding, times(1)).setList(arrayCriteria, list);
    }

    @Test
    public void unknownObjectIsIgnored() {
        ArrayCriteria arrayCriteria = new ArrayCriteria();
        Supplier<ArrayCriteria> constructor = () -> arrayCriteria;

        ArgumentBinding<ArrayCriteria> argumentBinding = new ArgumentBinding<>(ArrayCriteria.class, constructor, new HashMap<>());

        List list = ImmutableList.of(Integer.valueOf(10));
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("maxNutritionValue", list);

        assertSame(arrayCriteria, argumentBinding.instantiate(variableMap));
    }


}
