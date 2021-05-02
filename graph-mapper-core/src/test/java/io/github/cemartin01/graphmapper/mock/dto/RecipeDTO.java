package io.github.cemartin01.graphmapper.mock.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {

   private UUID id;

   private String code;

   private String name;

   private String note;

   private List<Meal> variants;

   private boolean active;

}