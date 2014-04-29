package Main.Units.Living.Boss.Arachnardus;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.NonLiving.Projectile;
import Main.Units.RepInstance;
import Main.Units.Units;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class Arachnardus extends LivingUnit {

   private ArrayList<SpiderNet> webs;
   private NetShield shield;
   private static ArrayList<Color> standardColors;
   private static RepInstance[] repInstances;
   private static final double SPEED_INITIAL = 0.04;
   private static final double DISPLAY_RADIUS = 30;
   private static final double INITIAL_HEALTH = 3000;
   private static final double ATTACK_SPEED = 2.0; //Seconds
   private static final double PROJECTILE_SPEED = 0.15;
   private static final Color DEFAULT_COLOR = Color.DARK_GRAY;
   private static final double RANGE_INITIAL = 200;
   private static final double DAMAGE_INITIAL = 750;
   private static final double WEB_RATE = 0.05;
   private static final double SHIELD_RATE = 0.02;
   private static final String NAME_DEFAULT = "Arachnardus";

   public static void initialize() {
      new Arachnardus();
   }

   private Arachnardus() {
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
      NetShield.initialize();
      SpiderNet.initialize();
   }

   public Arachnardus(Game game, Pointt position) {
      super(game, position, INITIAL_HEALTH, ProcessingUnit.AI_SIDE());
      this.setName(NAME_DEFAULT);
      this.setAttackSpeed(ATTACK_SPEED);
      this.setSpeed(SPEED_INITIAL);
      this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));
      this.setRange(RANGE_INITIAL);
      this.setDamage(DAMAGE_INITIAL);
      this.setColor(DEFAULT_COLOR);

      partColors = standardColors;
      webs = new ArrayList<>();
      currentRepIndex = 0;

      setProjectileGenerator(new ProjectileGenerator());
      attack = new Attack();
      attack.schedule();
   }

   @Override
   synchronized public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      currentRepIndex = (currentRepIndex + 1) % repInstances.length;
      super.move(time, testUnits, 1);
   }

   @Override
   synchronized public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      super.plot(a, transform, focus);
      a.setTransform(transform);
      a.setPaint(Color.BLACK);
      if (!this.dead()) {
         Pointt display = this.displayPosition(focus);
         a.translate(display.getX(), display.getY());
         a.rotate(this.movingAngle());
         repInstances[currentRepIndex].plot(a, partColors);

         plotProjectile(a, transform);
         plotAttackUnits(a, transform, focus);

         a.setTransform(transform);
         plotHealthBar(a, display);
      }
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      try {
         synchronized (projectiles) {
            for (int i = 0; i < webs.size(); i++) {
               if (!projectiles.contains(webs.get(i))) {
                  webs.get(i).plot(a, transform, focus);
               }
            }
         }
         if (shield != null) {
            shield.plot(a, transform, focus);
         }
      } catch (NullPointerException | IndexOutOfBoundsException e) {//Already deleted
      }
   }

   @Override
   public void die(double damagingSpeed) {
      clearTasks();
      super.die(damagingSpeed);

      synchronized (game.visualEffects()) {
         ArrayList<Area> part;
         for (int i = 0; i < repInstances[1].parts().size(); i++) {
            part = new ArrayList<>();
            part.add(new Area(repInstances[1].parts().get(i)));
            game.visualEffects().add(new UniversalEffect(deadFragment(),
                    new RepInstance(part), partColors.get(i), DEFAULT_FRAGMENT_FADE_TIME / 100, damagingSpeed,
                    Maths.randomAngle(), UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   @Override
   public Area getRep() {
      return new Area(Geometry.createEllipse(0, 0, DISPLAY_RADIUS, DISPLAY_RADIUS));
   }

   private class Attack extends LivingUnit.Attack {

      @Override
      public void run() {
         if ((!Arachnardus.this.target().dead())
                 && (Math.abs(Arachnardus.this.movingAngle() - Arachnardus.this.finalAngle()) <= DEFAULT_SHOOTING_ANGLE)) {
            double distance = Arachnardus.this.position().distance(Arachnardus.this.target().position());

            if (skillAble()) {
               if (Math.random() <= WEB_RATE) {
                  if (distance < SpiderNet.RANGE) {
                     SpiderNet net = new SpiderNet(game(), game().puck(), Arachnardus.this.position().clone(), Arachnardus.this);
                     net.schedule();
                     webs.add(net);
                     synchronized (projectiles) {
                        projectiles.add(net);
                     }
                  }
               }

               if (Math.random() <= SHIELD_RATE) {
                  if (shield == null) {
                     shield = new NetShield(game(), Arachnardus.this);
                     shield.schedule();
                  }
               }
            }

            if (distance < Arachnardus.this.range()) {
               synchronized (projectiles) {
                  projectiles.add(projectileGenerator().generateProjectile());
               }
            }
         }
      }
   }

   public class RepGenerator implements Units.RepGenerator {

      private ArrayList<RepInstance> instances;
      private double biasedAngle = Math.PI / 2;
      Area firstLeg, secondLeg, thirdLeg, fourthLeg; //On the right hand side
      Area firstLegg, secondLegg, thirdLegg, fourthLegg; //On the left hand side
      double angle = 8;

      public RepGenerator() {
         long start, end;
         start = Clocks.masterClock.currentTime();
      }

      @Override
      public void generateInstances() {
         Area body, eyes;

         standardColors = new ArrayList<>();
         instances = new ArrayList<>();
         body = generateBody();
         eyes = generateEyes();

         for (int j = 0; j < 8; j++) {
            standardColors.add(Color.BLACK);
         }
         standardColors.add(DEFAULT_COLOR);
         standardColors.add(Color.RED);

         for (int i = 0; i < angle; i++) {
            ArrayList<Area> parts = new ArrayList<>();

            firstLeg = firstLeg(-i);
            secondLeg = secondLeg(i);
            thirdLeg = thirdLeg(-i);
            fourthLeg = fourthLeg(i);

            firstLegg = flip(firstLeg(-angle + i));
            secondLegg = flip(secondLeg(angle - i));
            thirdLegg = flip(thirdLeg(-angle + i));
            fourthLegg = flip(fourthLeg(angle - i));

            fixBiased();

            parts.add(firstLeg);
            parts.add(secondLeg);
            parts.add(thirdLeg);
            parts.add(fourthLeg);

            parts.add(firstLegg);
            parts.add(secondLegg);
            parts.add(thirdLegg);
            parts.add(fourthLegg);

            parts.add(new Area(body));
            parts.add(new Area(eyes));

            instances.add(new RepInstance(parts));
         }
         for (int i = 0; i < angle; i++) {
            ArrayList<Area> parts = new ArrayList<>();

            firstLeg = firstLeg(-angle + i);
            secondLeg = secondLeg(angle - i);
            thirdLeg = thirdLeg(-angle + i);
            fourthLeg = fourthLeg(angle - i);

            firstLegg = flip(firstLeg(-i));
            secondLegg = flip(secondLeg(i));
            thirdLegg = flip(thirdLeg(-i));
            fourthLegg = flip(fourthLeg(i));

            fixBiased();

            parts.add(firstLeg);
            parts.add(secondLeg);
            parts.add(thirdLeg);
            parts.add(fourthLeg);

            parts.add(firstLegg);
            parts.add(secondLegg);
            parts.add(thirdLegg);
            parts.add(fourthLegg);

            parts.add(new Area(body));
            parts.add(new Area(eyes));

            instances.add(new RepInstance(parts));
         }
         try {
            repInstances = new RepInstance[instances.size()];
            for (int i = 0; i < repInstances.length; i++) {
               repInstances[i] = instances.get(i);
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      private void fixBiased() {
         firstLeg.transform(AffineTransform.getRotateInstance(biasedAngle));
         secondLeg.transform(AffineTransform.getRotateInstance(biasedAngle));
         thirdLeg.transform(AffineTransform.getRotateInstance(biasedAngle));
         fourthLeg.transform(AffineTransform.getRotateInstance(biasedAngle));

         firstLegg.transform(AffineTransform.getRotateInstance(biasedAngle));
         secondLegg.transform(AffineTransform.getRotateInstance(biasedAngle));
         thirdLegg.transform(AffineTransform.getRotateInstance(biasedAngle));
         fourthLegg.transform(AffineTransform.getRotateInstance(biasedAngle));
      }

      private Area generateEyes() {
         Area whole = new Area();
         whole.add(new Area(Geometry.createEllipse(-5, -30, 3, 3)));
         whole.add(new Area(Geometry.createEllipse(5, -30, 3, 3)));
         whole.transform(AffineTransform.getRotateInstance(biasedAngle));
         return whole;
      }

      private Area generateBody() {
         Area whole = new Area();
         double lengthTop = 25;
         double widthTop = 13.25;
         double lengthBot = 25;
         double widthBot = 17;

         whole.add(new Area(Geometry.createEllipse(0, -lengthTop / 2, widthTop, lengthTop)));
         whole.add(new Area(Geometry.createEllipse(0, lengthBot, widthBot, lengthBot)));
         whole.transform(AffineTransform.getRotateInstance(biasedAngle));
         return whole;
      }

      private Area firstLeg(double turning) {
         Area first = basicLeg(50, 5);
         Area second = turnedLeg(20, 4, 30);
         second.transform(AffineTransform.getTranslateInstance(47, 0));

         Area whole = new Area();
         whole.add(first);
         whole.add(second);
         whole.transform(AffineTransform.getRotateInstance(-rad(70) + rad(turning)));
         whole.transform(AffineTransform.getTranslateInstance(0, -18));
         return whole;
      }

      private Area secondLeg(double turning) {
         Area first = basicLeg(40, 5);
         Area second = turnedLeg(22, 4, 10);
         second.transform(AffineTransform.getTranslateInstance(38, 0));
         Area whole = new Area();
         whole.add(first);
         whole.add(second);
         whole.transform(AffineTransform.getRotateInstance(-rad(35) + rad(turning)));
         whole.transform(AffineTransform.getTranslateInstance(0, -18));
         return whole;
      }

      private Area thirdLeg(double turning) {
         Area first = basicLeg(35, 5);
         Area second = turnedLeg(22, 4, 10);
         second.transform(AffineTransform.getTranslateInstance(32, 0));
         Area whole = new Area();
         whole.add(first);
         whole.add(second);
         whole.transform(AffineTransform.getRotateInstance(rad(5) + rad(turning)));
         whole.transform(AffineTransform.getTranslateInstance(0, -15));
         return whole;
      }

      private Area fourthLeg(double turning) {
         Area first = basicLeg(50, 5);
         Area second = turnedLeg(20, 4, 20);
         second.transform(AffineTransform.getTranslateInstance(47, 0));
         Area whole = new Area();
         whole.add(first);
         whole.add(second);
         whole.transform(AffineTransform.getRotateInstance(rad(30) + rad(turning)));
         whole.transform(AffineTransform.getTranslateInstance(0, -15));
         return whole;
      }

      private double rad(double a) {
         return Math.toRadians(a);
      }

      private Area flip(Area area) {
         area.transform(AffineTransform.getScaleInstance(-1, 1));
         return area;
      }

      private Area turnedLeg(double width, double length, double angle) {
         Area whole = basicLeg(width, length);
         whole.transform(AffineTransform.getRotateInstance(Math.toRadians(angle)));
         return whole;
      }

      private Area basicLeg(double width, double length) {
         Area output = new Area(new RoundRectangle2D.Double(0, 0, width, length, 3, 3));
         return output;
      }
   }

   private class ProjectileGenerator implements LivingUnit.ProjectileGenerator {

      private static final int RADIUS = 10;
      private ArrayList<Area> parts;
      private ArrayList<Color> colors;

      @Override
      public Projectile generateProjectile() {
         parts = new ArrayList<>();
         colors = new ArrayList<>();

         Area left = new Area(Geometry.createEllipse(-5, 0, 10, 10));
         Area right = new Area(Geometry.createEllipse(5, 0, 10, 10));
         left.exclusiveOr(right);

         parts.add(left);
         colors.add(Color.GREEN);

         return new Projectile(Arachnardus.this.position().clone(), Arachnardus.this.movingAngle(),
                 PROJECTILE_SPEED, Arachnardus.this.damage(), colors, parts, RADIUS);
      }

      @Override
      public Projectile generateProjectile(Color color) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      @Override
      public Projectile generateProjectile(double movingAngle) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   protected void resetShield() {
      shield = null;
   }

   protected ArrayList<SpiderNet> webs() {
      return webs;
   }
}
