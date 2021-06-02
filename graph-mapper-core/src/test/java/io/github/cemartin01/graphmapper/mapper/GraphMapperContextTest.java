package io.github.cemartin01.graphmapper.mapper;

import io.github.cemartin01.graphmapper.classtree.ClassNode;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;
import io.github.cemartin01.graphmapper.mock.CateringNodeLabel;
import io.github.cemartin01.graphmapper.mock.dto.*;
import io.github.cemartin01.graphmapper.mock.entity.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.github.cemartin01.graphmapper.mock.CateringNodeLabel.*;
import static org.junit.jupiter.api.Assertions.*;

public class GraphMapperContextTest {

    @Test
    public void bindingsAreAdded() throws GraphMapperInitializationException {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

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

        System.out.println();

        List<ReferenceTemplate> weekMenuReferenceTemplates = ctx.getReferenceTemplates(WeekMenuDTO.class);
        assertNotNull(weekMenuReferenceTemplates);
        assertEquals(3, weekMenuReferenceTemplates.size());

        List<ReferenceTemplate> dayMenuReferenceTemplates = ctx.getReferenceTemplates(DayMenuDTO.class);
        assertNotNull(dayMenuReferenceTemplates);
        assertEquals(1, dayMenuReferenceTemplates.size());

        List<ReferenceTemplate> dayMenuItemReferenceTemplates = ctx.getReferenceTemplates(DayMenuItemDTO.class);
        assertNotNull(dayMenuItemReferenceTemplates);
        assertEquals(2, dayMenuItemReferenceTemplates.size());

        List<ReferenceTemplate> lunchReferenceTemplates = ctx.getReferenceTemplates(LunchDTO.class);
        assertNotNull(lunchReferenceTemplates);
        assertEquals(3, lunchReferenceTemplates.size());

        List<ReferenceTemplate> soupReferenceTemplates = ctx.getReferenceTemplates(SoupDTO.class);
        assertNotNull(soupReferenceTemplates);
        assertEquals(2, soupReferenceTemplates.size());

        List<ReferenceTemplate> recipeReferenceTemplates = ctx.getReferenceTemplates(RecipeDTO.class);
        assertNotNull(recipeReferenceTemplates);
        assertEquals(1, recipeReferenceTemplates.size());

        //Test setter and getter of an Object
        ReferenceTemplate mealTypeReference = lunchReferenceTemplates.get(0);
        assertEquals(CateringNodeLabel.MEAL_TYPE, mealTypeReference.getNodeLabel());
        assertEquals(NodeMapperTemplate.ReferenceType.OBJECT, mealTypeReference.getNodeMapperTemplate().getReferenceType());
        assertEquals(MealTypeDTO.class, mealTypeReference.getNodeMapperTemplate().getTargetClass());

        LunchDTO lunchDTO = new LunchDTO();
        MealTypeDTO mealTypeDTO = new MealTypeDTO();

        mealTypeReference.getSetter().accept(lunchDTO, mealTypeDTO);

        assertSame(mealTypeDTO, lunchDTO.getMealType());

        MealTypeEntity mealTypeEntity = new MealTypeEntity();

        LunchEntity lunchEntity = new LunchEntity();
        lunchEntity.setMealType(mealTypeEntity);

        assertSame(mealTypeEntity, mealTypeReference.getNodeMapperTemplate().getGetter().apply(lunchEntity));

        //Test setter and getter of a List
        ReferenceTemplate dayMenusReference = weekMenuReferenceTemplates.get(2);
        assertEquals(DAY_MENUS, dayMenusReference.getNodeLabel());
        assertEquals(NodeMapperTemplate.ReferenceType.LIST, dayMenusReference.getNodeMapperTemplate().getReferenceType());
        assertEquals(DayMenuDTO.class, dayMenusReference.getNodeMapperTemplate().getTargetClass());

        WeekMenuDTO weekMenuDTO = new WeekMenuDTO();
        List<DayMenuDTO> dayMenus = new ArrayList<>();

        dayMenusReference.getSetter().accept(weekMenuDTO, dayMenus);

        assertSame(dayMenus, weekMenuDTO.getDays());

        WeekMenuEntity weekMenuEntity = new WeekMenuEntity();
        List<DayMenuEntity> dayMenuEntities = weekMenuEntity.getDays();

        assertSame(dayMenuEntities, dayMenusReference.getNodeMapperTemplate().getGetter().apply(weekMenuEntity));

        //Test setter nad getter of a Set
        ReferenceTemplate variantsReference = recipeReferenceTemplates.get(0);
        assertEquals(VARIANTS, variantsReference.getNodeLabel());
        assertEquals(NodeMapperTemplate.ReferenceType.SET, variantsReference.getNodeMapperTemplate().getReferenceType());
        assertEquals(Meal.class, variantsReference.getNodeMapperTemplate().getTargetClass());

        RecipeDTO recipeDTO = new RecipeDTO();
        List<Meal> meals = new ArrayList<>();

        variantsReference.getSetter().accept(recipeDTO, meals);

        assertSame(meals, recipeDTO.getVariants());

        RecipeEntity recipeEntity = new RecipeEntity();
        Set<MealEntity> mealEntities = recipeEntity.getVariants();

        assertSame(mealEntities, variantsReference.getNodeMapperTemplate().getGetter().apply(recipeEntity));

        //Should be null
        List<ReferenceTemplate> providerReferenceTemplates = ctx.getReferenceTemplates(ProviderDTO.class);
        assertNull(providerReferenceTemplates);

    }

