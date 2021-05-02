package io.github.cemartin01.graphmapper.mapper.mock;

import io.github.cemartin01.graphmapper.NodeLabel;

public enum ModelMapperTestNodeLabel implements NodeLabel {

    MEAL_TYPE("mealType"),
    DAY_MENU_ITEMS("items"),
    DAY_MENU_ITEM_SET("itemSet"),
    MEAL("meal"),
    DAY_MENUS("days");

    private final String name;

    ModelMapperTestNodeLabel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
