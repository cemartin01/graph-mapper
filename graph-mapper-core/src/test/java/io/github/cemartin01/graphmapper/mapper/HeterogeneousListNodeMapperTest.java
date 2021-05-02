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
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class HeterogeneousListNodeMapperTest {

    @Test
    public void heterogeneousListIsMapped() throws InvocationTargetException, IllegalAccessException {

        HeterogeneousListNodeMapper nodeMapper = prepareMealMapper();

        SoupEntity soupEntity = new SoupEntity();
        soupEntity.setMealType(new MealTypeEntity());

        LunchEntity lunchEntity = new LunchEntity();
        lunchEntity.setMealType(new MealTypeEntity());

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.getListedVariants().add(soupEntity);
        recipeEntity.getListedVariants().add(lunchEntity);

        List<Object> variants = (List<Object>) nodeMapper.map(recipeEntity);

        assertNotNull(variants);

        assertNotNull(variants.get(0));
        assertNotNull(variants.get(1));

        assertEquals(SoupDTO.class, variants.get(0).getClass());
        assertEquals(LunchDTO.class, variants.get(1).getClass());

        assertNotNull(((SoupDTO)variants.get(0)).getMealType());
        assertNotNull(((LunchDTO)variants.get(1)).getMealType());
    }

    @Test
    public void nullHeterogeneousListIsMapped() throws InvocationTargetException, IllegalAccessException {

        HeterogeneousListNodeMapper nodeMapper = prepareMealMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setListedVariantsToNull();

        List<Object> variants = (List<Object>) nodeMapper.map(recipeEntity);

        assertNull(variants);
    }

    @Test
    public void nullInHeterogeneousListIsMapped() throws InvocationTargetException, IllegalAccessException {

        HeterogeneousListNodeMapper nodeMapper = prepareMealMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.getListedVariants().add(null);

        List<Object> variants = (List<Object>) nodeMapper.map(recipeEntity);

        assertNotNull(variants);

        assertNull(variants.get(0));
    }

    private HeterogeneousListNodeMapper prepareMealMapper() {

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

        Function<RecipeEntity, List<MealEntity>> getter = RecipeEntity::getListedVariants;

        return new HeterogeneousListNodeMapper (
                ctx,
                getter,
                ImmutableMap.<Class<?>, ClassMapping>builder()
                        .put(LunchEntity.class, new ClassMapping(LunchDTO.class, Collections.singletonList(mealTypeLunchReference)))
                        .put(SoupEntity.class, new ClassMapping(SoupDTO.class, Collections.singletonList(mealTypeSoupReference)))
                        .build()
        );
    }

}
