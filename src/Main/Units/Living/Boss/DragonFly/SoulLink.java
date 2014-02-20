package Main.Units.Living.Boss.DragonFly;

import Buffs.Curse;
import Buffs.DistanceSlow;
import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SoulLink extends AdvancedBossSkill {

   private int state;
   private Pointt finalPosition;
   private double previousDistance;  //Instantaneous slow
   private static final int RANGE = 250;
   private static final int UNIT_TIME = 100; //Milliseconds
   private static final int COOL_DOWN = 7500;
   private static final double SLOW_FACTOR = 0.01; //Proportional to distance between owner and target
   private static final int NORMAL_STATE = -1;
   private static final int NORMAL_FADE_TIME = 10;
   private static final int CHASE_STATE = 5;
   private static final int CHASE_FADE_TIME = 3;
   private static final double BREAK_RANGE = 100;
   private static final double HEALTH_DRAIN_FACTOR = 0.0001;
   protected static final byte VENOM_BLAST_COUNT = 20;
   private static final int MAX_POSITION_CHASE = 40;

   public SoulLink(Game game, Eadirulatep owner) {
      super(game, game.puck());
      setRadius(RANGE);
      setCoolDown(COOL_DOWN);
      setStartTime(0);
      setOwner(owner);
      createEffectsContainer();
      effect().add(new DistanceSlow(SLOW_FACTOR, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME, owner));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedDelay(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   synchronized public void run() {
      skillEffects(null, game().focus());
   }

   @Override
   synchronized protected void skillEffects(ArrayList<LivingUnit> mustBeNull, Pointt focus) {
      if (mustBeNull != null) {
         throw new UnsupportedOperationException("Invalid call");
      } else {
         if (!target().dead()) {
            if (state == NORMAL_STATE) {
               double newDistance;
               newDistance = owner().distance(target());
               if (Math.abs(newDistance - previousDistance) < BREAK_RANGE) {
                  applyEffect(target(), true);
                  previousDistance = newDistance;
                  Audio.attemptReplay(Audio.SOUL_LINK);
               } else {
                  double xDifference = (target().position().getX() - owner().position().getX()) / MAX_POSITION_CHASE;
                  double yDifference = (target().position().getY() - owner().position().getY()) / MAX_POSITION_CHASE;
                  finalPosition = new Pointt(owner().position().getX() + MAX_POSITION_CHASE * xDifference,
                          owner().position().getY() + MAX_POSITION_CHASE * yDifference);
                  double angle = owner().position().angle(target().position());
                  Pointt current;
                  ArrayList<Area> part;

                  for (int i = 0; i < MAX_POSITION_CHASE; i++) {
                     synchronized (game().visualEffects()) {
                        current = new Pointt(owner().position().getX() + i * xDifference,
                                owner().position().getY() + i * yDifference);
                        game().visualEffects().add(new UniversalEffect(current,
                                DragonFly.repInstances[0], DragonFly.standardColors,
                                (i + 1) / CHASE_FADE_TIME, 0, angle, UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));

                        int random = Maths.RANDOM.nextInt(DragonFly.repInstances[0].parts().size());
                        part = new ArrayList<>();

                        part.add(new Area(DragonFly.repInstances[0].parts().get(random)));
                        game().visualEffects().add(new UniversalEffect(current,
                                new RepInstance(part), Petaluridae.standardColors.get(random),
                                UniversalEffect.DEFAULT_COUNT_DOWN, 0, Maths.randomAngle(),
                                UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
                     }
                  }
                  owner().setMoveable(false);
                  owner().setInvulnerable(true);
                  state = CHASE_STATE;
                  Audio.playSound(Audio.SOUL_LINK_APPROACH);
               }

               synchronized (game().visualEffects()) {
                  game().visualEffects().add(new UniversalEffect(owner().position().clone(),
                          DragonFly.repInstances[0], DragonFly.standardColors,
                          NORMAL_FADE_TIME, 0, owner().movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
               }

            } else {//CHASE_STATE
               if (state > 0) {
                  state--;
               } else {
                  owner().setMoveable(true);
                  owner().setPosition(finalPosition);

                  owner().venomFire.scheduleOnce(0);
                  owner().venomFire.scheduleOnce(500);

                  owner().setInvulnerable(false);
                  setActivate(false, true);
               }
            }
         } else {
            setActivate(false, true);
         }
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      Pointt display;
      if (state == NORMAL_STATE) {
         a.setTransform(transform);
         display = owner().displayPosition(focus);
         if (!target().dead() && !owner().dead()) {
            Pointt currentDisplay = target().displayPosition(focus);
            GeneralPath path = new GeneralPath();
            path.moveTo(display.getX(), display.getY());
            path.quadTo(0.5 * (currentDisplay.getX() + display.getX()) + 20 * Math.random(),
                    0.5 * (currentDisplay.getY() + display.getY()) + 20 * Math.random(),
                    currentDisplay.getX(), currentDisplay.getY());
            path.closePath();
            a.fill(path);
         }
      }
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      target().setHealth(target().health() - owner().maxHealth() * previousDistance * HEALTH_DRAIN_FACTOR, Units.MAGICAL_DAMAGE);
      owner().setHealth(owner().health() + owner().maxHealth() * previousDistance * HEALTH_DRAIN_FACTOR, Units.HEAL);
   }

   @Override
   synchronized public void setActivate(boolean activate, boolean forcedAdjust) {
      if (activate) {
         if ((available() || forcedAdjust) && !target().dead()) {
            state = NORMAL_STATE;
            previousDistance = owner().distance(target());
            setStartTime(Clocks.masterClock.currentTime());

            Curse tam = effect().remove(0);
            effect().add(tam.clone());
            target().curses().add(effect().get(0));

            schedule();
            super.setActivate(true, forcedAdjust);
            Audio.playSound(Audio.SOUL_LINK);
         }
      } else {
         if (target().curses().contains(effect().get(0))) {
            effect().get(0).removeFrom(target(), null);
         }
         clearTask();
         super.setActivate(false, forcedAdjust);
      }
   }

   @Override
   protected long elapsedTime() {
      if (activate()) {
         return Clocks.masterClock.currentTime() - startTime();
      } else {
         return 0;
      }
   }

   @Override
   public boolean available() {
      return super.available() && !activate();
   }

   @Override
   protected Eadirulatep owner() {
      return (Eadirulatep) super.owner();
   }

   private class StaticState {

      private Pointt position;
      private double movingAngle;

      private StaticState(Pointt position, double movingAngle) {
         this.position = position;
         this.movingAngle = movingAngle;
      }
   }
}