package Buffs;

import Main.Units.Living.LivingUnit;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public class Poison extends Curse {

   private double damage;

   public Poison(double damage, int stackAmount, int duration, long startTime) {
      super(duration, stackAmount, startTime);
      this.damage = damage;
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      if (!affectedUnit.invulnerable()) {
         affectedUnit.setHealth(Math.max(1, affectedUnit.health() - damage), Units.MAGICAL_DAMAGE);
      }

      if (applyCount < stackAmount) {
         if (!affectedUnit.invulnerable()) {
            affectedUnit.setHealth(Math.max(1, affectedUnit.health() - damage), Units.MAGICAL_DAMAGE);
         }
         applyCount++;
      }
      affectedUnit.setHealable(false);
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setHealable(true);
   }

   @Override
   public Poison clone() {
      return new Poison(damage, stackAmount, duration, Clocks.masterClock.currentTime());
   }
}
