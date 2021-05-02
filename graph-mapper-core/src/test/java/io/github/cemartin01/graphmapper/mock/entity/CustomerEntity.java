package io.github.cemartin01.graphmapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class CustomerEntity extends CateringEntity {

   private String name;

   private boolean active;

   public CustomerEntity(final UUID id) {
      super(id);
   }

}