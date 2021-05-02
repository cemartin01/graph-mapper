package io.github.cemartin01.graphmapper.mapper;

import io.github.cemartin01.graphmapper.classtree.ClassNode;
import io.github.cemartin01.graphmapper.exception.GraphMapperInitializationException;
import io.github.cemartin01.graphmapper.mapper.mock.dto.*;
import io.github.cemartin01.graphmapper.mapper.mock.entity.*;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.lang.reflect.Field;

import static io.github.cemartin01.graphmapper.mapper.mock.ModelMapperTestNodeLabel.*;
import static org.junit.jupiter.api.Assertions.*;

public class ModelMapperBasedGraphMapperContextTest {

    @Test
    public void contextIsBuilt() throws GraphMapperInitializationException, NoSuchFieldException, IllegalAccessException {

        ModelMapper modelMapper = new ModelMapper();

        ModelMapperBasedGraphMapperContext ctx = new ModelMapperBasedGraphMapperContext(modelMapper, object -> object);

        ctx.defineClassHierarchy(MealDTO.class, MealEntity.class,
                ClassNode.of(LunchDTO.class, LunchEntity.class),
                ClassNode.of(SoupDTO.class, SoupEntity.class)
        );

        ctx.addMapping(WeekMenuDTO.class, WeekMenuEntity.class)
                .bindList(DAY_MENUS, WeekMenuEntity::getDays, WeekMenuDTO::setDays);

        ctx.addMapping(DayMenuDTO.class, DayMenuEntity.class)
                .bindList(DAY_MENU_ITEMS, DayMenuEntity::getItems, DayMenuDTO::setItems)
                .bindSet(DAY_MENU_ITEM_SET, DayMenuEntity::getItemSet, DayMenuDTO::setItemSet);

        ctx.addMapping(DayMenuItemDTO.class, DayMenuItemEntity.class)
                .bind(MEAL, DayMenuItemEntity::getMeal, DayMenuItemDTO::setMeal)
                .bind(MEAL_TYPE, DayMenuItemEntity::getMealType, DayMenuItemDTO::setMealType);

        ctx.addMapping(MealDTO.class, MealEntity.class)
                .bind(MEAL_TYPE, MealEntity::getMealType, MealDTO::setMealType);

        ctx.addMapping(LunchDTO.class, LunchEntity.class)
                .includeBaseBinding(MealDTO.class, MealEntity.class);

        ctx.addMapping(SoupDTO.class, SoupEntity.class)
                .includeBaseBinding(MealDTO.class, MealEntity.class);

        Field skipFunctionField = ModelMapperBasedGraphMapperContext.class.getDeclaredField("skipMappingFunction");
        skipFunctionField.setAccessible(true);
        Object skipFunction = skipFunctionField.get(ctx);

        TypeMap<WeekMenuEntity, WeekMenuDTO> weekMenuTypeMap = modelMapper.getTypeMap(WeekMenuEntity.class, WeekMenuDTO.class);

        assertSame(skipFunction, weekMenuTypeMap.getMappings().get(0).getCondition());

        TypeMap<DayMenuEntity, DayMenuDTO> dayMenuTypeMap = modelMapper.getTypeMap(DayMenuEntity.class, DayMenuDTO.class);

        assertSame(skipFunction, dayMenuTypeMap.getMappings().get(3).getCondition());
        assertSame(skipFunction, dayMenuTypeMap.getMappings().get(4).getCondition());

        TypeMap<DayMenuItemEntity, DayMenuItemDTO> dayMenuItemTypeMap = modelMapper.getTypeMap(DayMenuItemEntity.class, DayMenuItemDTO.class);

        assertSame(skipFunction, dayMenuItemTypeMap.getMappings().get(1).getCondition());

        TypeMap<MealEntity, MealDTO> mealTypeMap = modelMapper.getTypeMap(MealEntity.class, MealDTO.class);

        assertSame(skipFunction, mealTypeMap.getMappings().get(2).getCondition());

        TypeMap<LunchEntity, LunchDTO> lunchTypeMap = modelMapper.getTypeMap(LunchEntity.class, LunchDTO.class);

        assertSame(skipFunction, lunchTypeMap.getMappings().get(2).getCondition());

        TypeMap<SoupEntity, SoupDTO> soupTypeMap = modelMapper.getTypeMap(SoupEntity.class, SoupDTO.class);

        assertSame(skipFunction, soupTypeMap.getMappings().get(2).getCondition());
    }

    @Test
    public void objectIsMapped() throws GraphMapperInitializationException {

        ModelMapper modelMapper = new ModelMapper();
        ModelMapperBasedGraphMapperContext ctx = new ModelMapperBasedGraphMapperContext(modelMapper, object -> object);

        ctx.addMapping(DayMenuItemDTO.class, DayMenuItemEntity.class)
                .bind(MEAL_TYPE, DayMenuItemEntity::getMealType, DayMenuItemDTO::setMealType);

        DayMenuItemEntity entity = new DayMenuItemEntity();
        entity.setMealType(new MealTypeEntity());

        DayMenuItemDTO dto = (DayMenuItemDTO) ctx.map(entity, DayMenuItemDTO.class);
        assertNull(dto.getMealType());
    }

}
