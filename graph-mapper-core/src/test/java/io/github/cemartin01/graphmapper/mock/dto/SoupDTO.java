package io.github.cemartin01.graphmapper.mock.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SoupDTO implements Meal, MealUnion {

   private UUID id;

   private RecipeDTO recipe;

   private MealTypeDTO mealType;

   private String name;

   private boolean active;

}