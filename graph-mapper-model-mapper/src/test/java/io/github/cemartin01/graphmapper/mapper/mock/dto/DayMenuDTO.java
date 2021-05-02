package io.github.cemartin01.graphmapper.mapper.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayMenuDTO {

   private UUID id;

   private LocalDate dayDate;

   private byte dayOfWeek;

   private List<DayMenuItemDTO> items;

   private Set<DayMenuItemDTO> itemSet;

}