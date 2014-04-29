package Main.Units.Living.Boss.Sarcophagidae;

import Buffs.Curse;
import Buffs.Stun;
import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.concurrent.TimeUnit;

public class BoundOfFreedom extends AdvancedBossSkill {

   private static final int UNIT_TIME = 100; //Milliseconds
   private static final double RADIUS = 100; //Real radius
   private static final double CAST_RANGE = 150;
   private static final int ELAPSE_TIME = 6000;
   private static final int DISAPPEAR_TIME = 2500;
   private static final int NUMBER_OF_VERTICES = 5;
   private static final int STUN_DURATION = 2000;
   private static final int COOL_DOWN = 15000;
   private static final int APPEAR_TIME_VISUAL = 2000;
   private static final double APPEAR_SPEED_VISUAL = 0.05;
   private static final float[] PAINT_FRACTION = {0.2f, 0.8f};
   private static final Color[] PAINT_COLOR = {Color.RED, Color.BLACK};
   private RadialGradientPaint paint;
   private Pointt targetPreviousPosition;
   private Pointt[] vertices;
   private Area rep;

   BoundOfFreedom(Game game, Sarcophagidae owner) {
      super(game, owner.target());
      setOwner(owner);
      setRadius(RADIUS);
      setCoolDown(COOL_DOWN);
      vertices = new Pointt[NUMBER_OF_VERTICES]; //Anti masterClock-wise arrangement
      createEffectsContainer();
      effect().add(new Stun(STUN_DURATION, true, Curse.DEFAULT_START_TIME));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      if (elapsedTime() > ELAPSE_TIME) {
         if (!target().invulnerable()) {
            synchronized (target().curses()) {
               target().curses().add(effect().get(0).clone());
            }
         }
         setActivate(false, false);
      } else {
         applyEffect(target(), contains(target()));
         Audio.attemptReplay(Audio.BOUND_OF_FREEDOM);
      }
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      if (touched) {
         targetPreviousPosition.concur(target().position());
      } else {//Bounced back
         Pointt currentDestination = target().destination();
         double currentSpeed = target().speed();
         double currentMovingAngle = target().movingAngle();
         double currentFinalAngle = target().finalAngle();

         target().setSpeed(target().position().distance(targetPreviousPosition) / UNIT_TIME);
         target().updateMovement(position().clone());
         target().setMovingAngle(target().finalAngle());
         target().moveNoCollision(UNIT_TIME);

         target().setSpeed(currentSpeed);
         target().setDestination(currentDestination);
         target().setMovingAngle(currentMovingAngle);
         target().setFinalAngle(currentFinalAngle);

      }
   }

   @Override
   public void setActivate(boolean activate, boolean forceAdjust) {
      if (activate) {
         if (!activate() && (available() || forceAdjust)) {
            if (owner().distance(target()) < CAST_RANGE) {
               super.setActivate(true, forceAdjust);
               setTarget(owner().target());
               setPosition(target().position().clone());
               targetPreviousPosition = target().position().clone();
               schedule();
               setStartTime(Clocks.masterClock.currentTime());
               Audio.playSound(Audio.BOUND_OF_FREEDOM);
            }
         }
      } else {
         clearTask();
         super.setActivate(false, forceAdjust);
      }
   }

   private boolean contains(LivingUnit considering) {
      return distance(considering) < radius();
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (activate()) {
         a.setTransform(transform);
         Pointt display = displayPosition(focus);
         a.translate(display.getX(), display.getY());

         if (ELAPSE_TIME - elapsedTime() < DISAPPEAR_TIME) {
            if (ELAPSE_TIME - elapsedTime() > 0) {
               a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (ELAPSE_TIME - elapsedTime()) / (float) DISAPPEAR_TIME));
            } else {
               return;
            }
         } else {
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
         }
         a.setPaint(paint);
         a.fill(getRep());
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

         if (Math.random() < 0.5) {
            synchronized (game().visualEffects()) {
               game().visualEffects().add(new UniversalEffect(position().randomRange(25),
                       Sarcophagidae.repInstances[0], Sarcophagidae.standardColors, APPEAR_TIME_VISUAL / 100,
                       APPEAR_SPEED_VISUAL, Maths.randomAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
            }
         }
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
   public void setPosition(Pointt position) {
      super.setPosition(position);
      generatePaint();
      double startAngle = Maths.randomAngle();
      GeneralPath tempRep = new GeneralPath();

      Pointt next = new Pointt(0, 0);
      for (int i = 0; i < vertices.length; i++) {
         vertices[i] = next.getArcPointt(radius() * Geometry.DISPLAY_REAL_RATIO, startAngle + i * 2 * (Math.PI / NUMBER_OF_VERTICES));
      }

      tempRep.moveTo(vertices[0].getX(), vertices[0].getY());

      for (int i = 0; i < vertices.length; i++) {
         next = vertices[(i + 1) % vertices.length];
         tempRep.quadTo(vertices[i].midPoint(next).getX() + Maths.randomNegative(radius() * Geometry.DISPLAY_REAL_RATIO / 2),
                 vertices[i].midPoint(next).getY() + Maths.randomNegative(radius() * Geometry.DISPLAY_REAL_RATIO / 2), next.getX(), next.getY());
      }

      tempRep.closePath();
      rep = new Area(tempRep);

      for (int i = 0; i < vertices.length; i++) {
         vertices[i].translate(position);
      }
   }

   private void generatePaint() {
      paint = new RadialGradientPaint(new Point(0, 0), (float) radius(), PAINT_FRACTION, PAINT_COLOR, MultipleGradientPaint.CycleMethod.REFLECT);
   }

   @Override
   protected Sarcophagidae owner() {
      return (Sarcophagidae) super.owner();
   }

   @Override
   public Area getRep() {
      return rep;
   }
}
