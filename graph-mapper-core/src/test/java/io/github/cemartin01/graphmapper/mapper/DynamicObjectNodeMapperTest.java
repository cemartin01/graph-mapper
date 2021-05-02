package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.cemartin01.graphmapper.mock.dto.LunchDTO;
import io.github.cemartin01.graphmapper.mock.dto.MealTypeDTO;
import io.github.cemartin01.graphmapper.mock.dto.SoupDTO;
import io.github.cemartin01.graphmapper.mock.entity.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DynamicObjectNodeMapperTest {

    @Test
    public void dynamicObjectIsMapped() throws InvocationTargetException, IllegalAccessException {

        DynamicObjectNodeMapper nodeMapper = prepareMealMapper();

        SoupEntity soupEntity = new SoupEntity();
        soupEntity.setMealType(new MealTypeEntity());

        LunchEntity lunchEntity = new LunchEntity();
        lunchEntity.setMealType(new MealTypeEntity());

        DayMenuItemEntity dayMenuItemEntity = new DayMenuItemEntity();
        dayMenuItemEntity.setMeal(soupEntity);

        SoupDTO soup = (SoupDTO) nodeMapper.map(dayMenuItemEntity);

        assertNotNull(soup);
        assertNotNull(soup.getMealType());

        dayMenuItemEntity = new DayMenuItemEntity();
        dayMenuItemEntity.setMeal(lunchEntity);

        LunchDTO lunch = (LunchDTO) nodeMapper.map(dayMenuItemEntity);

        assertNotNull(lunch);
        assertNotNull(lunch.getMealType());
    }

    @Test
    public void nullDynamicObjectIsHandled() throws InvocationTargetException, IllegalAccessException {

        DynamicObjectNodeMapper nodeMapper = prepareMealMapper();

        DayMenuItemEntity dayMenuItemEntity = new DayMenuItemEntity();
        dayMenuItemEntity.setMeal(null);

        Object nullObject = nodeMapper.map(dayMenuItemEntity);

        assertNull(nullObject);
    }

    private DynamicObjectNodeMapper prepareMealMapper() {

        GraphMapperContext ctx = new GraphMapperContext(object -> object);
        ctx.addMapper((e) -> new LunchDTO(), LunchDTO.class);
        ctx.addMapper((e) -> new SoupDTO(), SoupDTO.class);
        ctx.addMapper((e) -> new MealTypeDTO(), MealTypeDTO.class);

        Function<MealEntity, MealTypeEntity> mealTypeGetter = MealEntity::getMealType;

        ObjectNodeMapper mealTypeMapper = new ObjectNodeMapper(
                ctx,
                mealTypeGetter,
                MealTypeDTO.class,
                ImmutableList.of()
        );

        //SoupDTO
        BiConsumer<SoupDTO, MealTypeDTO> mealTypeSoupSetter = SoupDTO::setMealType;
        Reference mealTypeSoupReference = new Reference(mealTypeSoupSetter, mealTypeMapper);

        //LunchDTO
        BiConsumer<LunchDTO, MealTypeDTO> mealTypeLunchSetter = LunchDTO::setMealType;
        Reference mealTypeLunchReference = new Reference(mealTypeLunchSetter, mealTypeMapper);

        Function<DayMenuItemEntity, MealEntity> getter = DayMenuItemEntity::getMeal;

        return new DynamicObjectNodeMapper (
                ctx,
                getter,
                ImmutableMap.<Class<?>, ClassMapping>builder()
                        .put(LunchEntity.class, new ClassMapping(LunchDTO.class, Collections.singletonList(mealTypeLunchReference)))
                        .put(SoupEntity.class, new ClassMapping(SoupDTO.class, Collections.singletonList(mealTypeSoupReference)))
                        .build()
        );
    }

}
