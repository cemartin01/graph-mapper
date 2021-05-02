package io.github.cemartin01.graphmapper.mock;

import io.github.cemartin01.graphmapper.NodeLabel;

public enum CateringNodeLabel implements NodeLabel {

    VARIANTS("variants"),
    LISTED_VARIANTS("listedVariants"),
    MEAL_TYPE("mealType"),
    RECIPE("recipe"),
    DAY_MENU_ITEMS("items"),
    MEAL("meal"),
    DAY_MENUS("days"),
    PROVIDER("provider"),
    CUSTOMER("customer"),
    SIDE_DISH("sideDish");

    private final String name;

    CateringNodeLabel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
