package io.github.cemartin01.graphmapper.graphql.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import graphql.Scalars;
import graphql.language.Argument;
import graphql.language.ObjectValue;
import graphql.language.VariableReference;
import graphql.schema.Coercing;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import io.github.cemartin01.graphmapper.exception.GraphMapperException;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;
import io.github.cemartin01.graphmapper.graphql.coercing.Code;
import io.github.cemartin01.graphmapper.graphql.coercing.CodeScalar;
import io.github.cemartin01.graphmapper.graphql.mock.dto.ArrayCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.EmptyCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.MealCriteria;
import io.github.cemartin01.graphmapper.graphql.mock.dto.NestedCriteria;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static io.github.cemartin01.graphmapper.graphql.mock.dto.MealGroup.LUNCH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArgumentBindingContextTest {

    @Test
    public void argumentBindingContextIsInitializedByRuntimeWiring() throws GraphMapperInitializationException,
            NoSuchFieldException, IllegalAccessException {

        ArgumentBindingContext ctx = new ArgumentBindingContext(prepareRuntimeWiring());

        ctx.addBinding(NestedCriteria.class);
        ctx.addBinding(MealCriteria.class);
        ctx.addBinding(ArrayCriteria.class);

        validateArgumentBindingContext(ctx);
    }

    @Test
    public void argumentBindingContextIsInitialized() throws GraphMapperInitializationException, NoSuchFieldException,
            IllegalAccessException {

        ArgumentBindingContext ctx = new ArgumentBindingContext(ImmutableList.of(
                Scalars.GraphQLBoolean,
                Scalars.GraphQLInt,
                Scalars.GraphQLFloat,
                Scalars.GraphQLString,
                Scalars.GraphQLID,
                Scalars.GraphQLByte,
                CodeScalar.CODE_SCALAR
        ));

        ctx.addBinding(NestedCriteria.class);
        ctx.addBinding(MealCriteria.class);
        ctx.addBinding(ArrayCriteria.class);

        validateArgumentBindingContext(ctx);
    }

    private void validateArgumentBindingContext(ArgumentBindingContext ctx) throws NoSuchFieldException, IllegalAccessException {

        //COERCING
        Field coercingMapField = ArgumentBindingContext.class.getDeclaredField("coercingMap");
        coercingMapField.setAccessible(true);
        Map<Class<?>, Coercing> coercingMap = (Map<Class<?>, Coercing>)coercingMapField.get(ctx);

        assertEquals(7, coercingMap.size());
        assertSame(Scalars.GraphQLBoolean.getCoercing(), coercingMap.get(Boolean.class));
        assertSame(Scalars.GraphQLInt.getCoercing(), coercingMap.get(Integer.class));
        assertSame(Scalars.GraphQLFloat.getCoercing(), coercingMap.get(Double.class));
        assertSame(Scalars.GraphQLString.getCoercing(), coercingMap.get(String.class));
        assertSame(Scalars.GraphQLID.getCoercing(), coercingMap.get(Object.class));
        assertSame(Scalars.GraphQLByte.getCoercing(), coercingMap.get(Byte.class));
        assertSame(CodeScalar.CODE_SCALAR.getCoercing(), coercingMap.get(Code.class));

        //ARGUMENT BINDINGS
        Field bindingMapField = ArgumentBindingContext.class.getDeclaredField("bindingMap");
        bindingMapField.setAccessible(true);
        Map<Class<?>, ArgumentBinding<?>> bindingMap = (Map<Class<?>, ArgumentBinding<?>>) bindingMapField.get(ctx);

        assertEquals(3, bindingMap.size());
        assertNotNull(bindingMap.get(NestedCriteria.class));

        //Validate constructor, 1 coercing, 1 enum and 1 nestedInput binding via instantiation
        ArgumentBinding<MealCriteria> criteriaBinding = (ArgumentBinding<MealCriteria>) bindingMap.get(MealCriteria.class);
        assertEquals(8, criteriaBinding.getPropertyMap().size());
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("nameContains", "ABC");
        variableMap.put("group", LUNCH);
        Map<String, Object> nestedCriteriaMap = new HashMap<>();
        nestedCriteriaMap.put("nameExcludes", "XYZ");
        variableMap.put("nestedCriteria", nestedCriteriaMap);
        MealCriteria mealCriteria = criteriaBinding.instantiate(variableMap);

        assertEquals("ABC", mealCriteria.getNameContains());
        assertEquals(LUNCH, mealCriteria.getGroup());
        assertEquals("XYZ", mealCriteria.getNestedCriteria().getNameExcludes());

        //Validate 1 list binding via instantiation
        ArgumentBinding<ArrayCriteria> arrayCriteriaBinding = (ArgumentBinding<ArrayCriteria>) bindingMap.get(ArrayCriteria.class);
        assertEquals(8, arrayCriteriaBinding.getPropertyMap().size());
        variableMap = new HashMap<>();
        variableMap.put("nameContains", ImmutableList.of("A", "B", "C"));
        ArrayCriteria arrayCriteria = arrayCriteriaBinding.instantiate(variableMap);

        assertEquals("A", arrayCriteria.getNameContains().get(0));
        assertEquals("B", arrayCriteria.getNameContains().get(1));
        assertEquals( "C", arrayCriteria.getNameContains().get(2));
    }

    @Test
    public void argumentIsInstantiated() throws GraphMapperInitializationException {
        ArgumentBindingContext ctx = new ArgumentBindingContext(ImmutableList.of());
        ctx.addBinding(EmptyCriteria.class);
        Argument argument = Argument.newArgument().value(
                VariableReference.newVariableReference()
                        .name("empty")
                        .build())
                .build();

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getVariables()).thenReturn(ImmutableMap.<String, Object>builder()
                .put("empty", new HashMap<>())
                .build());

        EmptyCriteria c = ctx.instantiateArgument(argument, EmptyCriteria.class, env);
        assertNotNull(c);
    }

    @Test
    public void argumentBasedOnValueIsInstantiated() throws GraphMapperInitializationException {
        ArgumentBindingContext ctx = new ArgumentBindingContext(ImmutableList.of());
        ctx.addBinding(EmptyCriteria.class);
        Argument argument = Argument.newArgument().value(
                new ObjectValue(ImmutableList.of()))
                .build();

        EmptyCriteria c = ctx.instantiateArgument(argument, EmptyCriteria.class, null);
        assertNotNull(c);
    }

    @Test
    public void instantiationFailsDueToMissingBinding() {
        ArgumentBindingContext ctx = new ArgumentBindingContext(ImmutableList.of());

        assertThrows(GraphMapperException.class, () -> {
            ctx.instantiateArgument(null, MealCriteria.class, null);
        });
    }

    private RuntimeWiring prepareRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(Scalars.GraphQLByte)
                .scalar(CodeScalar.get())
                .build();
    }

}
