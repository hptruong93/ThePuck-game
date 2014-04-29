package Main.Units.Living.Boss.DragonFly;

import Features.Audio;
import Main.Game;
import Main.MainScreen;
import Main.Units.Living.Puck.Puck;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Features.Clocks;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public final class SoulDive extends Dive {

   private double healthIncrement;
   private static final double ATTACK_SPEED_MINIMUM = 0.1;
   private static final double ATTACK_SPEED_DECREMENT = 0.01; //second
   private static final double REGEN_INCREMENT = 0.05;
   private static final double INCREMENT_FACTOR = 2; //The smaller this POSITIVE number is, the more health owner gains
   protected final static double INCREMENT = 3;
   private static final int FADE_TIME = 2;
   //RunningTime = (SKILL_BOUND/INCREMENT) * UNIT_TIME;

   public SoulDive(Game game, Eadirulatep owner, Puck target, long startTime) {
      super(game, owner);
      start = owner.position().clone();
      calculateEndPoint(target.position());
      healthIncrement = (owner.health() / 2) / ((SKILL_BOUND / 2.0) / INCREMENT);
      relativeTime = 0;

      start = start.getRotated(origin, -angle);
      end = end.getRotated(origin, -angle);
      setStartTime(startTime);

      owner().increaseMaxHealth(owner().maxHealth() / INCREMENT_FACTOR);
      owner().increaseRegen(REGEN_INCREMENT);
   }

   @Override
   public void calculateEndPoint(Pointt targetPosition) {
      double distanceTravel = 2 * owner().position().distance(targetPosition);
      angle = start.angle(targetPosition);
      double x = start.getX() + distanceTravel * Math.cos(angle);// + Math.cos(angle);
      double y = start.getY() + distanceTravel * Math.sin(angle);// + Math.sin(angle);
      end = new Pointt(x, y);

      angle = start.angle(end);
      origin = new Pointt(0.5 * (start.getX() + end.getX()), 0.5 * (start.getY() + end.getY()));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
      Audio.playSound(Audio.SOUL_DIVE);
   }

   @Override
   synchronized public void moveNoCollision(double time) {
      if (2 * relativeTime >= SKILL_BOUND) {//Half Period
         clearTask();
         owner().increaseSplitFire();
         owner().setAttackSpeed(Math.max(owner().attackSpeed() - ATTACK_SPEED_DECREMENT, ATTACK_SPEED_MINIMUM));

         synchronized (owner()) {
            owner().rescheduleAttack(0);
         }

         owner().removeDive();
      } else {
         relativeTime += INCREMENT;
         owner().setHealth(owner().health() + healthIncrement, Units.MAGICAL_DAMAGE);

         Pointt currentPosition = position(relativeTime);
         Pointt veryClose = position(relativeTime + DELTA);
         owner().setMovingAngle(currentPosition.angle(veryClose));
         owner().setPosition(currentPosition);

         for (int i = 1; i < 10; i++) {
            synchronized (game().visualEffects()) {
               game().visualEffects().add(new UniversalEffect(position(relativeTime - i * INCREMENT / 5),
                       DragonFly.repInstances[0], DragonFly.standardColors,
                       (i + 1) / FADE_TIME, 0, owner().movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
            }
         }


         if (owner().pathOfVenoms().activate()) {
            Venom toBeAdd = new Venom(game(), owner(), owner().position(), 0, 0, PathOfVenoms.VENOM_TYPE);
            synchronized (owner().projectiles()) {
               owner().projectiles().add(toBeAdd);
            }

            synchronized (owner().venoms()) {
               owner().venoms().add(toBeAdd);
            }

            toBeAdd.schedule();
         }


         int random = Maths.RANDOM.nextInt(Petaluridae.repInstances[0].parts().size());
         ArrayList<Area> part = new ArrayList<>();
         part.add(new Area(Petaluridae.repInstances[0].parts().get(random)));

         synchronized (game().visualEffects()) {
            game().visualEffects().add(new UniversalEffect(owner().position(),
                    new RepInstance(part), Petaluridae.standardColors.get(random),
                    UniversalEffect.DEFAULT_COUNT_DOWN, 0, Maths.randomAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   @Override
   protected long elapsedTime() {
      return (long) (2 * relativeTime);
   }

   @Override
   public boolean available() {
      return super.available() && (owner().soulLink().activate());
   }

   @Override
   protected Eadirulatep owner() {
      return (Eadirulatep) super.owner();
   }
}