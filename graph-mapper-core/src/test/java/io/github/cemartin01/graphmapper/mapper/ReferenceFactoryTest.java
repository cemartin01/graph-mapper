package io.github.cemartin01.graphmapper.mapper;

import com.google.common.collect.ImmutableMap;
import io.github.cemartin01.graphmapper.mock.dto.LunchDTO;
import io.github.cemartin01.graphmapper.mock.dto.RecipeDTO;
import io.github.cemartin01.graphmapper.mock.dto.SoupDTO;
import io.github.cemartin01.graphmapper.mock.dto.WeekMenuDTO;
import io.github.cemartin01.graphmapper.mock.entity.LunchEntity;
import io.github.cemartin01.graphmapper.mock.entity.SoupEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.github.cemartin01.graphmapper.mock.CateringNodeLabel.RECIPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ReferenceFactoryTest {

    private final GraphMapperContext CONTEXT = new GraphMapperContext(object -> object);

    @Test
    public void objectNodeMapperIsCreated() throws IllegalAccessException, NoSuchFieldException {
        ReferenceFactory factory = new ReferenceFactory(CONTEXT);
        ReferenceTemplate referenceTemplate = prepareReferenceTemplate(NodeMapperTemplate.ReferenceType.OBJECT);

        List<Reference> references = new ArrayList<>();
        Map<Class<?>, ClassMapping> mappings = prepareSingletonMapping(references);

        Reference r = factory.getReference(referenceTemplate, mappings);

        assertSame(referenceTemplate.getSetter(), r.getSetter());
        assertEquals(ObjectNodeMapper.class, r.getNodeMapper().getClass());
        ObjectNodeMapper nodeMapper = (ObjectNodeMapper) r.getNodeMapper();

        Field ctxField = ObjectNodeMapper.class.getDeclaredField("ctx");
        ctxField.setAccessible(true);
        assertSame(CONTEXT, ctxField.get(nodeMapper));

        Field getterField = ObjectNodeMapper.class.getDeclaredField("getter");
        getterField.setAccessible(true);
        assertSame(referenceTemplate.nodeMapperTemplate.getGetter(), getterField.get(nodeMapper));

        Field dtoClassField = ObjectNodeMapper.class.getDeclaredField("dtoClass");
        dtoClassField.setAccessible(true);
        assertSame(referenceTemplate.nodeMapperTemplate.getDtoClass(), dtoClassField.get(nodeMapper));

        Field referencesField = ObjectNodeMapper.class.getDeclaredField("references");
        referencesField.setAccessible(true);
        assertSame(references, referencesField.get(nodeMapper));
    }

    @Test
    public void listNodeMapperIsCreated() {
        ReferenceFactory factory = new ReferenceFactory(CONTEXT);
        ReferenceTemplate referenceTemplate = prepareReferenceTemplate(NodeMapperTemplate.ReferenceType.LIST);

        List<Reference> references = new ArrayList<>();
        Map<Class<?>, ClassMapping> mappings = prepareSingletonMapping(references);

        Reference r = factory.getReference(referenceTemplate, mappings);
        assertSame(referenceTemplate.getSetter(), r.getSetter());
        assertEquals(ListNodeMapper.class, r.getNodeMapper().getClass());
    }

    @Test
    public void setNodeMapperIsCreated() {
        ReferenceFactory factory = new ReferenceFactory(CONTEXT);
        ReferenceTemplate referenceTemplate = prepareReferenceTemplate(NodeMapperTemplate.ReferenceType.SET);

        List<Reference> references = new ArrayList<>();
        Map<Class<?>, ClassMapping> mappings = prepareSingletonMapping(references);

        Reference r = factory.getReference(referenceTemplate, mappings);
        assertSame(referenceTemplate.getSetter(), r.getSetter());
        assertEquals(SetNodeMapper.class, r.getNodeMapper().getClass());
    }

    private ReferenceTemplate prepareReferenceTemplate(NodeMapperTemplate.ReferenceType referenceType) {
        Function getter = (object) -> object;

        NodeMapperTemplate nodeMapperTemplate = new NodeMapperTemplate(
                getter, WeekMenuDTO.class, referenceType
        );

        BiConsumer setter = (object, item) -> {};

        ReferenceTemplate referenceTemplate = new ReferenceTemplate(
                RECIPE, setter, nodeMapperTemplate
        );

        return referenceTemplate;
    }

    private Map<Class<?>, ClassMapping> prepareSingletonMapping(List<Reference> references) {
        return Collections.singletonMap(RootMapping.class,
                new ClassMapping(RecipeDTO.class, references)
        );
    }

    @Test
    public void dynamicObjectNodeMapperIsCreated() throws NoSuchFieldException, IllegalAccessException {
        ReferenceFactory factory = new ReferenceFactory(CONTEXT);
        ReferenceTemplate referenceTemplate = prepareReferenceTemplate(NodeMapperTemplate.ReferenceType.OBJECT);

        List<Reference> references = new ArrayList<>();
        Map<Class<?>, ClassMapping> mappings = prepareMappings(references);

        Reference r = factory.getReference(referenceTemplate, mappings);
        assertSame(referenceTemplate.getSetter(), r.getSetter());
        assertEquals(DynamicObjectNodeMapper.class, r.getNodeMapper().getClass());

        DynamicObjectNodeMapper nodeMapper = (DynamicObjectNodeMapper) r.getNodeMapper();

        Field ctxField = DynamicObjectNodeMapper.class.getDeclaredField("ctx");
        ctxField.setAccessible(true);
        assertSame(CONTEXT, ctxField.get(nodeMapper));

        Field getterField = DynamicObjectNodeMapper.class.getDeclaredField("getter");
        getterField.setAccessible(true);
        assertSame(referenceTemplate.nodeMapperTemplate.getGetter(), getterField.get(nodeMapper));

        Field classMappingsField = DynamicObjectNodeMapper.class.getDeclaredField("classMappings");
        classMappingsField.setAccessible(true);
        assertSame(mappings, classMappingsField.get(nodeMapper));
    }

    @Test
    public void heterogeneousListNodeMapperIsCreated() {
        ReferenceFactory factory = new ReferenceFactory(CONTEXT);
        ReferenceTemplate referenceTemplate = prepareReferenceTemplate(NodeMapperTemplate.ReferenceType.LIST);

        List<Reference> references = new ArrayList<>();
        Map<Class<?>, ClassMapping> mappings = prepareMappings(references);

        Reference r = factory.getReference(referenceTemplate, mappings);
        assertSame(referenceTemplate.getSetter(), r.getSetter());
        assertEquals(HeterogeneousListNodeMapper.class, r.getNodeMapper().getClass());
    }

    @Test
    public void heterogeneousSetNodeMapperIsCreated() {
        ReferenceFactory factory = new ReferenceFactory(CONTEXT);
        ReferenceTemplate referenceTemplate = prepareReferenceTemplate(NodeMapperTemplate.ReferenceType.SET);

        List<Reference> references = new ArrayList<>();
        Map<Class<?>, ClassMapping> mappings = prepareMappings(references);

        Reference r = factory.getReference(referenceTemplate, mappings);
        assertSame(referenceTemplate.getSetter(), r.getSetter());
        assertEquals(HeterogeneousSetNodeMapper.class, r.getNodeMapper().getClass());
    }

    private Map<Class<?>, ClassMapping> prepareMappings(List<Reference> references) {
        return ImmutableMap.<Class<?>, ClassMapping>builder()
                .put(LunchEntity.class, new ClassMapping(LunchDTO.class, references))
                .put(SoupEntity.class, new ClassMapping(SoupDTO.class, references))
                .build();
    }

}
