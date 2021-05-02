package io.github.cemartin01.graphmapper.classtree;

import io.github.cemartin01.graphmapper.mock.dto.LunchDTO;
import io.github.cemartin01.graphmapper.mock.dto.Meal;
import io.github.cemartin01.graphmapper.mock.dto.SoupDTO;
import io.github.cemartin01.graphmapper.mock.entity.LunchEntity;
import io.github.cemartin01.graphmapper.mock.entity.MealEntity;
import io.github.cemartin01.graphmapper.mock.entity.SoupEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassTreeTest {

    @Test
    public void classTreeIsBuilt() {

        ClassTree tree = ClassTree.of(Meal.class, MealEntity.class,
                ClassNode.of(LunchDTO.class, LunchEntity.class)
        );

        ClassNode classNode = tree.getRoot();
        assertNotNull(classNode);

        assertEquals(Meal.class, classNode.getDtoClass());
        assertEquals(MealEntity.class, classNode.getEntityClass());

        List<ClassNode> children = classNode.getChildren();
        assertNotNull(children);
        assertEquals(1, children.size());

        ClassNode lunchNode = children.get(0);
        assertEquals(LunchDTO.class, lunchNode.getDtoClass());
        assertEquals(LunchEntity.class, lunchNode.getEntityClass());

    }

}
