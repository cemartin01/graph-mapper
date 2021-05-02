package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableList;
import io.github.cemartin01.graphmapper.mock.dto.SoupDTO;
import io.github.cemartin01.graphmapper.mock.entity.MealEntity;
import io.github.cemartin01.graphmapper.mock.entity.RecipeEntity;
import io.github.cemartin01.graphmapper.mock.entity.SoupEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SetNodeMapperTest {

    @Test
    public void setIsMapped() throws InvocationTargetException, IllegalAccessException {

        SetNodeMapper nodeMapper = prepareSoupMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.getVariants().add(new SoupEntity());

        List<SoupDTO> soups = (List<SoupDTO>) nodeMapper.map(recipeEntity);

        assertNotNull(soups);
        assertNotNull(soups.get(0));

    }

    @Test
    public void nullSetIsHandled() throws InvocationTargetException, IllegalAccessException {

        SetNodeMapper nodeMapper = prepareSoupMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setVariantsToNull();

        List<SoupDTO> soups = (List<SoupDTO>) nodeMapper.map(recipeEntity);

        assertNull(soups);
    }

    @Test
    public void nullObjectInSetHandled() throws InvocationTargetException, IllegalAccessException {
        SetNodeMapper nodeMapper = prepareSoupMapper();

        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.getVariants().add(null);

        List<SoupDTO> soups = (List<SoupDTO>) nodeMapper.map(recipeEntity);

        assertNotNull(soups);
        assertNull(soups.get(0));

    }

    private SetNodeMapper prepareSoupMapper() {

        GraphMapperContext ctx = new GraphMapperContext(object -> object);
        ctx.addMapper((e) -> new SoupDTO(), SoupDTO.class);

        Function<RecipeEntity, Set<MealEntity>> getter = RecipeEntity::getVariants;

        return new SetNodeMapper (
                ctx,
                getter,
                SoupDTO.class,
                ImmutableList.of()
        );
    }

}
