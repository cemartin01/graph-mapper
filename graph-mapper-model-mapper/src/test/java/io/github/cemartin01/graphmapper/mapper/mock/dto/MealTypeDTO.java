package io.github.cemartin01.graphmapper.mapper.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealTypeDTO {

   private UUID id;

   private String name;

   private boolean active;

}