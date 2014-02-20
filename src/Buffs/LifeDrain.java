package Buffs;

import Main.Units.Living.LivingUnit;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public class LifeDrain extends Curse {

   private double drainAmount;

   public LifeDrain(double drainAmount, int duration, long startTime) {
      super(duration, Curse.INFINITE_STACK, startTime);
      this.drainAmount = drainAmount;
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      if (!affectedUnit.invulnerable()) {
         affectedUnit.setHealth(affectedUnit.health() - drainAmount, Units.MAGICAL_DAMAGE);
      }
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      //Intentionally left blank
   }

   @Override
   public LifeDrain clone() {
      return new LifeDrain(drainAmount, duration, Clocks.masterClock.currentTime());
   }
}
