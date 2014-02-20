package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public class Slow extends Curse {

   protected double slowFactor;
   public static final int FREEZE = 999;

   public Slow(double slowFactor, int stackAmount, int duration, long startTime) {
      super(duration, stackAmount, startTime);
      this.slowFactor = slowFactor;
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      if (slowFactor == FREEZE) {
         affectedUnit.setMoveable(false);
         return;
      }

      if (applyCount < stackAmount) {
         affectedUnit.setSpeed(affectedUnit.speed() / slowFactor);
         applyCount++;
      }
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      if (slowFactor == FREEZE) {
         affectedUnit.setMoveable(true);
      }
      affectedUnit.setSpeed(affectedUnit.speed() * Math.pow(slowFactor, applyCount));
   }

   @Override
   public Slow clone() {
      return new Slow(slowFactor, stackAmount, duration, Clocks.masterClock.currentTime());
   }
}