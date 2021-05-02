package io.github.cemartin01.graphmapper.graphql.mock.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EnumBindingInput {

    private MealGroup mealGroup;

    private List<MealGroup> mealGroups;

}
