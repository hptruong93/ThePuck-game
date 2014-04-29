package Main.Units.Living.Puck;

import Main.Game;
import Main.Units.Living.Illusion;
import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FirstSkill extends PuckSkill {

   public static final int DEFAULT_BONUS_TYPE = 0;
   private static final double WIDTH = 40;
   private static final double HEIGHT = 40;
   private static final Color SKILL_COLOR = new Color(0x7C, 0xFC, 0x00);
   public static final Color SKILL_BUTTON_COLOR = new Color(0x7C, 0xFC, 0x00);
   public static final double[] FACTORS = {1, 1.4, 1.7, 2, 2.3, 2.6, 2.9, 3.2, 3.5, 3.8, 4.1, 4.4, 4.7, 5, 5.3, 5.6, 5.9, 6.2, 6.5, 6.8, 7.1};
   public static final double INITIAL_DAMAGE = 600;
   private static final double DAMAGING_SPEED = 0.01;
   private static final double DAMAGE_INCREMENT = 1;
   private static final byte DAMAGE_BONUS_INDEX = 0;
   private static final int UNIT_TIME = 50; //In milliseconds
   private static final int COOL_DOWN = 2000; //Millisecond
   public static final double COVER_ANGLE = Math.toRadians(90);
   private static double initialDamage = INITIAL_DAMAGE;
   private int factor;
   private ArrayList<Area> rep;
   private AffineTransform shift;

   public FirstSkill(Game gameMap, double movingAngle) {
      super(gameMap, COOL_DOWN);

      this.setDamage(initialDamage);
      this.setBonusType(DAMAGE_BONUS_INDEX);
      this.setDamagingSpeed(DAMAGING_SPEED);
      this.setPosition(gameMap.puck().position().clone());
      this.setMovingAngle(movingAngle - Math.PI / 2);
      this.setStartTime(Clocks.masterClock.currentTime());

      factor = -1;
      shift = new AffineTransform();
      rep = new ArrayList<>();
   }

   public FirstSkill() {//Creating fake firstSkill
      this.setCoolDown(COOL_DOWN);
      this.setStartTime(0);
      factor = -1;
   }

   @Override
   public void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void moveNoCollision(double thisCanBeAnyDouble) {//Propagate
      factor = factor + 1;
      if (factor < FACTORS.length) {
         Area top = new Area(new Ellipse2D.Double(-WIDTH * FACTORS[factor], HEIGHT, 2 * WIDTH * FACTORS[factor], 2 * HEIGHT));
         Area bot = new Area(new Ellipse2D.Double(-WIDTH * FACTORS[factor], HEIGHT - 10 * FACTORS[factor], 2 * WIDTH * FACTORS[factor], 2 * HEIGHT));

         top.subtract(bot);
         shift.translate(0, 5 + 4 * FACTORS[factor]);
         top.transform(shift);
         rep.add(top);
         if (factor == FACTORS.length - 1) {
            shift = new AffineTransform();
         }
      } else {
         if (factor > FACTORS.length * 2) {
            clearTask();
         }
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (this.fakeSkill()) {
         return;
      }
      Pointt display = this.displayPosition(focus);
      a.setTransform(transform);
      a.setPaint(SKILL_COLOR);
      a.translate(display.getX(), display.getY());
      a.rotate(this.movingAngle());
      for (int i = Math.max(factor - (FACTORS.length - 1), 0); i < rep.size(); i++) {
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min((float) (0.05 * i), 1)));
         a.fill(rep.get(i));
      }
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
   }

   @Override
   protected void applyKillBonus(LivingUnit killedUnit) {
      if (killedUnit == null) throw new RuntimeException("Dude, you're killing a null unit!");
      if (killedUnit instanceof Illusion) return;

      if (Clocks.masterClock.currentTime() - owner().ultimate().startTime() < Ultimate.BONUS_PERIOD) {
         owner().ultimate().applyKillBonus(killedUnit);
         owner().increaseNumberOfKill(-1); //So that ultimate does not double count this kill
      }

      owner().increaseNumberOfKill(1);

      if (owner().bonusType() == Puck.SKILL_BONUS) {
         if (bonusType() == DAMAGE_BONUS_INDEX) {
            initialDamage += DAMAGE_INCREMENT;
            this.setDamage(this.damage() - 5 * DAMAGE_INCREMENT);
         } else {
            throw new RuntimeException("Invalid bonnus type " + bonusType());
         }
      } else if (owner().bonusType() == Puck.HEALTH_BONUS) {
         owner().increaseMaxHealth(Puck.HEALTH_INCREMENT);
      } else {
         throw new RuntimeException("Invalid bonnus type " + bonusType());
      }
   }

   @Override
   public void degrade() {
      initialDamage = Math.max(initialDamage - 2 * DAMAGE_INCREMENT, 0);
      this.setDamage(Math.min(damage() - 2 * DAMAGE_INCREMENT, INITIAL_DAMAGE));
   }

   public boolean fakeSkill() {
      return rep == null;
   }

   @Override
   public Shape getRep() {
      if (rep.isEmpty()) {
         return null;
      } else {
         return new Area(rep.get(rep.size() - 1));
      }

   }

   public int factor() {
      return factor;
   }

   @Override
   protected long elapsedTime() {
      throw new RuntimeException("Not suppose to call this!");
   }

   @Override
   public void setActivate(boolean activate) {
      throw new UnsupportedOperationException("Not suppose to call this.");
   }

   public static void setInitialDamage(double initialDamage) {
      FirstSkill.initialDamage = initialDamage;
   }

   public static double initialDamage() {
      return initialDamage;
   }
}