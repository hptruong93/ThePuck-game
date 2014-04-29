package Buffs;

import Main.Units.Living.LivingUnit;
import java.util.Iterator;

public abstract class DynamicSlow extends Slow {

   private LivingUnit source;

   public DynamicSlow(int duration, long startTime, LivingUnit source) {
      super(1, Curse.NO_STACK, duration, startTime);
      setSource(source);
   }

   @Override
   protected final void putEffectOn(LivingUnit affectedUnit) {
      affectedUnit.setSpeed(affectedUnit.speed() * slowFactor);
      slowFactor = slowFunction(affectedUnit);
      if (slowFactor == FREEZE) {
         affectedUnit.setMoveable(false);
      }
      affectedUnit.setSpeed(affectedUnit.speed() / slowFactor);
   }

   @Override
   protected final void removeEffectFrom(LivingUnit affectedUnit) {
      if (slowFactor == FREEZE) {
         affectedUnit.setMoveable(true);
      }
      affectedUnit.setSpeed(affectedUnit.speed() * slowFactor);
   }

   protected abstract double slowFunction(LivingUnit affectedUnit);

   @Override
   public abstract DynamicSlow clone();

   private void setSource(LivingUnit source) {
      this.source = source;
   }

   protected LivingUnit source() {
      return source;
   }
}