package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public class Stun extends Curse {

   boolean penetrateRepel;

   public Stun(int duration, boolean penetrateRepel, long startTime) {
      super(duration, Curse.INFINITE_STACK, startTime);
      this.penetrateRepel = penetrateRepel;
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      if (!affectedUnit.repel() || penetrateRepel) {
         affectedUnit.setMoveable(false);
         affectedUnit.setSkillEnable(false);
      }
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setSkillEnable(true);
            affectedUnit.setMoveable(true);
   }

   @Override
   public Stun clone() {
      return new Stun(duration, penetrateRepel, Clocks.masterClock.currentTime());
   }
}
