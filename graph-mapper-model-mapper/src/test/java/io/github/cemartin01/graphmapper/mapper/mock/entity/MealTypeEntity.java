package io.github.cemartin01.graphmapper.mapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MealTypeEntity extends CateringEntity {

   private String name;

   private boolean active;

}