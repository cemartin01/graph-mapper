package io.github.cemartin01.graphmapper.graphql.mock.dto;

import io.github.cemartin01.graphmapper.graphql.coercing.Code;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealCriteria {

    private String nameContains;

    private Integer maxNutritionValue;

    private Boolean active;

    private Double popularity;

    private MealGroup group;

    private Byte byteValue;

    private Code code;

    private NestedCriteria nestedCriteria;

}
