package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public class Silent extends Curse {

   public Silent(int duration, long startTime) {
      super(duration, Curse.INFINITE_STACK, startTime);
   }

   @Override
   protected void putEffectOn(LivingUnit affectedUnit) {
      affectedUnit.setSkillEnable(false);
   }

   @Override
   protected void removeEffectFrom(LivingUnit affectedUnit) {
      affectedUnit.setSkillEnable(true);
   }

   @Override
   public Silent clone() {
      return new Silent(duration, Clocks.masterClock.currentTime());
   }
}
