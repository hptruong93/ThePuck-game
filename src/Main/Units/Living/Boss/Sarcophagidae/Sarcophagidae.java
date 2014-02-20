package Main.Units.Living.Boss.Sarcophagidae;

import Features.Audio;
import Features.Clocks;
import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.Projectile;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

public class Sarcophagidae extends LivingUnit {

   protected static RepInstance[] repInstances;
   protected static RepInstance[] attackInstances;
   protected static ArrayList<Color> standardColors;
   protected static RepInstance[] projectileInstance;
   protected static ArrayList<Color> projectileColors;
   private HashSet<SarcophagidaeShadow> illusions;
   private ExponentialGrowth exponentialGrowth;
   private SuicidalAttack suicidalAttack;
   private BoundOfFreedom boundOfFreedom;
   private ShadowsDance shadowsDance;
   private static final double SPEED_INITIAL = 0.07;
   private static final double DISPLAY_RADIUS = 30;
   private static final double INITIAL_HEALTH = 100000;
   private static final double ATTACK_SPEED = 1; //Seconds
   private static final double BAT = 0.1;
   protected static final double PROJECTILE_SPEED = 0.15;
   protected static final Color PROJECTILE_COLOR = new Color(0, 255, 229);
   private static final Color DEFAULT_COLOR = Color.GREEN;
   private static final double RANGE_INITIAL = 200;
   private static final double DAMAGE_INITIAL = 500;
   private static final String NAME_DEFAULT = "Sarcophagidae";

   public static void initialize() {
      new Sarcophagidae();
      initializeProjectile();
      ExponentialGrowth.initialize();
      FreezingProjectile.initialize();
   }

   private Sarcophagidae() {
      new RepGenerator();
   }

