package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableList;
import io.github.cemartin01.graphmapper.mock.dto.MealTypeDTO;
import io.github.cemartin01.graphmapper.mock.dto.SoupDTO;
import io.github.cemartin01.graphmapper.mock.entity.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ObjectNodeMapperTest {

    @Test
    public void objectIsMapped() throws InvocationTargetException, IllegalAccessException {

        ObjectNodeMapper nodeMapper = prepareSoupMapper();

        SoupEntity soupEntity = new SoupEntity();
        soupEntity.setMealType(new MealTypeEntity());

        DayMenuItemEntity dayMenuItemEntity = new DayMenuItemEntity();
        dayMenuItemEntity.setMeal(soupEntity);

        SoupDTO soupDTO = (SoupDTO) nodeMapper.map(dayMenuItemEntity);

        assertNotNull(soupDTO);
        assertNotNull(soupDTO.getMealType());
    }

    @Test
    public void nullObjectIsHandled() throws InvocationTargetException, IllegalAccessException {

        ObjectNodeMapper nodeMapper = prepareSoupMapper();

        DayMenuItemEntity dayMenuItemEntity = new DayMenuItemEntity();
        dayMenuItemEntity.setMeal(null);

        SoupDTO soupDTO = (SoupDTO) nodeMapper.map(dayMenuItemEntity);

        assertNull(soupDTO);
    }

    private ObjectNodeMapper prepareSoupMapper() {

        GraphMapperContext ctx = new GraphMapperContext(object -> object);
        ctx.addMapper((e) -> new SoupDTO(), SoupDTO.class);
        ctx.addMapper((e) -> new MealTypeDTO(), MealTypeDTO.class); 

        Function<SoupEntity, MealTypeEntity> mealTypeGetter = SoupEntity::getMealType;

        ObjectNodeMapper mealTypeMapper = new ObjectNodeMapper(
                ctx,
                mealTypeGetter,
                MealTypeDTO.class,
                ImmutableList.of()
        );

        BiConsumer<SoupDTO, MealTypeDTO> setter = SoupDTO::setMealType;

        Reference mealTypeReference = new Reference(setter, mealTypeMapper);

        Function<DayMenuItemEntity, MealEntity> getter = DayMenuItemEntity::getMeal;

        return new ObjectNodeMapper(
                ctx,
                getter,
                SoupDTO.class,
                ImmutableList.of(mealTypeReference)
        );
    }

}
