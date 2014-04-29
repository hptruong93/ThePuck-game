package Main.Units.Living.Boss.Ryskor;

import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Pointt;
import Utilities.FileUtilities;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MoonBlade extends AdvancedBossSkill {

   private static final double ANGULAR_SPEED = Math.toRadians(360);
   private static final double ROTATING_SPEED = Math.toRadians(-5);
   private static final int COOL_DOWN = 7000; //Milliseconds
   private static final double INITIAL_SPEED = 0.01;
   private static final double SPEED_INCREMENT = 0.00001;
   protected static final double RANGE = 350;
   private static final int UNIT_TIME = 7000; //MICROSECONDS
   private static final long CHASE_TIME = 3500; //Milliseconds
   private static final int REDUCED_ATTACK_SPEED_FACTOR = 5;
   private static final int INITIAL_DAMAGE = 30;
   private static final double INITIAL_DAMAGING_SPEED = 1;
   public static final double INITIAL_RADIUS = 180;
   private static final double INITIAL_LIFE_STEAL = 0.01; //1% of enemy max health
   private static ArrayList<Color> colors;
   private static Area rep;
   private double lifeSteal;
   private double rotatingAngle;
   private int colorIndex; //Showing what the current color is

   public static void initialize() {//Call only once
   }

   public MoonBlade(Game game, Ryskor owner, LivingUnit target) {
      super(game, target);
      createRep(INITIAL_RADIUS);
      colors = FileUtilities.getGradient("MoonBlade.txt");
      colorIndex = 0;
      lifeSteal = INITIAL_LIFE_STEAL;
      rotatingAngle = 0;
      this.setAngularSpeed(ANGULAR_SPEED);
      this.setSpeed(INITIAL_SPEED);
      this.setDamage(INITIAL_DAMAGE);
      this.setRadius(INITIAL_RADIUS);
      this.setPosition(owner.position().clone());
      this.setDamagingSpeed(INITIAL_DAMAGING_SPEED);
      this.setCoolDown(COOL_DOWN);
      this.setOwner(owner);
   }

   @Override
   public void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MICROSECONDS));
   }

   @Override
   public void moveNoCollision(double time) {
      if (!owner().dead() && this.activate()) {
         rotatingAngle += ROTATING_SPEED;
         boolean chasing = elapsedTime() < CHASE_TIME && target() != null && !target().dead();
         if (chasing) {//Chase the target
            updateMovement(target().position().clone());
            setSpeed(speed() + SPEED_INCREMENT);
         } else {//Return to owner
            updateMovement(owner().position().clone());
            setSpeed(speed() - SPEED_INCREMENT);
         }
         super.moveNoCollision(time);

         if (!chasing && (this.position().distance(owner().position()) <= owner().radius())) {//Turn off
            setSpeed(INITIAL_SPEED);
            setActivate(false, true);
         }
         Audio.attemptReplay(Audio.MOON_BLADE_LOOP);
      }
   }

   @Override
   protected void skillEffects(ArrayList<LivingUnit> enemies, Pointt focus) {
      Pointt display;
      Area skill, crep;

      skill = new Area(this.getRep());
      display = this.displayPosition(focus);
      AffineTransform af = AffineTransform.getTranslateInstance(display.getX(), display.getY());
      af.rotate(rotatingAngle);
      skill.transform(af);

      synchronized (enemies) {
         for (LivingUnit current : enemies) {
            if (current.dead()) {
               continue;
            }
            crep = new Area(current.getRep());

            display = current.displayPosition(focus);
            af = AffineTransform.getTranslateInstance(display.getX(), display.getY());
            crep.transform(af);

            crep.intersect(skill);

            applyEffect(current, !crep.isEmpty());
         }
      }
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {//Skill effect
      if (touched) {
         affectedUnit.setHealth(affectedUnit.health() - this.damage(), Units.PURE_DAMAGE);
         if (affectedUnit.damageReturn()) {
            owner().setHealth(affectedUnit.health() - this.damage(), Units.MAGICAL_DAMAGE);
         }

         if (affectedUnit.health() <= 0) {
            affectedUnit.die(damagingSpeed());
         }
         owner().setHealth(owner().health() + this.damage() * lifeSteal, HEAL);
      }
   }

   @Override
   public void setActivate(boolean activate, boolean forcedAdjust) {
      if (activate != activate()) {//No turn on/off when already on/off
         if (activate) {//Turn on
            if ((available() && !owner().dead()) || forcedAdjust) {
               if (owner().distance(target()) < RANGE) {
                  owner().setAttackSpeed(owner().attackSpeed() / REDUCED_ATTACK_SPEED_FACTOR);
                  owner().rescheduleAttack(0);
                  this.setPosition(owner().position());
                  this.schedule();
                  this.setStartTime(Clocks.masterClock.currentTime());
                  super.setActivate(activate, forcedAdjust);
                  Audio.playSound(Audio.MOON_BLADE_START);
                  Audio.playSound(Audio.MOON_BLADE_LOOP);
               }
            }
         } else {//Turn off
            clearTask();
            owner().setAttackSpeed(owner().attackSpeed() * REDUCED_ATTACK_SPEED_FACTOR);
            owner().rescheduleAttack(0);
            super.setActivate(activate, forcedAdjust);
         }
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (activate()) {
         Pointt display = this.displayPosition(focus);

         a.setTransform(transform);
         a.setPaint(this.getColor());
         a.translate(display.getX(), display.getY());
         a.rotate(rotatingAngle);
         a.fill(rep);

         for (int i = 0; i < 15; i++) {
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) (i / 15.0)));
            a.rotate(-Math.toRadians(-7));
            a.fill(rep);
         }

         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      }
   }

   @Override
   public Shape getRep() {
      return rep;
   }

   private Color getColor() {
      if (colorIndex >= 0) {
         if (colorIndex + 2 < colors.size()) {
            colorIndex += 2;
         } else {
            colorIndex -= 2;
            colorIndex = -colorIndex;
         }
      } else {
         colorIndex = -(Math.abs(colorIndex) - 2);
      }
      return colors.get(Math.abs(colorIndex));
   }

   private static void createRep(double radius) {
      double f = radius / 6;
      double a = 6 * f;
      double b = 4.5 * f;
      double y = 2 * f;
      double x = a * Math.sqrt(1 - (y / b) * (y / b));

      Ellipse2D e1 = new Ellipse2D.Double(-a - x, -b - y, 2 * a, 2 * b);

      double a1 = 3 * f;
      double b1 = 6 * f;
      double y1 = 2 * f;
      double x1 = a1 * Math.sqrt(1 - (y1 / b1) * (y1 / b1));

      Ellipse2D e2 = new Ellipse2D.Double(-a1 - x1, -b1 - y1, 2 * a1, 2 * b1);

      Area ar = new Area(e2);
      ar.subtract(new Area(e1));
      ar.subtract(new Area(new Rectangle2D.Double(-1000, -1000, 5000, 1000)));

      rep = ar;
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
   protected Ryskor owner() {
      return (Ryskor) super.owner();
   }

   @Override
   protected final void setOwner(LivingUnit owner) {
      super.setOwner((Ryskor) owner);
   }
}