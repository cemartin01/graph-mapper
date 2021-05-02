package io.github.cemartin01.graphmapper.mock.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SideDishDTO {

   private UUID id;

   private String name;

   private boolean active;

}