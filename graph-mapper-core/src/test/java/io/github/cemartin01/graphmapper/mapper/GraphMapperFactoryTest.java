package io.github.cemartin01.graphmapper.mapper;

import io.github.cemartin01.graphmapper.MappingGraph;
import io.github.cemartin01.graphmapper.Node;
import io.github.cemartin01.graphmapper.classtree.ClassNode;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;
import io.github.cemartin01.graphmapper.mock.CateringNodeLabel;
import io.github.cemartin01.graphmapper.mock.dto.*;
import io.github.cemartin01.graphmapper.mock.entity.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static io.github.cemartin01.graphmapper.mock.CateringNodeLabel.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphMapperFactoryTest {

    private final GraphMapperContext CONTEXT = initContext();

    @Test
    public void graphMapperForObjectIsBuilt() throws NoSuchFieldException, IllegalAccessException {

        GraphMapperFactory factory = new GraphMapperFactory(CONTEXT);

        MappingGraph<Void> mappingGraph = MappingGraph.of(
                Node.<Void>builder()
                        .child(Node.<Void>builder()
                                .label(DAY_MENUS)
                                .child(Node.<Void>builder()
                                        .label(DAY_MENU_ITEMS)
                                        .child(Node.<Void>builder()
                                                .label(MEAL)
                                                .child(Node.<Void>builder()
                                                        .label(RECIPE)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build()
        );

        GraphMapper<WeekMenuDTO> mapper = factory.getGraphMapper(mappingGraph, WeekMenuDTO.class);

        Field ctxField = GraphMapper.class.getDeclaredField("ctx");
        ctxField.setAccessible(true);
        assertSame(CONTEXT, ctxField.get(mapper));

        Field dtoClassField = GraphMapper.class.getDeclaredField("dtoClass");
        dtoClassField.setAccessible(true);
        assertEquals(WeekMenuDTO.class, dtoClassField.get(mapper));

        Field classMappingsField = GraphMapper.class.getDeclaredField("classMappings");
        classMappingsField.setAccessible(true);
        Map<Class<?>, ClassMapping> classMappings = (Map<Class<?>, ClassMapping>) classMappingsField.get(mapper);

        ClassMapping currentMapping = classMappings.get(RootMapping.class);

        assertEquals(WeekMenuDTO.class, currentMapping.getDtoClass());

        Reference currentReference = currentMapping.getReferences().get(0);

        //Day Menus
        ListNodeMapper dayMenusMapper = (ListNodeMapper) currentReference.getNodeMapper();

        Field listNodeMapperReferencesField = ListNodeMapper.class.getDeclaredField("references");
        listNodeMapperReferencesField.setAccessible(true);

        currentReference = ((List<Reference>)listNodeMapperReferencesField.get(dayMenusMapper)).get(0);

        //Day Menu Items
        ListNodeMapper dayMenuItemsMapper = (ListNodeMapper) currentReference.getNodeMapper();

        currentReference = ((List<Reference>)listNodeMapperReferencesField.get(dayMenuItemsMapper)).get(0);

        //Meal
        DynamicObjectNodeMapper mealMapper = (DynamicObjectNodeMapper) currentReference.getNodeMapper();

        Field dynamicObjectNodeMapperReferencesField = DynamicObjectNodeMapper.class.getDeclaredField("classMappings");
        dynamicObjectNodeMapperReferencesField.setAccessible(true);
        classMappings = (Map<Class<?>, ClassMapping>) dynamicObjectNodeMapperReferencesField.get(mealMapper);

        //Recipe
        assertNotNull(classMappings.get(LunchEntity.class));
        assertNotNull(classMappings.get(SoupEntity.class));

        assertNotNull(classMappings.get(LunchEntity.class).getReferences().get(0).getNodeMapper());
        assertNotNull(classMappings.get(LunchEntity.class).getReferences().get(0).getNodeMapper());
    }

    @Test
    public void graphMapperForDynamicObjectIsBuilt() throws NoSuchFieldException, IllegalAccessException {

        GraphMapperFactory factory = new GraphMapperFactory(CONTEXT);

        MappingGraph<Void> mappingGraph = MappingGraph.of(
                Node.<Void>builder()
                        .child(Node.<Void>builder()
                                .label(RECIPE)
                                .build())
                        .build()
        );

        GraphMapper<Meal> mapper = factory.getGraphMapper(mappingGraph, Meal.class);

        Field ctxField = GraphMapper.class.getDeclaredField("ctx");
        ctxField.setAccessible(true);
        assertSame(CONTEXT, ctxField.get(mapper));

        Field classMappingsField = GraphMapper.class.getDeclaredField("classMappings");
        classMappingsField.setAccessible(true);
        Map<Class<?>, ClassMapping> classMappings = (Map<Class<?>, ClassMapping>) classMappingsField.get(mapper);

        assertNotNull(classMappings.get(LunchEntity.class));
        assertNotNull(classMappings.get(SoupEntity.class));

        assertNotNull(classMappings.get(LunchEntity.class).getReferences().get(0).getNodeMapper());
        assertNotNull(classMappings.get(LunchEntity.class).getReferences().get(0).getNodeMapper());
    }

    private GraphMapperContext initContext() {
        try {
            GraphMapperContext ctx = new GraphMapperContext((object) -> object);

            ctx.defineInterface(Meal.class, MealEntity.class,
                    ClassNode.of(LunchDTO.class, LunchEntity.class),
                    ClassNode.of(SoupDTO.class, SoupEntity.class)
            );

            ctx.addMapping(WeekMenuDTO.class, WeekMenuEntity.class)
                    .bind(PROVIDER)
                    .bind(CUSTOMER)
                    .bindList(DAY_MENUS);

            ctx.addMapping(DayMenuDTO.class, DayMenuEntity.class)
                    .bindList(DAY_MENU_ITEMS);

            ctx.addMapping(DayMenuItemDTO.class, DayMenuItemEntity.class)
                    .bind(MEAL)
                    .bind(MEAL_TYPE);

            ctx.addMapping(LunchDTO.class, LunchEntity.class)
                    .bind(MEAL_TYPE)
                    .bind(RECIPE)
                    .bind(SIDE_DISH);

            ctx.addMapping(SoupDTO.class, SoupEntity.class)
                    .bind(MEAL_TYPE)
                    .bind(RECIPE);

            ctx.addMapping(RecipeDTO.class, RecipeEntity.class)
                    .bindSet(VARIANTS);

            return ctx;
        } catch (GraphMapperInitializationException e) {
            return null;
        }
    }

}
