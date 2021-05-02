package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableList;
import io.github.cemartin01.graphmapper.mock.dto.DayMenuDTO;
import io.github.cemartin01.graphmapper.mock.dto.DayMenuItemDTO;
import io.github.cemartin01.graphmapper.mock.entity.DayMenuEntity;
import io.github.cemartin01.graphmapper.mock.entity.DayMenuItemEntity;
import io.github.cemartin01.graphmapper.mock.entity.WeekMenuEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ListNodeMapperTest {

    @Test
    public void listIsMapped() throws InvocationTargetException, IllegalAccessException {

        ListNodeMapper nodeMapper = prepareDayMenuMapper();

        DayMenuEntity dayMenuEntity = new DayMenuEntity();
        dayMenuEntity.getItems().add(new DayMenuItemEntity());

        WeekMenuEntity weekMenuEntity = new WeekMenuEntity();
        weekMenuEntity.getDays().add(dayMenuEntity);

        List<DayMenuDTO> dayMenus = (List<DayMenuDTO>) nodeMapper.map(weekMenuEntity);

        assertNotNull(dayMenus);
        assertNotNull(dayMenus.get(0).getItems());

    }

    @Test
    public void nullListIsHandled() throws InvocationTargetException, IllegalAccessException {

        ListNodeMapper nodeMapper = prepareDayMenuMapper();

        WeekMenuEntity weekMenuEntity = new WeekMenuEntity();
        weekMenuEntity.setDaysToNull();

        List<DayMenuDTO> dayMenus = (List<DayMenuDTO>) nodeMapper.map(weekMenuEntity);

        assertNull(dayMenus);
    }

    @Test
    public void nullObjectInListHandled() throws InvocationTargetException, IllegalAccessException {

        ListNodeMapper nodeMapper = prepareDayMenuMapper();

        WeekMenuEntity weekMenuEntity = new WeekMenuEntity();
        weekMenuEntity.getDays().add(null);

        List<DayMenuDTO> dayMenus = (List<DayMenuDTO>) nodeMapper.map(weekMenuEntity);

        assertNotNull(dayMenus);
        assertNull(dayMenus.get(0));
    }

    private ListNodeMapper prepareDayMenuMapper() {

        GraphMapperContext ctx = new GraphMapperContext(object -> object);
        ctx.addMapper((e) -> new DayMenuDTO(), DayMenuDTO.class);
        ctx.addMapper((e) -> new DayMenuItemDTO(), DayMenuItemDTO.class);

        Function<DayMenuEntity, List<DayMenuItemEntity>> dayMenuItemGetter = DayMenuEntity::getItems;

        ListNodeMapper dayMenuItemMapper = new ListNodeMapper(
                ctx,
                dayMenuItemGetter,
                DayMenuItemDTO.class,
                ImmutableList.of()
        );

        BiConsumer<DayMenuDTO, List<DayMenuItemDTO>> setter = DayMenuDTO::setItems;

        Reference dayMenuItemReference = new Reference(setter, dayMenuItemMapper);

        Function<WeekMenuEntity, List<DayMenuEntity>> getter = WeekMenuEntity::getDays;

        return new ListNodeMapper (
                ctx,
                getter,
                DayMenuDTO.class,
                ImmutableList.of(dayMenuItemReference)
        );
    }

}
