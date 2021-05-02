package io.github.cemartin01.graphmapper.graphql.mock.dto;

import io.github.cemartin01.graphmapper.graphql.coercing.Code;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ArrayCriteria {

    private List<String> nameContains;

    private List<Integer> maxNutritionValue;

    private List<Boolean> active;

    private List<Double> popularity;

    private List<MealGroup> group;

    private List<Byte> byteValue;

    private List<Code> code;

    private List<NestedCriteria> nestedCriteria;

}
