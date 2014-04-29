package Main.Units.Living.Boss.DragonFly;

import Features.Audio;
import Features.Clocks;
import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.concurrent.TimeUnit;

public final class IllusoryDive extends Dive {

   protected static final double RANGE = 250;
   protected static final double INCREMENT = 3;
   private static final int FADE_TIME = 10; //Increase this to decrease fade masterClock
   private static final double DISTANCE_TRAVELLED = (Game.map.REAL_SIZEX() / 3) / Geometry.DISPLAY_REAL_RATIO;
   //RunningTime = (SKILL_BOUND/INCREMENT) * UNIT_TIME;

   public IllusoryDive(Petaluridae owner, LivingUnit target) {
      super(owner.game(), owner);//Does not need to reference target at other points
      //This skill does not need to refer to game
      start = owner.position().clone();
      owner.setRepel(true);
      calculateEndPoint(target.position());
      relativeTime = 0;

      start = start.getRotated(origin, -angle);
      end = end.getRotated(origin, -angle);
   }

   @Override
   public void calculateEndPoint(Pointt targetPosition) {
      angle = start.angle(targetPosition);
      double x = start.getX() + DISTANCE_TRAVELLED * Math.cos(angle);// + Math.cos(angle);
      double y = start.getY() + DISTANCE_TRAVELLED * Math.sin(angle);// + Math.sin(angle);
      end = new Pointt(x, y);

      angle = start.angle(end);
      origin = new Pointt(0.5 * (start.getX() + end.getX()), 0.5 * (start.getY() + end.getY()));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
      Audio.playSound(Audio.ILLUSORY_DIVE);
   }

   @Override
   synchronized public void moveNoCollision(double time) {
      if (2 * relativeTime >= SKILL_BOUND) {//Half Period
         clearTask();
         owner().setRepel(false);
         owner().removeDive();
      } else {
         relativeTime += INCREMENT;

         Pointt currentPosition = position(relativeTime);
         Pointt veryClose = position(relativeTime + DELTA);
         owner().setMovingAngle(currentPosition.angle(veryClose));
         owner().setPosition(currentPosition);

         for (int i = 1; i < 10; i++) {
            synchronized (game().visualEffects()) {
               game().visualEffects().add(new UniversalEffect(position(relativeTime - i * INCREMENT / 5),
                       DragonFly.repInstances[0], DragonFly.standardColors,
                       (2 * (10 - i) + 1) / FADE_TIME, 0, owner().movingAngle(), UniversalEffect.STAND_STILL,
                       UniversalEffect.FADING, UniversalEffect.SHRINKING_SCALE));
            }
         }
      }
   }

   @Override
   protected Petaluridae owner() {
      return (Petaluridae) super.owner();
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
