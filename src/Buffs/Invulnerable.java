package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;

public class Invulnerable extends Curse {

   public Invulnerable(int duration, long startTime) {
      super(duration, Curse.INFINITE_STACK, startTime);
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      affectedUnit.setInvulnerable(true);
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setInvulnerable(false);
   }

   @Override
   public Invulnerable clone() {
      return new Invulnerable(duration, Clocks.masterClock.currentTime());
   }

}
