package io.github.cemartin01.graphmapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DayMenuItemEntity extends CateringEntity {

   private DayMenuEntity dayMenu;

   private MealEntity meal;

   private MealTypeEntity mealType;

   private byte position;

}