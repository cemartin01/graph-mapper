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
public class WeekMenuDTO {

   private UUID id;

   private Integer menuYear;

   private Integer menuWeek;

   private List<DayMenuDTO> days;

   private LocalDate firstDay;

   private LocalDate lastDay;

   private ProviderDTO provider;

   private CustomerDTO customer;

}