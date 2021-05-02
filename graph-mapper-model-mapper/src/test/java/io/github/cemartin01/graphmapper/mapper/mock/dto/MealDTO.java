package io.github.cemartin01.graphmapper.mapper.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealDTO {

    private UUID id;

    private MealTypeDTO mealType;

    private String name;

    private boolean active;

}
