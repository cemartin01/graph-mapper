package io.github.cemartin01.graphmapper.mapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class DayMenuEntity extends CateringEntity {

   private LocalDate dayDate;

   private byte dayOfWeek;

   private WeekMenuEntity weekMenu;

   private List<DayMenuItemEntity> items = new ArrayList<>();

   private Set<DayMenuItemEntity> itemSet = new HashSet<>();

}