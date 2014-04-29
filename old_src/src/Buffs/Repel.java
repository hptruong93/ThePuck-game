package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;

public class Repel extends Curse {

   public Repel(int duration, long startTime) {
      super(duration, Curse.INFINITE_STACK, startTime);
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      affectedUnit.setRepel(true);
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setRepel(false);
   }

   @Override
   public Repel clone() {
      return new Repel(duration, Clocks.masterClock.currentTime());
   }
}