    @Test
    public void interfaceIsDefined() throws GraphMapperInitializationException {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        ctx.defineInterface(Meal.class, MealEntity.class,
                ClassNode.of(LunchDTO.class, LunchEntity.class),
                ClassNode.of(SoupDTO.class, SoupEntity.class)
        );

        ClassNode classNode = ctx.getClassNode(Meal.class);
        assertNotNull(classNode);

        assertEquals(Meal.class, classNode.getTargetClass());
        assertEquals(MealEntity.class, classNode.getSourceClass());

        List<ClassNode> children = classNode.getChildren();
        assertNotNull(children);
        assertEquals(2, children.size());

        ClassNode lunchNode = children.get(0);
        assertEquals(LunchDTO.class, lunchNode.getTargetClass());
        assertEquals(LunchEntity.class, lunchNode.getSourceClass());

        ClassNode soupNode = children.get(1);
        assertEquals(SoupDTO.class, soupNode.getTargetClass());
        assertEquals(SoupEntity.class, soupNode.getSourceClass());

    }

    @Test
    public void unionIsDefined() throws GraphMapperInitializationException {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        ctx.defineUnion(MealUnion.class, MealEntity.class,
                ClassNode.of(LunchDTO.class, LunchEntity.class),
                ClassNode.of(SoupDTO.class, SoupEntity.class)
        );

        ClassNode classNode = ctx.getClassNode(MealUnion.class);
        assertNotNull(classNode);

        assertEquals(MealUnion.class, classNode.getTargetClass());
        assertEquals(MealEntity.class, classNode.getSourceClass());

        List<ClassNode> children = classNode.getChildren();
        assertNotNull(children);
        assertEquals(2, children.size());

        ClassNode lunchNode = children.get(0);
        assertEquals(LunchDTO.class, lunchNode.getTargetClass());
        assertEquals(LunchEntity.class, lunchNode.getSourceClass());

        ClassNode soupNode = children.get(1);
        assertEquals(SoupDTO.class, soupNode.getTargetClass());
        assertEquals(SoupEntity.class, soupNode.getSourceClass());

    }

    @Test
    public void addingABindingFailedDueToNonExistingMethod() {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        assertThrows(GraphMapperInitializationException.class, () -> {
            ctx.addMapping(WeekMenuDTO.class, WeekMenuEntity.class)
                    .bind(MEAL);
        });

        assertThrows(GraphMapperInitializationException.class, () -> {
            ctx.addMapping(WeekMenuDTO.class, WeekMenuEntity.class)
                    .bindList(MEAL);
        });

        assertThrows(GraphMapperInitializationException.class, () -> {
            ctx.addMapping(WeekMenuDTO.class, WeekMenuEntity.class)
                    .bindSet(MEAL);
        });

    }

    @Test
    public void definingAnInterfaceFailsDueToValidation() {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        assertThrows(GraphMapperInitializationException.class, () -> {
            ctx.defineInterface(LunchDTO.class, LunchEntity.class,
                    ClassNode.of(SoupDTO.class, SoupEntity.class)
            );
        });

    }

    @Test
    public void definingAUnionFailsDueToValidation() {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        assertThrows(GraphMapperInitializationException.class, () -> {
            ctx.defineUnion(Meal.class, LunchEntity.class,
                    ClassNode.of(SoupDTO.class, SoupEntity.class)
            );
        });

    }

    @Test
    public void mapperForClassIsAdded() {

        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        ctx.addMapper((entity) -> {
            CustomerEntity customerEntity = (CustomerEntity) entity;
            CustomerDTO dto = new CustomerDTO();
            dto.setName(customerEntity.getName());
            return dto;
        }, CustomerDTO.class);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName("My Customer");

        CustomerDTO customerDTO = (CustomerDTO) ctx.map(customerEntity, CustomerDTO.class);

        assertNotNull(customerDTO);
        assertEquals("My Customer", customerDTO.getName());

    }

    @Test
    public void objectIsUnproxied() {
        GraphMapperContext ctx = new GraphMapperContext((object) -> object);

        CustomerEntity e = new CustomerEntity();

        assertSame(e, ctx.unproxy(e));
    }

}
