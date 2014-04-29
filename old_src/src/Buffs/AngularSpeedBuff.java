package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;

public class AngularSpeedBuff extends Curse {

   private double increment;

   public AngularSpeedBuff(double increment, int duration, long startTime) {
      super(duration, Curse.NO_STACK, startTime);
      this.increment = increment;
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      if (applyCount < stackAmount) {
         affectedUnit.setAngularSpeed(affectedUnit.angularSpeed() + increment);
         applyCount++;
      }
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setAngularSpeed(affectedUnit.angularSpeed() - increment * applyCount);
   }

   @Override
   public AngularSpeedBuff clone() {
      return new AngularSpeedBuff(increment, duration, Clocks.masterClock.currentTime());
   }
}
