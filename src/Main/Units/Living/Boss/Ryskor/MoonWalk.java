package Main.Units.Living.Boss.Ryskor;

import Buffs.Curse;
import Buffs.Slow;
import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MoonWalk extends AdvancedBossSkill {

   private static final double ANGULAR_SPEED = Math.toRadians(360);
   private static final int COOL_DOWN = 5000; //Milliseconds
   private static final double INITIAL_SPEED = 0.25;
   private static final double SLOW_FACTOR = 2;
   private static final int SLOW_DURATION = 2000; //Milliseconds
   private static final double MINIMUM_RANGE = 50;
   private static final double RANGE = 400;
   private static final int UNIT_TIME = 100; //Milliseconds
   private static final int INITIAL_DAMAGE = 0;
   public static final double INITIAL_RADIUS = 180;
   private ArrayList<Pointt> previousPositions;

   public static void initialize() {
      new MoonWalk();
   }

   private MoonWalk() {
   }

   public MoonWalk(Game game, Ryskor owner, LivingUnit target) {
      super(game, target);
      setAngularSpeed(ANGULAR_SPEED);
      setSpeed(INITIAL_SPEED);
      setDamage(INITIAL_DAMAGE);
      setRadius(INITIAL_RADIUS);
      setCoolDown(COOL_DOWN);
      setOwner(owner);
      previousPositions = new ArrayList<>();
      createEffectsContainer();
      effect().add(new Slow(SLOW_FACTOR, Curse.NO_STACK, SLOW_DURATION, Curse.DEFAULT_START_TIME));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      if (!owner().dead()) {
         ArrayList enemies = new ArrayList<>();
         enemies.add(game().puck());
         moveNoCollision(UNIT_TIME);
         skillEffects(enemies, game().focus());
      } else {
         removeFromAllContainer();
      }
   }

   @Override
   public void moveNoCollision(double time) {
      updateMovement(calculateDestination(target()));
      super.moveNoCollision(time);
      owner().setMovingAngle(movingAngle() + Math.PI);
      previousPositions.add(position().clone());
      if (position().samePlace(destination())) {
         owner().setPosition(this.position());
         setActivate(false, true);
      } else {
         Audio.attemptReplay(Audio.MOON_WALK);
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (activate() && !owner().dead()) {
         Pointt display;
         try {
            synchronized (previousPositions) {
               for (int i = 0; i < previousPositions.size(); i++) {
                  a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) i / previousPositions.size()));
                  a.setTransform(transform);
                  display = previousPositions.get(i).realToDisplay(focus);
                  a.translate(display.getX(), display.getY());
                  Ryskor.repInstances[0].plot(a, owner().partColors());
               }
            }
         } catch (IndexOutOfBoundsException e) {
         }
      }
   }

   @Override
   public Shape getRep() {
      return owner().getRep();
   }

   @Override
   protected long elapsedTime() {
      throw new UnsupportedOperationException("Not needed.");
   }

   @Override
   public void setActivate(boolean activate, boolean forcedAdjust) {
      if (activate) {//Turn on
         if (available() && !target().dead()) {
            previousPositions.add(owner().position().clone());
            owner().setRepel(true);
            setPosition(owner().position());
            owner().setMovingAngle(movingAngle() + Math.PI);
            updateMovement(calculateDestination(target()));
            setMovingAngle(finalAngle());
            schedule();
            setStartTime(Clocks.masterClock.currentTime());
            super.setActivate(activate, forcedAdjust);
            try {
            Audio.playSound(Audio.MOON_WALK);} catch (Exception e) {
               e.printStackTrace();
               System.exit(0);
            }
         }
      } else {//Turn off
         clearTask();
         previousPositions.clear();
         owner().setRepel(false);
         owner().updateMovement(target().position());
         owner().setMovingAngle(owner().finalAngle());
         super.setActivate(activate, forcedAdjust);
      }

   }

   @Override
   public boolean available() {
      return super.available() && inRange();
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      if (touched) {
         synchronized (affectedUnit.curses()) {
            affectedUnit.curses().add(effect().get(0).clone());
         }
      }
   }

   @Override
   public void removeFromAllContainer() {
      clearTask();
   }

   private static Pointt calculateDestination(LivingUnit target) {//Go behind target
      final double range = target.radius() * 3;
      double angle = target.movingAngle() + Math.PI;
      return new Pointt(target.position().getX() + range * Math.cos(angle), target.position().getY() + range * Math.sin(angle));
   }

   protected boolean inRange() {
      double distance = owner().distance(target());
      return distance < RANGE && distance > MINIMUM_RANGE;
   }

   @Override
   protected Ryskor owner() {
      return (Ryskor) super.owner();
   }
}