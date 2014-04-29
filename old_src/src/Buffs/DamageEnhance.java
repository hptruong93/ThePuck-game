package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public class DamageEnhance extends Curse {

   private double factor;

   public DamageEnhance(double factor, int duration, long startTime) {
      super(duration, Curse.NO_STACK, startTime);
      this.factor = factor;
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      if (applyCount < stackAmount) {
            affectedUnit.setDamage(affectedUnit.damage() * factor);
            applyCount++;
         }
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setDamage(affectedUnit.damage() / Math.pow(factor, stackAmount));
   }

   @Override
   public Curse clone() {
      return new DamageEnhance(factor, duration, Clocks.masterClock.currentTime());
   }
}
