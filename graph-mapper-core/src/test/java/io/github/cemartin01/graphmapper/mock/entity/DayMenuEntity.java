package io.github.cemartin01.graphmapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DayMenuEntity extends CateringEntity {

   private LocalDate dayDate;

   private byte dayOfWeek;

   private WeekMenuEntity weekMenu;

   private List<DayMenuItemEntity> items = new ArrayList<>();

}