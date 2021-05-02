package io.github.cemartin01.graphmapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public abstract class MealEntity extends CateringEntity {

   private RecipeEntity recipe;

   private MealTypeEntity mealType;

   private String name;

   private boolean active;

}