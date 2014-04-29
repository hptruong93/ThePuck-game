package Main.Units.Living.Puck;

import Features.Audio;
import Features.Clocks;
import Main.Game;
import Main.Units.Living.Illusion;
import Main.Units.Living.LivingUnit;
import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.FileUtilities;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;

public class Ultimate extends PuckSkill {

   private final HashSet<LivingUnit> underEffect;
   private boolean activate;
   private double lifeSteal;
   private static RepInstance rep;
   private static ArrayList<Color> standardColors;
   public static final int DEFAULT_BONUS_TYPE = 0; //Radius
   private static final int SKILL_TIME = 700; //Milliseconds
   protected static final int BONUS_PERIOD = 2 * SKILL_TIME;
   public static final double DEFAULT_SPEED = 0.09;
   public static final double DEFAULT_LIFE_STEAL = 0.01;
   private static final double FIXED_LIFE_STEAL_GAIN = 0.01;
   private static final double DEFAULT_ANGULAR_SPEED = Math.toRadians(-10);
   public static final double DEFAULT_RADIUS = 75;
   private static final double DEFAULT_RANGE = 200;
   private static final int COOL_DOWN = 4000; //Milliseconds
   private static final int RADIUS_BONUS_INDEX = 0;
   private static final int SPEED_BONUS_INDEX = 1;
   private static final int LIFE_STEAL_BONUS_INDEX = 2;
   private static final double INCREMENT_SPEED = 0.00005;
   private static final double INCREMENT_RADIUS = 0.1;
   private static final double INCREMENT_LIFE_STEAL_BONUS = 0.000001;
   private static double initialRadius = DEFAULT_RADIUS;
   private static double initialSpeed = DEFAULT_SPEED;
   private static double initialLifeSteal = DEFAULT_LIFE_STEAL;

   public Ultimate(Game game) {
      super(game, COOL_DOWN);
      this.setRadius(initialRadius);
      generateRep(radius());

      activate = false;
      underEffect = new HashSet<>();
      this.setSpeed(initialSpeed);
      lifeSteal = initialLifeSteal;
      this.setAngularSpeed(DEFAULT_ANGULAR_SPEED);
      this.setStartTime(0);
   }

   @Override
   public void schedule() {
      throw new RuntimeException("Don't call this. Process with Puck sequentially.");
   }

   @Override
   public void moveNoCollision(double time) {
      if (activate) {
         if (elapsedTime() > SKILL_TIME) {
            setActivate(false, null);
            return;
         }
         currentRepIndex = (currentRepIndex + 1) % standardColors.size();
         setMovingAngle(movingAngle() + angularSpeed());
         checkKill(null, null);
      }
   }

   @Override
   protected double checkKill(HashSet<LivingUnit> enemies, Pointt focus) {
      if ((enemies != null) || (focus != null)) {
         throw new RuntimeException("Enemies and focus should both be null: enemies is " + enemies.toString() + " . Focus is: " + focus);
      }
      synchronized (underEffect) {
         for (LivingUnit current : underEffect) {
            if (!current.dead()) {
               if (!current.invulnerable() && !current.repel()) {
                  Pointt currentDestination = current.destination();
                  double currentSpeed = current.speed();
                  double currentMovingAngle = current.movingAngle();
                  double currentFinalAngle = current.finalAngle();

                  current.setSpeed(this.speed());
                  current.updateMovement(position().clone());
                  current.setMovingAngle(current.finalAngle());
                  current.moveNoCollision(2 * Game.map.PROCESSING_RATE());

                  current.setSpeed(currentSpeed);
                  current.setDestination(currentDestination);
                  current.setMovingAngle(currentMovingAngle);
                  current.setFinalAngle(currentFinalAngle);

                  current.setHealth(current.health() - current.maxHealth() * lifeSteal, Units.MAGICAL_DAMAGE);

                  if (current.damageReturn()) {
                     owner().setHealth(owner().health() - current.maxHealth() * lifeSteal, Units.MAGICAL_DAMAGE);
                     if (current.health() <= 0) {
                        this.applyKillBonus(current);
                     }
                  } else {
                     owner().setHealth(owner().health() + current.maxHealth() * lifeSteal * FIXED_LIFE_STEAL_GAIN, Units.MAGICAL_DAMAGE);
                  }
               }
            }
         }
      }
      return 0;
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      a.setTransform(transform);
      if (activate) {
         Pointt display = this.displayPosition(focus);
         a.translate(display.getX(), display.getY());
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
         a.rotate(this.movingAngle());
         rep.plot(a, standardColors.get(currentRepIndex));
      } else if (owner().holdUltimate()) {
         a.setTransform(transform);
         Pointt display = new Pointt(MouseInfo.getPointerInfo().getLocation());
         a.translate(display.getX(), display.getY() - Game.map.MENU_BAR_WIDTH());
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
         rep.plot(a, Color.BLUE);
      }
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
   }