   public Sarcophagidae(Game game, Pointt position) {
      super(game, position, INITIAL_HEALTH, ProcessingUnit.AI_SIDE());
      this.setName(NAME_DEFAULT);
      this.setAttackSpeed(ATTACK_SPEED);
      this.setAngularSpeed(ANGULAR_SPEED);
      this.setSpeed(SPEED_INITIAL);
      this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));
      this.setRange(RANGE_INITIAL);
      this.setDamage(DAMAGE_INITIAL);
      this.setBat(BAT);
      this.setColor(DEFAULT_COLOR);

      illusions = new HashSet<>();

      partColors = standardColors;
      currentRepIndex = 0;

      setProjectileGenerator(new ProjectileGenerator());
      boundOfFreedom = new BoundOfFreedom(game, this);
      exponentialGrowth = new ExponentialGrowth(game, this);
      suicidalAttack = new SuicidalAttack(game, this);
      shadowsDance = new ShadowsDance(game, this);

      attack = new Attack();
      attack.schedule();
   }

   @Override
   synchronized public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      if (currentAttackIndex() == LivingUnit.READY_TO_ATTACK) {
         projectiles.add(projectileGenerator().generateProjectile(PROJECTILE_COLOR));
         setCurrentAttackIndex(LivingUnit.NOT_ATTACKING_REP_INDEX);
      } else if (currentAttackIndex() == LivingUnit.NOT_ATTACKING_REP_INDEX) {
         currentRepIndex = (currentRepIndex + 1) % repInstances.length;
      }

      if (!illusions.isEmpty()) {
         setDamage(DAMAGE_INITIAL * illusions.size());
      }


      if (shadowsDance.available()) {
         shadowsDance.setActivate(true, false);
      }

      if (boundOfFreedom.available()) {
         boundOfFreedom.setActivate(true, false);
      }

      if (!exponentialGrowth.activate() && exponentialGrowth.available()) {
         exponentialGrowth.setActivate(true, false);
      }

      if (suicidalAttack.available()) {
         if (illusions.size() > 19 || SuicidalAttack.damageFunction(illusions.size()) > target().health()) {
            suicidalAttack.setActivate(true, false);
         }
      }

      super.move(time, testUnits, thisCanBeAnyInt);
   }

   @Override
   synchronized public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      super.plot(a, transform, focus);

      if (!this.dead()) {
         a.setTransform(transform);
         a.setPaint(Color.BLACK);
         Pointt display = this.displayPosition(focus);
         a.translate(display.getX(), display.getY());
         a.rotate(this.movingAngle());

         int tam;

         if (currentAttackIndex() < 0) {
            repInstances[currentRepIndex].plot(a, partColors);
            for (int i = 0; i < 5; i++) {
               a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - i / 4.0f));
               tam = (currentRepIndex - i) % repInstances.length;
               if (tam < 0) {
                  tam += repInstances.length;
               }
               repInstances[tam].plot(a, partColors);
            }
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
         } else {
            attackInstances[currentAttackIndex()].plot(a, partColors);
            for (int i = 0; i < 5; i++) {
               a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - i / 4.0f));
               tam = (currentRepIndex - i) % repInstances.length;
               if (tam < 0) {
                  tam += attackInstances.length;
               }
               attackInstances[(tam) % attackInstances.length].plot(a, partColors);
            }
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            setCurrentAttackIndex(currentAttackIndex() + (int) ((attackInstances.length * Game.map.TIME_FRAME()) / (bat() * 1000)));
            if (currentAttackIndex() >= attackInstances.length) {
               setCurrentAttackIndex(LivingUnit.READY_TO_ATTACK);
            }
         }

         plotProjectile(a, transform);
         plotAttackUnits(a, transform, focus);

         a.setTransform(transform);
         plotHealthBar(a, display);
      } else {
         super.plot(a, transform, focus);
      }
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      exponentialGrowth.plot(a, transform, focus);
      boundOfFreedom.plot(a, transform, focus);
   }

   @Override
   public void die(double damagingSpeed) {
      super.die(damagingSpeed);

      exponentialGrowth.setActivate(false, false);

      synchronized (game.visualEffects()) {
         ArrayList<Area> part;
         for (int i = 0; i < repInstances[0].parts().size(); i++) {
            part = new ArrayList<>();
            part.add(new Area(repInstances[0].parts().get(i)));
            game.visualEffects().add(new UniversalEffect(position(),
                    new RepInstance(part), partColors.get(i), DEFAULT_FRAGMENT_FADE_TIME / 100,
                    damagingSpeed, Math.random() * Math.PI * 2, UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   protected void addIllusion(SarcophagidaeShadow illusion, int delay) {
      Clocks.masterClock.scheduleOnce(new AddIllusion(illusion), delay);
   }

   @Override
   public Area getRep() {
      return new Area(new Ellipse2D.Double(-DISPLAY_RADIUS, -DISPLAY_RADIUS, 2 * DISPLAY_RADIUS, 2 * DISPLAY_RADIUS));
   }

   private final class AddIllusion implements Runnable {

      private final SarcophagidaeShadow toBeAdd;

      private AddIllusion(SarcophagidaeShadow toBeAdd) {
         this.toBeAdd = toBeAdd;
      }

      @Override
      public void run() {
         synchronized (game.enemies()) {
            game.enemies().add(toBeAdd);
         }

         synchronized (illusions) {
            illusions.add(toBeAdd);
         }
      }
   }

   private class Attack extends LivingUnit.Attack {

      @Override
      public void run() {
         if ((!Sarcophagidae.this.target().dead())
                 && (Math.abs(movingAngle() - finalAngle()) <= DEFAULT_SHOOTING_ANGLE)) {
            double distance = position().distance(target().position());

            if (distance < RANGE_INITIAL) {//Shoot normally
               if (currentAttackIndex() == LivingUnit.NOT_ATTACKING_REP_INDEX) {
                  setCurrentAttackIndex(LivingUnit.START_ATTACKING);
               }
            }
         }
      }
   }

   private final class RepGenerator implements Units.RepGenerator {

      boolean up, up2, up3;
      double a1, a2, a3;
      int index;

      public RepGenerator() {
         long start;
         start = Clocks.masterClock.currentTime();

         repInstances = new RepInstance[12];
         attackInstances = new RepInstance[12];

         index = -1;
         a1 = 1;
         a2 = 2.5;
         a3 = 0;
         up = true;
         up2 = false;
         up3 = true;
         for (int i = 0; i < repInstances.length; i++) {
            index++;
            generateInstances();
         }

         index = -1;
         a1 = 1;
         a2 = 2.5;
         a3 = 0;
         up = true;
         up2 = false;
         up3 = true;

         for (int i = 0; i < repInstances.length; i++) {
            index++;
            generateAttackInstances();
         }

         standardColors = new ArrayList<>();

         standardColors.add(new Color(67, 145, 68)); //wing
         standardColors.add(new Color(15, 28, 44)); //Body bot
         standardColors.add(Color.BLACK); //Leg bot
         standardColors.add(Color.BLACK); //Leg bot
         standardColors.add(Color.BLACK); //Leg top
         standardColors.add(Color.BLACK); //Leg top
         standardColors.add(new Color(119, 148, 56)); //Body top
      }

      @Override
      public void generateInstances() {
         if (up) {
            a1 += 0.2;
            if (a1 > 2) {
               up = false;
            }
         } else {
            a1 -= 0.2;
            if (a1 < 1) {
               up = true;
            }
         }

         if (up3) {
            a3 += 3;
            if (a3 > 20) {
               up3 = false;
            }
         } else {
            a3 -= 3;
            if (a3 < 0) {
               up3 = true;
            }
         }

         ArrayList<Area> parts = new ArrayList<>();
         double radius = 40;
         Area whole = new Area(), cut;

         Area wing = new Area(Geometry.createEllipse(0, 0, radius * a1, radius / 2.5));
         cut = new Area(new Rectangle2D.Double(radius * 0.8125, -500, 1000, 1000));
         wing.subtract(cut);
         wing.transform(AffineTransform.getRotateInstance(Math.toRadians(-45)));
         wing.transform(AffineTransform.getTranslateInstance(-radius * 0.575, radius * 0.375));
         Area tam = new Area(wing);
         wing.transform(AffineTransform.getScaleInstance(-1, 1));
         tam.add(wing);
         wing = new Area(tam);
         parts.add(wing);

         whole.add(generateHead(radius, 2.5));

         Area bodyBot = new Area(Geometry.createEllipse(0, radius / 4, radius / 3, radius));
         parts.add(bodyBot);

         Area bodyTop = new Area(Geometry.createEllipse(0, 0, radius / 4, radius / 2));
         cut = new Area(new Rectangle2D.Double(-5000, -5000, 10000, 5000));
         bodyTop.subtract(cut);
         cut = new Area(new Rectangle2D.Double(0, -5000, 10000, 10000));
         bodyTop.subtract(cut);

         Area circle = new Area(Geometry.createEllipse(-radius / 10, radius * 0.6725, radius / 10, radius / 5));
         cut = new Area(new Rectangle2D.Double(-radius / 10, radius * 0.6725 - radius / 5, radius / 10, radius / 5));
         cut.subtract(circle);
         cut.transform(AffineTransform.getTranslateInstance(0, -radius * 0.005));
         bodyTop.add(cut);
         bodyTop.transform(AffineTransform.getScaleInstance(1.1, 1.5));
         bodyTop.transform(AffineTransform.getTranslateInstance(0, -radius * 0.75));
         Area symmetrical = new Area(bodyTop);
         bodyTop.transform(AffineTransform.getScaleInstance(-1, 1));
         bodyTop.add(symmetrical);
         whole.add(bodyTop);


         Area legBot = new Area(new RoundRectangle2D.Double(-1, -10, 2, 20, 2, 2));
         legBot.transform(AffineTransform.getRotateInstance(Math.toRadians(75)));
         legBot.transform(AffineTransform.getTranslateInstance(-12, -18));

         Area lastLegBot = new Area(Geometry.createEllipse(0, 0, 8, 12));
         cut = new Area(Geometry.createEllipse(-2, 0, 8, 12));
         lastLegBot.subtract(cut);
         cut = new Area(new Rectangle2D.Double(-500, -500, 5000, 493));
         lastLegBot.subtract(cut);
         lastLegBot.transform(AffineTransform.getRotateInstance(Math.toRadians(100)));
         lastLegBot.transform(AffineTransform.getTranslateInstance(-26, -22.5));
         legBot.add(lastLegBot);

         legBot.transform(AffineTransform.getTranslateInstance(12, 18));
         legBot.transform(AffineTransform.getRotateInstance(Math.toRadians(a3)));
         legBot.transform(AffineTransform.getTranslateInstance(-12, -18));
         parts.add(new Area(legBot));
         legBot.transform(AffineTransform.getScaleInstance(-1, 1));
         parts.add(legBot);



         Area legTop = new Area(new RoundRectangle2D.Double(-1, -5, 2, 10, 2, 2));
         legTop.transform(AffineTransform.getRotateInstance(Math.toRadians(135)));
         legTop.transform(AffineTransform.getTranslateInstance(-12, -25));

         Area midLegTop = new Area(new RoundRectangle2D.Double(-1, -8, 2, 16, 2, 2));
         midLegTop.transform(AffineTransform.getRotateInstance(Math.toRadians(175)));
         midLegTop.transform(AffineTransform.getTranslateInstance(-15.7, -35));
         legTop.add(midLegTop);

         Area lastLegTop = new Area(new RoundRectangle2D.Double(-1, -5, 2, 10, 2, 2));
         lastLegTop.transform(AffineTransform.getRotateInstance(Math.toRadians(-45)));
         lastLegTop.transform(AffineTransform.getTranslateInstance(-19, -45));
         legTop.add(lastLegTop);
         legTop.transform(AffineTransform.getTranslateInstance(12, 25));
         legTop.transform(AffineTransform.getRotateInstance(Math.toRadians(a3)));
         legTop.transform(AffineTransform.getTranslateInstance(-12, -25));

         parts.add(new Area(legTop));
         legTop.transform(AffineTransform.getScaleInstance(-1, 1));
         parts.add(legTop);

         parts.add(new Area(whole));
         for (int i = 0; i < parts.size(); i++) {
            parts.get(i).transform(AffineTransform.getRotateInstance(Math.PI / 2));
         }

         repInstances[index] = new RepInstance(parts);
      }

      private void generateAttackInstances() {
         if (up) {
            a1 += 0.2;
            if (a1 > 2) {
               up = false;
            }
         } else {
            a1 -= 0.2;
            if (a1 < 1) {
               up = true;
            }
         }

         if (up2) {
            a2 += 0.1;
            if (a2 > 3.5) {
               up2 = false;
            }
         } else {
            a2 -= 0.1;
            if (a2 < 2.5) {
               up2 = true;
            }
         }

         if (up3) {
            a3 += 3;
            if (a3 > 20) {
               up3 = false;
            }
         } else {
            a3 -= 3;
            if (a3 < 0) {
               up3 = true;
            }
         }

         ArrayList<Area> parts = new ArrayList<>();
         double radius = 40;
         Area whole = new Area(), cut;

         Area wing = new Area(Geometry.createEllipse(0, 0, radius * a1, radius / 2.5));
         cut = new Area(new Rectangle2D.Double(radius * 0.8125, -500, 1000, 1000));
         wing.subtract(cut);
         wing.transform(AffineTransform.getRotateInstance(Math.toRadians(-45)));
         wing.transform(AffineTransform.getTranslateInstance(-radius * 0.575, radius * 0.375));
         Area tam = new Area(wing);
         wing.transform(AffineTransform.getScaleInstance(-1, 1));
         tam.add(wing);
         wing = new Area(tam);
         parts.add(wing);

         whole.add(generateHead(radius, a2));

         Area bodyBot = new Area(Geometry.createEllipse(0, radius / 4, radius / 3, radius));
         parts.add(bodyBot);

         Area bodyTop = new Area(Geometry.createEllipse(0, 0, radius / 4, radius / 2));
         cut = new Area(new Rectangle2D.Double(-5000, -5000, 10000, 5000));
         bodyTop.subtract(cut);
         cut = new Area(new Rectangle2D.Double(0, -5000, 10000, 10000));
         bodyTop.subtract(cut);

         Area circle = new Area(Geometry.createEllipse(-radius / 10, radius * 0.6725, radius / 10, radius / 5));
         cut = new Area(new Rectangle2D.Double(-radius / 10, radius * 0.6725 - radius / 5, radius / 10, radius / 5));
         cut.subtract(circle);
         cut.transform(AffineTransform.getTranslateInstance(0, -radius * 0.005));
         bodyTop.add(cut);
         bodyTop.transform(AffineTransform.getScaleInstance(1.1, 1.5));
         bodyTop.transform(AffineTransform.getTranslateInstance(0, -radius * 0.75));
         Area symmetrical = new Area(bodyTop);
         bodyTop.transform(AffineTransform.getScaleInstance(-1, 1));
         bodyTop.add(symmetrical);
         whole.add(bodyTop);


         Area legBot = new Area(new RoundRectangle2D.Double(-1, -10, 2, 20, 2, 2));
         legBot.transform(AffineTransform.getRotateInstance(Math.toRadians(75)));
         legBot.transform(AffineTransform.getTranslateInstance(-12, -18));

         Area lastLegBot = new Area(Geometry.createEllipse(0, 0, 8, 12));
         cut = new Area(Geometry.createEllipse(-2, 0, 8, 12));
         lastLegBot.subtract(cut);
         cut = new Area(new Rectangle2D.Double(-500, -500, 5000, 493));
         lastLegBot.subtract(cut);
         lastLegBot.transform(AffineTransform.getRotateInstance(Math.toRadians(100)));
         lastLegBot.transform(AffineTransform.getTranslateInstance(-26, -22.5));
         legBot.add(lastLegBot);

         legBot.transform(AffineTransform.getTranslateInstance(12, 18));
         legBot.transform(AffineTransform.getRotateInstance(Math.toRadians(a3)));
         legBot.transform(AffineTransform.getTranslateInstance(-12, -18));
         parts.add(new Area(legBot));
         legBot.transform(AffineTransform.getScaleInstance(-1, 1));
         parts.add(legBot);



         Area legTop = new Area(new RoundRectangle2D.Double(-1, -5, 2, 10, 2, 2));
         legTop.transform(AffineTransform.getRotateInstance(Math.toRadians(135)));
         legTop.transform(AffineTransform.getTranslateInstance(-12, -25));

         Area midLegTop = new Area(new RoundRectangle2D.Double(-1, -8, 2, 16, 2, 2));
         midLegTop.transform(AffineTransform.getRotateInstance(Math.toRadians(175)));
         midLegTop.transform(AffineTransform.getTranslateInstance(-15.7, -35));
         legTop.add(midLegTop);

         Area lastLegTop = new Area(new RoundRectangle2D.Double(-1, -5, 2, 10, 2, 2));
         lastLegTop.transform(AffineTransform.getRotateInstance(Math.toRadians(-45)));
         lastLegTop.transform(AffineTransform.getTranslateInstance(-19, -45));
         legTop.add(lastLegTop);
         legTop.transform(AffineTransform.getTranslateInstance(12, 25));
         legTop.transform(AffineTransform.getRotateInstance(Math.toRadians(a3)));
         legTop.transform(AffineTransform.getTranslateInstance(-12, -25));

         parts.add(new Area(legTop));
         legTop.transform(AffineTransform.getScaleInstance(-1, 1));
         parts.add(legTop);

         parts.add(new Area(whole));
         for (int i = 0; i < parts.size(); i++) {
            parts.get(i).transform(AffineTransform.getRotateInstance(Math.PI / 2));
         }

         attackInstances[index] = new RepInstance(parts);
      }

      private Area generateHead(double radius, double a2) {
         Area head = new Area(Geometry.createEllipse(0, 0, radius / 3, radius / 6));
         Area cut = new Area(new Rectangle2D.Double(-5000, radius / 10, 10000, 10000));
         head.subtract(cut);
         cut = new Area(Geometry.createEllipse(0, 0, radius * 0.233, radius * 0.233));
         cut.transform(AffineTransform.getTranslateInstance(0, -radius / a2));
         head.subtract(cut);
         head.transform(AffineTransform.getTranslateInstance(0, -radius / 1.2));
         return head;
      }
   }

   private static void initializeProjectile() {
      projectileInstance = new RepInstance[20];

      double radius = ProjectileGenerator.RADIUS;
      double f = radius / 6;
      double a = 6 * f;
      double b = 3.75 * f;
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

      ArrayList<Area> parts = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
         ar.transform(AffineTransform.getRotateInstance(Math.PI / 2));
         parts.add(new Area(ar));
      }

      for (int i = 0; i < 20; i++) {
         ArrayList current = new ArrayList<>();
         for (int j = 0; j < parts.size(); j++) {
            Area tam = new Area(parts.get(j));
            tam.transform(AffineTransform.getRotateInstance(-2 * i * Math.PI / 20));
            current.add(tam);
         }
         projectileInstance[i] = new RepInstance(current);
      }

      projectileColors = new ArrayList<>();
      for (int i = 0; i < projectileInstance[0].size(); i++) {
         projectileColors.add(PROJECTILE_COLOR);
      }
   }

   protected class ProjectileGenerator implements LivingUnit.ProjectileGenerator {

      protected static final int RADIUS = 25;

      @Override
      public Projectile generateProjectile(Color color) {
         if (exponentialGrowth.activate()) {
            Audio.playSound(Audio.FREEZING_PROJECTILE);
            return new FreezingProjectile(game, position(), Sarcophagidae.this, target(), damage());
         } else {
            return new Projectile(Sarcophagidae.this.position().clone(), Sarcophagidae.this.movingAngle(),
                    PROJECTILE_SPEED, Sarcophagidae.this.damage(), projectileColors, projectileInstance, RADIUS);
         }
      }

      @Override
      public Projectile generateProjectile() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      @Override
      public Projectile generateProjectile(double movingAngle) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   protected HashSet<SarcophagidaeShadow> illusions() {
      return illusions;
   }

   protected ExponentialGrowth exponentialGrowth() {
      return exponentialGrowth;
   }
}