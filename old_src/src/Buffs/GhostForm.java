package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;

public class GhostForm extends Curse {

   public GhostForm(int duration, long startTime) {
      super(duration, Curse.INFINITE_STACK, startTime);
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      affectedUnit.setInGhostForm(true);
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setInGhostForm(false);
   }

   @Override
   public GhostForm clone() {
      return new GhostForm(duration, Clocks.masterClock.currentTime());
   }

}
