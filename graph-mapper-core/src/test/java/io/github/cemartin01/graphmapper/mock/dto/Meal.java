package io.github.cemartin01.graphmapper.mock.dto;

import java.util.UUID;

public interface Meal {

   UUID getId();

   RecipeDTO getRecipe();

   MealTypeDTO getMealType();

   String getName();

   boolean isActive();

}