package Main.Units.Living.Boss.DragonFly;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.NonLiving.Projectile;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class DragonFly extends LivingUnit {

   protected static ArrayList<Area> standardProjectile;
   protected static RepInstance[] repInstances;
   protected static ArrayList<Color> standardColors;
   protected Dive dive;
   private final HashSet<Venom> venoms;
   protected VenomFire venomFire;
   private static final double DISPLAY_RADIUS = 30;
   private static final double INITIAL_HEALTH = 15000;
   private static final double PROJECTILE_SPEED = 0.15;

   public static void initialize() {//Call only once
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
      initProjectile();
      Venom.initialize();
   }

   public DragonFly(Game game, Pointt position) {
      super(game, position, INITIAL_HEALTH, ProcessingUnit.AI_SIDE());
      this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));

      partColors = standardColors;
      currentRepIndex = 0;
      venoms = new HashSet<>();
   }

   @Override
   public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      super.move(time, testUnits, 1);
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      super.plot(a, transform, focus);
   }

   @Override
   public void die(double damagingSpeed) {
      clearTasks();
      super.die(damagingSpeed);

      ArrayList<Area> part;
      for (int i = 0; i < repInstances[1].parts().size(); i++) {
         part = new ArrayList<>();
         part.add(new Area(repInstances[1].parts().get(i)));
         synchronized (game.visualEffects()) {
            game.visualEffects().add(new UniversalEffect(deadFragment(),
                    new RepInstance(part), partColors.get(i), DEFAULT_FRAGMENT_FADE_TIME / 100,
                    damagingSpeed, Maths.randomAngle(), UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   protected abstract boolean threatened();

   @Override
   public Area getRep() {
      return new Area(Geometry.createEllipse(0, 0, DISPLAY_RADIUS, DISPLAY_RADIUS));
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      synchronized (venoms) {
         for (Venom current : venoms) {
            current.plot(a, transform, focus);
         }
      }
   }

   protected abstract class VenomFire extends Attack {

      @Override
      public abstract void run();
   }

   public static final class RepGenerator implements LivingUnit.RepGenerator {

      private static final double LENGTH_TOP = 50, wIDTH_TOP = 6, LENGTH_BOT = 40, WIDTH_BOT = 6;
      public static final Area BODY_TOP = new Area(Geometry.createEllipse(0, -15, 5, 15));
      public static final Area BODY_BOT = new Area(Geometry.createEllipse(0, 11, 2, 19));
      private ArrayList<RepInstance> instances;

      RepGenerator() {
         long start;
         start = Clocks.masterClock.currentTime();
         generateInstances();
      }
      double innerRadius = 10, maxRadius = 30;
      int currentInner = 0;

      @Override
      public final void generateInstances() {
         standardColors = new ArrayList<>();
         instances = new ArrayList<>();
         int i = 0;
         while (i < 11) {
            i = (i + 2) % 14;
            if (i == 0) {
               break;
            }
            ArrayList<Area> parts = instance(i);
            parts.add(new Area(BODY_TOP));
            parts.add(new Area(BODY_BOT));

            for (int j = 0; j < parts.size(); j++) {
               parts.get(j).transform(AffineTransform.getRotateInstance(Math.PI / 2));
            }

            instances.add(new RepInstance(parts));
         }

//            topR topL botR botL top bot
         standardColors.add(new Color(210, 204, 116));
         standardColors.add(new Color(210, 204, 116));
         standardColors.add(new Color(213, 203, 109));
         standardColors.add(new Color(213, 203, 109));
         standardColors.add(new Color(123, 91, 70));
         standardColors.add(new Color(212, 0, 5));

         repInstances = new RepInstance[instances.size()];
         for (int u = 0; u < repInstances.length; u++) {
            repInstances[u] = instances.get(u);
         }
      }

      private ArrayList<Area> instance(int k) {
         Area topW = right(LENGTH_TOP - k * 2, wIDTH_TOP);
         topW.transform(AffineTransform.getTranslateInstance(0, -20));

         Area botW = right(LENGTH_BOT - k * 2, WIDTH_BOT);
         botW.transform(AffineTransform.getRotateInstance(Math.toRadians(10)));
         botW.transform(AffineTransform.getTranslateInstance(0, -12));

         ArrayList<Area> output = new ArrayList<>();

         output.add(topW);
         topW = new Area(topW);
         topW.transform(AffineTransform.getScaleInstance(-1, 1));
         output.add(topW);

         output.add(botW);
         botW = new Area(botW);
         botW.transform(AffineTransform.getScaleInstance(-1, 1));
         output.add(botW);

         return output;
      }

      private Area right(double length, double width) {
         Area topWing = new Area(Geometry.createEllipse(length, 0, length, width));
         Area minus = new Area(new Rectangle2D.Double(-1000, -1000, 1000 + width, 10000));
         topWing.subtract(minus);
         topWing.transform(AffineTransform.getTranslateInstance(-5, 0));
         return topWing;
      }
   }

   private static void initProjectile() {
      standardProjectile = new ArrayList<>();
      GeneralPath defaultShape = Geometry.regular(20, ProjectileGenerator.RADIUS, 3, 1);
      standardProjectile.add(new Area(defaultShape));
   }

   protected final class ProjectileGenerator implements LivingUnit.ProjectileGenerator {

      private static final int RADIUS = 15;
      final Color PROJECTILE_COLOR = Color.MAGENTA;
      private ArrayList<Area> parts;
      private ArrayList<Color> colors;

      @Override
      public Projectile generateProjectile() {
         colors = new ArrayList<>(Arrays.asList(PROJECTILE_COLOR));
         parts = new ArrayList<>();

         parts.add(standardProjectile.get(Venom.THORN_TYPE));

         return new Projectile(DragonFly.this.position().clone(), DragonFly.this.movingAngle(),
                 PROJECTILE_SPEED, DragonFly.this.damage(), colors, parts, RADIUS);
      }

      @Override
      public Projectile generateProjectile(double movingAngle) {
         colors = new ArrayList<>(Arrays.asList(PROJECTILE_COLOR));
         parts = new ArrayList<>();

         parts.add(standardProjectile.get(0));

         return new Projectile(DragonFly.this.position().clone(), movingAngle,
                 PROJECTILE_SPEED, DragonFly.this.damage(), colors, parts, RADIUS);

      }

      @Override
      public Projectile generateProjectile(Color color) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   protected void removeDive() {
      dive = null;
   }

   protected HashSet<Venom> venoms() {
      return venoms;
   }

   protected boolean diveable() {
      return dive == null;
   }

   protected void setDive(Dive dive) {//Can only be called after calling removeDive()
      this.dive = dive;
   }

   protected Dive dive() {
      return dive;
   }
}