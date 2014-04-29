package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;

public class DistanceSlow extends DynamicSlow {

   private final double factor;

   public DistanceSlow(double factor, int duration, long startTime, LivingUnit source) {
      super(duration, startTime, source);
      this.factor = factor;
   }

   @Override
   protected double slowFunction(LivingUnit affectedUnit) {
      return Math.max(1, factor * affectedUnit.distance(source()));
   }

   @Override
   public DistanceSlow clone() {
      return new DistanceSlow(factor, duration, Clocks.masterClock.currentTime(), source());
   }
}
