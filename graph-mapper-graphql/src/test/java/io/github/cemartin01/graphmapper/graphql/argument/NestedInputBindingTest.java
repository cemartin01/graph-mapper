package io.github.cemartin01.graphmapper.graphql.argument;

import com.google.common.collect.ImmutableList;
import graphql.language.ArrayValue;
import graphql.language.ObjectValue;
import io.github.cemartin01.graphmapper.graphql.mock.dto.ArrayCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.NestedCriteria;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NestedInputBindingTest {

    @Test
    public void nestedInputIsSet() {
        MealCriteria input = new MealCriteria();
        prepareBinding(prepareValueArgumentBinding()).setByValue(input, new ObjectValue(ImmutableList.of()));
        assertNotNull(input.getNestedCriteria());
    }

    @Test
    public void nestedInputBasedOnVariableIsSet() {
        MealCriteria input = new MealCriteria();
        prepareBinding(prepareArgumentBinding()).set(input, new Object());
        assertNotNull(input.getNestedCriteria());
    }

    @Test
    public void nestedInputListIsSet() {
        ArrayCriteria input = new ArrayCriteria();
        ArrayValue arrayValue = new ArrayValue(ImmutableList.of(new ObjectValue(ImmutableList.of()), new ObjectValue(ImmutableList.of())));
        prepareListBinding(prepareValueArgumentBinding()).setListByValue(input, arrayValue);
        assertEquals(2, input.getNestedCriteria().size());
        assertNotNull(input.getNestedCriteria().get(0));
        assertNotNull(input.getNestedCriteria().get(1));
    }

    @Test
    public void nestedInputListBasedOnVariableIsSet() {
        ArrayCriteria input = new ArrayCriteria();
        List list = new ArrayList();
        list.add(new Object());
        list.add(new Object());
        prepareListBinding(prepareArgumentBinding()).setList(input, list);
        assertEquals(2, input.getNestedCriteria().size());
        assertNotNull(input.getNestedCriteria().get(0));
        assertNotNull(input.getNestedCriteria().get(1));
    }

    private ArgumentBinding<NestedCriteria> prepareArgumentBinding() {
        ArgumentBinding<NestedCriteria> argumentBinding = mock(ArgumentBinding.class);
        when(argumentBinding.instantiate(any())).thenReturn(new NestedCriteria());
        return argumentBinding;
    }

    private ArgumentBinding<NestedCriteria> prepareValueArgumentBinding() {
        ArgumentBinding<NestedCriteria> argumentBinding = mock(ArgumentBinding.class);
        when(argumentBinding.instantiateByValue(any())).thenReturn(new NestedCriteria());
        return argumentBinding;
    }

    private ValueBindings.NestedInputBinding<MealCriteria> prepareBinding(ArgumentBinding<NestedCriteria> argumentBinding) {
        BiConsumer<MealCriteria, NestedCriteria> setter = (input, nestedInput) -> input.setNestedCriteria(nestedInput);
        return new ValueBindings.NestedInputBinding<>(setter, argumentBinding);
    }

    private ValueBindings.NestedInputBinding<ArrayCriteria> prepareListBinding(ArgumentBinding<NestedCriteria> argumentBinding) {
        BiConsumer<ArrayCriteria, List<NestedCriteria>> setter = (input, nestedInput) -> input.setNestedCriteria(nestedInput);
        return new ValueBindings.NestedInputBinding<>(setter, argumentBinding);
    }

}
