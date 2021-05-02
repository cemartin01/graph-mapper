package io.github.cemartin01.graphmapper.mapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public abstract class CateringEntity {

   private UUID id;

   private LocalDateTime created;

   private LocalDateTime modified;

   public CateringEntity(final UUID id) {
      this.id = id;
   }

}