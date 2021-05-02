package io.github.cemartin01.graphmapper.mock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class RecipeEntity extends CateringEntity {

   private String code;

   private String name;

   private String note;

   private Set<MealEntity> variants = new HashSet<>();

   private List<MealEntity> listedVariants = new ArrayList<>();

   //For Testing only
   public void setVariantsToNull() {
      variants = null;
   }

   //For Testing only
   public void setListedVariantsToNull() {
      listedVariants = null;
   }

   private boolean active;

}