   @Override
   protected void applyKillBonus(LivingUnit killedUnit) {
      if (killedUnit == null) throw new RuntimeException("Dude, you're killing a null unit!");
      if (killedUnit instanceof Illusion) return;

      owner().increaseNumberOfKill(1);
      if (owner().bonusType() == Puck.HEALTH_BONUS) {
         owner().increaseRegen(Puck.REGEN_INCREMENT);
      } else if (owner().bonusType() == Puck.SKILL_BONUS) {
         if (bonusType() == SPEED_BONUS_INDEX) {
            this.setSpeed(this.speed() + INCREMENT_SPEED);
         } else if (bonusType() == RADIUS_BONUS_INDEX) {
            this.setRadius(this.radius() + INCREMENT_RADIUS);
         } else if (bonusType() == LIFE_STEAL_BONUS_INDEX) {
            owner().increaseRegen(Puck.REGEN_INCREMENT);
            lifeSteal += INCREMENT_LIFE_STEAL_BONUS;
         } else {
            throw new RuntimeException("Invalid bonnus type " + bonusType());
         }
      } else {
         throw new RuntimeException("Invalid bonnus type " + owner().bonusType());
      }
   }

   @Override
   public void degrade() {
      this.setRadius(this.radius() - INCREMENT_RADIUS);
   }

   @Override
   protected long elapsedTime() {
      if (activate) {
         return Clocks.masterClock.currentTime() - startTime();
      } else {
         return 0;
      }
   }

   public void setActivate(boolean activate, Pointt castPosition) {
      if (activate) {
         if (this.available()) {
            if (owner().position().distance(castPosition) <= DEFAULT_RANGE) {
               owner().setUsedUltimate(true);
               this.activate = true;
               this.setPosition(castPosition.clone());

               synchronized (game().enemies()) {
                  for (LivingUnit current : game().enemies()) {
                     if (!current.repel()) {
                        if (current.distance(this) <= this.radius()) {
                           underEffect.add(current);
                        }
                     }
                  }
               }
               setStartTime(Clocks.masterClock.currentTime());
               Audio.playSound(Audio.ULTIMATE);
            }
         }
      } else {
         this.activate = false;
         underEffect.clear();
      }
   }

   @Override
   public void setActivate(boolean activate) {
      this.activate = activate;
   }

   private void generateRep(double initialRadius) {
      double radius = initialRadius * Geometry.DISPLAY_REAL_RATIO;

      ArrayList<Area> parts = new ArrayList<>();

      //Subtract 2 circles
      Area circle = new Area(Geometry.createEllipse(0, -radius / 2, radius / 2, radius / 2));
      double center2 = radius / 4;
      double radius2 = Math.sqrt(center2 * center2 + radius * radius / 4);
      Area circle2 = new Area(Geometry.createEllipse(center2, -radius / 2, radius2, radius2));

      circle.subtract(circle2);
      parts.add(new Area(circle));

      //Rotate for complete image
      int rotation = 5;
      for (int i = 0; i < 2 * rotation - 1; i++) {
         circle.transform(AffineTransform.getRotateInstance(Math.PI / rotation));
         parts.add(new Area(circle));
      }

      rep = new RepInstance(parts);
      standardColors = FileUtilities.getGradient("PuckUltimateGradient.txt");
   }

   public boolean activate() {
      return activate;
   }

   @Override
   public final void setRadius(double radius) {
      if (rep != null) {
         double scale = radius / this.radius();
         synchronized (rep) {
            for (Area current : rep.parts()) {
               current.transform(AffineTransform.getScaleInstance(scale, scale));
            }
         }

      }
      super.setRadius(radius);
   }

   public static void setInitialRadius(double initialRadius) {
      Ultimate.initialRadius = initialRadius;
   }

   public static void setInitialSpeed(double initialSpeed) {
      Ultimate.initialSpeed = initialSpeed;
   }

   public static void setInitialLifeSteal(double initialLifeSteal) {
      Ultimate.initialLifeSteal = initialLifeSteal;
   }

   public double lifeSteal() {
      return lifeSteal;
   }
}
