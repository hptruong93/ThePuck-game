package Buffs;

import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import java.util.Iterator;

public abstract class Curse {

   public static final int INFINITE_DURATION = Integer.MAX_VALUE;
   public static final int INFINITE_STACK = Integer.MAX_VALUE;
   public static final int NO_STACK = 1;
   public static final long DEFAULT_START_TIME = Long.MAX_VALUE;
   protected static final int STARTING_CUMULATIVE_EFFECT = 1;
   protected int duration;
   protected long startTime;
   protected int stackAmount;
   protected int applyCount;

   public Curse(int duration, int stackAmount, long startTime) {
      this.duration = duration;
      this.startTime = startTime;
      this.stackAmount = stackAmount;
      applyCount = 0;
   }

   public boolean removable(LivingUnit affectedUnit, Iterator it) {
      return Clocks.masterClock.currentTime() - startTime > duration;
   }

   /**
    *
    * @param affectedUnit: an ALIVE LivingUnit
    */
   public final void applyEffect(LivingUnit affectedUnit) {
      if (affectedUnit.curses().contains(this)) {
         putEffectOn(affectedUnit);
      } else {
         throw new RuntimeException("Invalid call. Must be added to enemy curses first");
      }
   }

   public final void removeFrom(LivingUnit affectedUnit, Iterator it) {//Invert any effect applied
      synchronized (affectedUnit.curses()) {
         if (affectedUnit.curses().contains(this)) {
            removeEffectFrom(affectedUnit);

            if (it == null) {
               affectedUnit.curses().remove(this);
            } else {
               it.remove();
            }
         } else {
            throw new RuntimeException("Invalid call. Must be added to enemy curses first");
         }
      }
   }

   protected abstract void putEffectOn(LivingUnit affectedUnit);

   protected abstract void removeEffectFrom(LivingUnit affectedUnit);

   @Override
   public abstract Curse clone();

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }
}