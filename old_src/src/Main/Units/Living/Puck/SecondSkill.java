package Main.Units.Living.Puck;

import Main.Game;
import Main.Units.Living.Illusion;
import Main.Units.Living.LivingUnit;
import Utilities.Pointt;
import Main.Units.NonLiving.Projectile;
import Features.Audio;
import Features.Clocks;
import Utilities.Geometry;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class SecondSkill extends PuckSkill {//Sweep an area around the puck

   public static final int DEFAULT_BONUS_TYPE = 2; //Damage
   private static final int WIDTH = 75;
   public static final double SPEED = 0.5; //Per millisecond 0.5
   private static final int UNIT_TIME = 30; //In millisecond
   private static final double SKILL_TIME = 9; //UNIT_TIME 9
   public static final int INITIAL_DAMAGE = 150;
   private static final int COOL_DOWN = 400; //Milliseconds
   public static final double COVER_ANGLE = Math.toRadians(50);
   private static final double LEVEL_UP_FACTOR = 0.95;
   private static final double DAMAGE_INCREMENT = 3;
   private static final byte DECREASE_SPEED_BONUS = 0;
   private static final byte INCREASE_SPEED_BONUS = 1;
   private static final byte INCREASE_DAMAGE_BONUS = 2;
   private static double initialDamage = INITIAL_DAMAGE;
   private static double initialSpeed = SPEED;
   private static Color[] colors;
   private double skillTime;
   private double unitDistance; //Distance moved per UNIT_TIME --> = speed * UNIT_TIME
   private Shape rep;
   private boolean activate;

   public SecondSkill(Game game) {
      super(game, COOL_DOWN);
      createRep();
      skillTime = SKILL_TIME;
      setSpeed(initialSpeed);
      unitDistance = speed() * UNIT_TIME;
      activate = false;
      setBonusType(INCREASE_DAMAGE_BONUS);
      setDamage(initialDamage);
      setDamagingSpeed(0.05);
      setStartTime(DEFAULT_START_TIME);
   }

   @Override
   public void schedule() {
      this.setPosition(this.game().puck().position());
      this.setMovingAngle(this.game().puck().movingAngle());
      this.setDestination(new Pointt(
              Geometry.fixX(this.position().getX()
              + skillTime * unitDistance * Math.cos(this.game().puck().movingAngle())),
              Geometry.fixY(this.position().getY()
              + skillTime * unitDistance * Math.sin(this.game().puck().movingAngle()))));
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   public void closeOperation() {
      this.clearTask();
      this.setActivate(false);
   }

   @Override
   public void moveNoCollision(double thisCanBeAnyDouble) {
      if (elapsedTime() >= skillTime * UNIT_TIME) {//Job done
         closeOperation();
         return;
      }

      this.position().setX(Geometry.fixX(this.position().getX() + unitDistance * Math.cos(this.movingAngle())));
      this.position().setY(Geometry.fixY(this.position().getY() + unitDistance * Math.sin(this.movingAngle())));

      this.game().puck().setPosition(this.position());
      this.game().puck().setDestination(this.position());
   }

   @Override
   protected double checkKill(HashSet<LivingUnit> enemies, Pointt focus) {
      if (!activate) {
         return 0;
      }
      synchronized (enemies) {
         super.checkKill(enemies, focus);
         // Have to check the enemies' projectiles as well
         Pointt display;
         Area skill, proj;
         skill = new Area(this.getRep().getBounds2D());
         display = this.displayPosition(focus);
         AffineTransform af = AffineTransform.getTranslateInstance(display.getX(), display.getY());
         af.rotate(this.movingAngle());
         skill.transform(af);


         for (LivingUnit current : enemies) {
            if (current.dead()) {
               continue;
            }

            synchronized (current.projectiles()) {
               for (Iterator<Projectile> it = current.projectiles().iterator(); it.hasNext();) {
                  Projectile projectile = it.next();
                  if (projectile.position().distance(this.position()) <= WIDTH / Geometry.DISPLAY_REAL_RATIO) {
                     proj = new Area(projectile.getRep());
                     display = projectile.displayPosition(focus);
                     af = AffineTransform.getTranslateInstance(display.getX(), display.getY());
                     proj.transform(af);
                     proj.intersect(skill);

                     if (!proj.isEmpty()) {//Touch the projectile --> Destroy it
                        projectile.removeFromAllContainer();
                     }
                  }
               }
            }
         }
      }
      return 0;
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (!activate) {
         return;
      }
      Pointt display;
      a.setTransform(transform);
      a.setPaint(Color.BLACK);

      a.setTransform(transform);
      display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.rotate(this.movingAngle());

      for (int i = 0; i < 10; i++) {
         a.setPaint(colors[i]);
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) (i * (1.0 / 10))));
         a.translate(-5, 0);
         a.fill(rep);
      }

      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
   }

   @Override
   protected void applyKillBonus(LivingUnit killedUnit) {
      if (killedUnit == null) {
         throw new RuntimeException("Dude, you're killing a null unit!");
      }
      if (killedUnit instanceof Illusion) {
         return;
      }

      owner().increaseNumberOfKill(1);
      if (game().puck().bonusType() == Puck.SKILL_BONUS) {
         if (bonusType() == INCREASE_DAMAGE_BONUS) {
            this.setDamage(this.damage() + DAMAGE_INCREMENT);
         } else if (bonusType() == DECREASE_SPEED_BONUS) {
            this.setSpeed(this.speed() * LEVEL_UP_FACTOR);
            skillTime /= LEVEL_UP_FACTOR;
            unitDistance = speed() * UNIT_TIME;
         } else if (bonusType() == INCREASE_SPEED_BONUS) {
            this.setSpeed(this.speed() / LEVEL_UP_FACTOR);
            skillTime *= LEVEL_UP_FACTOR;
            unitDistance = speed() * UNIT_TIME;
         } else {
            throw new RuntimeException("Invalid bonnus type " + bonusType());
         }
         game().puck().increaseMaxHealth(Puck.HEALTH_INCREMENT);
      } else if (game().puck().bonusType() == Puck.HEALTH_BONUS) {
         game().puck().increaseRegen(Puck.REGEN_INCREMENT);
      } else {
         throw new RuntimeException("Invalid bonnus type " + bonusType());
      }
   }

   @Override
   public void degrade() {
      this.setDamage(Math.min(this.damage() - 2 * DAMAGE_INCREMENT, INITIAL_DAMAGE));
   }

   private void createRep() {// Ratio WIDTH : HEIGHT = 15 : 14
      final int HEIGHT = WIDTH * 14 / 15;

      Area top = new Area(new Ellipse2D.Double(-WIDTH, -HEIGHT, 2 * WIDTH, 2 * HEIGHT));
      Area bot = new Area(new Ellipse2D.Double(-WIDTH, -HEIGHT - 10, 2 * WIDTH, 2 * HEIGHT));

      bot.subtract(top);
      rep = bot.createTransformedArea(AffineTransform.getRotateInstance(Math.PI / 2));

      colors = new Color[20];
      colors[0] = new Color(0xE50300);
      colors[1] = new Color(0xE61001);
      colors[2] = new Color(0xE71D02);
      colors[3] = new Color(0xE82A04);
      colors[4] = new Color(0xEA3805);
      colors[5] = new Color(0xEB4506);
      colors[6] = new Color(0xEC5208);
      colors[7] = new Color(0xEE5F09);
      colors[8] = new Color(0xEF6D0A);
      colors[9] = new Color(0xF07A0C);
      colors[10] = new Color(0xF2870D);
      colors[11] = new Color(0xF3940F);
      colors[12] = new Color(0xF4A210);
      colors[13] = new Color(0xF6AF11);
      colors[14] = new Color(0xF7BC13);
      colors[15] = new Color(0xF8C914);
      colors[16] = new Color(0xFAD715);
      colors[17] = new Color(0xFBE417);
      colors[18] = new Color(0xFCF118);
      colors[19] = new Color(0xFEFF1A);
   }

   @Override
   public Shape getRep() {
      return rep;
   }

   @Override
   public void setActivate(boolean activate) {
      if (!activate) {
         this.activate = false;
         this.game().puck().setMoveable(true);
      } else {
         if (this.available()) {
            owner().setUsedSecondSkill(true);
            this.setStartTime(Clocks.masterClock.currentTime());
            this.schedule();
            owner().thirdSkill().setActivate(false);
            owner().setMoveable(!activate);
            this.activate = true;
            Audio.playSound(Audio.SECOND_SKILL);
         }
      }
   }

   //Getter-Setter Auto Generated code
   public boolean activate() {
      return activate;
   }

   @Override
   public boolean available() {
      return super.available() && !activate;
   }

   public static Color colors(int i) {
      return colors[i];
   }

   @Override
   protected long elapsedTime() {
      if (!activate) {
         return 0;
      } else {
         return Clocks.masterClock.currentTime() - startTime();
      }
   }

   public static void setInitialDamage(double initialDamage) {
      SecondSkill.initialDamage = initialDamage;
   }

   public static void setInitialSpeed(double initialSpeed) {
      SecondSkill.initialSpeed = initialSpeed;
   }

   public static double initialDamage() {
      return initialDamage;
   }

   public static double initialSpeed() {
      return initialSpeed;
   }
}