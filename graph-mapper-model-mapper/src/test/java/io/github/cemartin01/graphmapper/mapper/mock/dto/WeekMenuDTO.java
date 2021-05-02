package io.github.cemartin01.graphmapper.mapper.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeekMenuDTO {

   private UUID id;

   private Integer menuYear;

   private Integer menuWeek;

   private List<DayMenuDTO> days;

   private LocalDate firstDay;

   private LocalDate lastDay;

}