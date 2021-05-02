package io.github.cemartin01.graphmapper.mock.dto;

import java.time.LocalDate;
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
public class DayMenuDTO {

   private UUID id;

   private LocalDate dayDate;

   private byte dayOfWeek;

   private List<DayMenuItemDTO> items;

}