package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.cemartin01.graphmapper.mock.dto.*;
import io.github.cemartin01.graphmapper.mock.entity.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GraphMapperTest {

    @Test
    public void topLevelObjectIsMapped() {
        GraphMapperContext ctx = new GraphMapperContext(object -> object);
        ctx.addMapper((e) -> new DayMenuItemDTO(), DayMenuItemDTO.class);
        ctx.addMapper((e) -> new MealTypeDTO(), MealTypeDTO.class);

        Function<DayMenuItemEntity, MealTypeEntity> mealTypeGetter = DayMenuItemEntity::getMealType;

        ObjectNodeMapper mealTypeMapper = new ObjectNodeMapper(
                ctx,
                mealTypeGetter,
                MealTypeDTO.class,
                ImmutableList.of()
        );

        BiConsumer<DayMenuItemDTO, MealTypeDTO> mealTypeSoupSetter = DayMenuItemDTO::setMealType;
        Reference mealTypeReference = new Reference(mealTypeSoupSetter, mealTypeMapper);

        GraphMapper<DayMenuItemDTO> mapper = new GraphMapper<>(
                ctx,
                DayMenuItemDTO.class,
                ImmutableMap.<Class<?>, ClassMapping>builder()
                        .put(RootMapping.class, new ClassMapping(DayMenuItemDTO.class, Collections.singletonList(mealTypeReference)))
                        .build()
        );

        DayMenuItemEntity entity = new DayMenuItemEntity();
        entity.setMealType(new MealTypeEntity());

        DayMenuItemDTO dto = mapper.map(entity);

        assertNotNull(dto);
        assertNotNull(dto.getMealType());
    }

    @Test
    public void topLevelDynamicObjectIsMapped() {
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

        GraphMapper<Meal> mapper = new GraphMapper<>(
                ctx,
                Meal.class,
                ImmutableMap.<Class<?>, ClassMapping>builder()
                        .put(LunchEntity.class, new ClassMapping(LunchDTO.class, Collections.singletonList(mealTypeLunchReference)))
                        .put(SoupEntity.class, new ClassMapping(SoupDTO.class, Collections.singletonList(mealTypeSoupReference)))
                        .build()
        );

        LunchEntity lunch = new LunchEntity();
        lunch.setMealType(new MealTypeEntity());

        Meal lunchDTO = mapper.map(lunch);

        assertNotNull(lunchDTO);
        assertNotNull(lunchDTO.getMealType());

        SoupEntity soup = new SoupEntity();
        soup.setMealType(new MealTypeEntity());

        Meal soupDTO = mapper.map(soup);

        assertNotNull(soupDTO);
        assertNotNull(soupDTO.getMealType());
    }

}
