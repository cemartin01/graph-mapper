package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.cemartin01.graphmapper.mock.dto.LunchDTO;
import io.github.cemartin01.graphmapper.mock.dto.SoupDTO;
import io.github.cemartin01.graphmapper.mock.entity.LunchEntity;
import io.github.cemartin01.graphmapper.mock.entity.MealEntity;
import io.github.cemartin01.graphmapper.mock.entity.RecipeEntity;
import io.github.cemartin01.graphmapper.mock.entity.SoupEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class HeterogeneousSetNodeMapperTest {

    @Test
    public void heterogeneousSetIsMapped() throws InvocationTargetException, IllegalAccessException {

        HeterogeneousSetNodeMapper nodeMapper = prepareMealMapper();

        SoupEntity soupEntity = new SoupEntity();

        LunchEntity lunchEntity = new LunchEntity();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.getVariants().add(soupEntity);
        recipeEntity.getVariants().add(lunchEntity);

        List<Object> variants = (List<Object>) nodeMapper.map(recipeEntity);

        assertNotNull(variants);

        boolean containsLunch = false;
        boolean containsSoup = false;

        assertEquals(2, variants.size());

        for (Object variant : variants) {
            assertNotNull(variant);
            if (variant instanceof LunchDTO) {
                containsLunch = true;
            }
            if (variant instanceof SoupDTO) {
                containsSoup = true;
            }
        }

        assertTrue(containsLunch);
        assertTrue(containsSoup);
    }

    @Test
    public void nullHeterogeneousSetIsMapped() throws InvocationTargetException, IllegalAccessException {

        HeterogeneousSetNodeMapper nodeMapper = prepareMealMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setVariantsToNull();

        List<Object> variants = (List<Object>) nodeMapper.map(recipeEntity);

        assertNull(variants);
    }

    @Test
    public void nullInHeterogeneousSetIsMapped() throws InvocationTargetException, IllegalAccessException {

        HeterogeneousSetNodeMapper nodeMapper = prepareMealMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.getVariants().add(null);

        List<Object> variants = (List<Object>) nodeMapper.map(recipeEntity);

        assertNotNull(variants);
        assertNull(variants.get(0));

    }

    private HeterogeneousSetNodeMapper prepareMealMapper() {

        GraphMapperContext ctx = new GraphMapperContext(object -> object);
        ctx.addMapper((e) -> new LunchDTO(), LunchDTO.class);
        ctx.addMapper((e) -> new SoupDTO(), SoupDTO.class);

        Function<RecipeEntity, Set<MealEntity>> getter = RecipeEntity::getVariants;

        return new HeterogeneousSetNodeMapper (
                ctx,
                getter,
                ImmutableMap.<Class<?>, ClassMapping>builder()
                        .put(LunchEntity.class, new ClassMapping(LunchDTO.class, ImmutableList.of()))
                        .put(SoupEntity.class, new ClassMapping(SoupDTO.class, ImmutableList.of()))
                        .build()
        );
    }

}
