package io.github.cemartin01.graphmapper.mock.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class WeekMenuEntity extends CateringEntity {

   public WeekMenuEntity(final UUID id) {
      super(id);
   }

   private short menuYear;

   private byte menuWeek;

   @Setter(AccessLevel.NONE)
   private List<DayMenuEntity> days = new ArrayList<>();

   //For testing only
   public void setDaysToNull() {
      days = null;
   }

   private LocalDate firstDay;

   private LocalDate lastDay;

   private ProviderEntity provider;

   private CustomerEntity customer;

}