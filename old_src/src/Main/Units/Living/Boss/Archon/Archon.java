package Main.Units.Living.Boss.Archon;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.LivingUnit.Attack;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.NonLiving.Projectile;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.FileUtilities;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Archon extends LivingUnit {

   private NightSilence silent;
   private int innerCurrentRepIndex;
   private int outerColorIndex;
   private int innerColorIndex;
   private static Encircle encircle;
   private static RepInstance[] outerRepInstances;
   private static RepInstance[] innerRepInstances;
   private static ArrayList<Color> standardColors;
   private static final double SPEED_INITIAL = 0.07;
   private static final double ANGULAR_SPEED = Math.toRadians(5);
   private static final double DISPLAY_RADIUS = 30;
   private static final double INITIAL_HEALTH = 6000;
   private static final double ATTACK_SPEED = 0.75; //Seconds
   private static final int ACCELERATE_CONSTANT = 5;
   private static final double ACCELERATED_ATTACK_SPEED = 1000 * ATTACK_SPEED / ACCELERATE_CONSTANT;//Millisecond
   private static final double PROJECTILE_SPEED = 0.15;
   private static final Color PROJECTILE_COLOR = new Color(0, 255, 229);
   private static final Color DEFAULT_COLOR = Color.GREEN;
   private static final double RANGE_INITIAL = 200;
   private static final double DAMAGE_INITIAL = 200;
   private static final double SILENT_RATE = 0.01;
   private static final double ENCIRCLE_RATE = 0.2;
   private static final String NAME_DEFAULT = "Archon";

   public static void initialize() {
      new Archon();
      NightSilence.initialize();
      Encircle.initialize();
   }

   private Archon() {
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
   }

   public Archon(Game gameMap, Pointt position) {
      super(gameMap, position, INITIAL_HEALTH, ProcessingUnit.AI_SIDE());
      this.setName(NAME_DEFAULT);
      this.setAttackSpeed(ATTACK_SPEED);
      this.setAngularSpeed(ANGULAR_SPEED);
      this.setSpeed(SPEED_INITIAL);
      this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));
      this.setRange(RANGE_INITIAL);
      this.setDamage(DAMAGE_INITIAL);
      this.setColor(DEFAULT_COLOR);

      partColors = standardColors;
      currentRepIndex = 0;

      outerColorIndex = (int) (Math.random() * standardColors.size());
      innerColorIndex = (int) (Math.random() * standardColors.size());


      setProjectileGenerator(new ProjectileGenerator());
      attack = new Attack();
      attack.schedule();
   }

   @Override
   synchronized public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      currentRepIndex = (currentRepIndex + 1) % outerRepInstances.length;
      innerCurrentRepIndex = (innerColorIndex + 1) % innerRepInstances.length;
      innerColorIndex = (innerColorIndex + 1) % standardColors.size();
      outerColorIndex = (outerColorIndex + 1) % standardColors.size();

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
         outerRepInstances[currentRepIndex].plot(a, standardColors.get(outerColorIndex));
         innerRepInstances[innerCurrentRepIndex].plot(a, standardColors.get(innerColorIndex));

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
      try {
         if (silent != null) {
            silent.plot(a, transform, focus);
         }
      } catch (NullPointerException e) {//Unit removed
      }
   }

   public static void plotEncircle(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (encircle != null) {
         encircle.plot(a, transform, focus);
      }
   }

   @Override
   public void die(double damagingSpeed) {
      clearTasks();
      super.die(damagingSpeed);

      Random random = new Random(Clocks.masterClock.currentTime());
      synchronized (game.visualEffects()) {
         ArrayList<Area> part;
         for (int i = 0; i < outerRepInstances.length; i++) {
            for (int j = 0; j < outerRepInstances[i].parts().size(); j++) {
               part = new ArrayList<>();
               part.add(new Area(outerRepInstances[i].parts().get(j)));
               game.visualEffects().add(new UniversalEffect(deadFragment(),
                       new RepInstance(part), partColors.get(random.nextInt(partColors.size())),
                       DEFAULT_FRAGMENT_FADE_TIME / 100, damagingSpeed, Maths.randomAngle(),
                       UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.SHRINKING_SCALE));
            }
         }
      }
   }

   @Override
   public Area getRep() {
      return new Area(new Ellipse2D.Double(-DISPLAY_RADIUS, -DISPLAY_RADIUS, 2 * DISPLAY_RADIUS, 2 * DISPLAY_RADIUS));
   }

   private void castEncircle() {
      if (skillAble()) {
         if (Math.random() < ENCIRCLE_RATE) {
            if (encircle == null) {
               encircle = new Encircle(game, game.puck(), this.position());
               encircle.schedule();
            }
         }
      }
   }

   private class Attack extends LivingUnit.Attack {

      private boolean acceleratedAttack;

      @Override
      public void run() {
         if ((!Archon.this.target().dead())
                 && (Math.abs(Archon.this.movingAngle() - Archon.this.finalAngle()) <= DEFAULT_SHOOTING_ANGLE)) {
            double distance = Archon.this.position().distance(Archon.this.target().position());

            castEncircle();
            if (silent != null && silent.caught() && !acceleratedAttack) {//Caught Puck
               acceleratedAttack = true;
               Shoot accelerated = new Shoot();
               for (int i = 0; i < ACCELERATE_CONSTANT - 1; i++) {//Avoid overlapse between threads
                  accelerated.scheduleOnce((long) ACCELERATED_ATTACK_SPEED);
               }
            } else {
               acceleratedAttack = false;
               if (skillAble()) {
                  if (Math.random() <= SILENT_RATE) {//Cast silent
                     silent = new NightSilence(game, game.puck(), Archon.this.position(), Archon.this);
                     silent.schedule();
                  }
               }
               if (distance < RANGE_INITIAL) {//Shoot normally
                  synchronized (projectiles) {
                     projectiles.add(projectileGenerator().generateProjectile(PROJECTILE_COLOR));
                  }
               }
            }
         }
      }
   }

   private class Shoot extends Attack {

      @Override
      public void run() {
         double distance = Archon.this.position().distance(Archon.this.target().position());
         if (distance < RANGE_INITIAL) {//Shoot normally
            synchronized (projectiles) {
               projectiles.add(projectileGenerator().generateProjectile(PROJECTILE_COLOR));
            }
         }
      }
   }

   private final class RepGenerator implements Units.RepGenerator {

      private ArrayList<RepInstance> outer;
      private ArrayList<RepInstance> inner;

      public RepGenerator() {
         long start;
         start = Clocks.masterClock.currentTime();
         generateInstances();
      }
      double innerRadius = 10, maxRadius = 30;
      int currentInner = 0;

      @Override
      public void generateInstances() {
         ArrayList<Area> parts;
         outer = new ArrayList<>();
         for (int i = 5; i < 12; i++) {
            parts = new ArrayList<>();
            parts.add(new Area(Geometry.regular(i, 30, innerRadius, 1)));
            outer.add(new RepInstance(parts));
         }

         int size = outer.size();
         for (int i = size; i < 2 * (size - 1); i++) {
            parts = new ArrayList<>();
            parts.add(new Area(outer.get(2 * size - i - 2).parts().get(0)));
            outer.add(new RepInstance(parts));
         }

         double currentRadius = innerRadius + 1;
         int direction = 1;
         inner = new ArrayList<>();
         while (currentRadius >= innerRadius) {
            parts = new ArrayList<>();
            parts.add(new Area(Geometry.createEllipse(0, 0, currentRadius, currentRadius)));
            inner.add(new RepInstance(parts));
            if (direction == 1) {
               currentRadius += 1;
            } else {
               currentRadius -= 1;
            }

            if (currentRadius >= maxRadius) {
               direction = -1;
            }
         }

         outerRepInstances = new RepInstance[outer.size()];
         for (int i = 0; i < outerRepInstances.length; i++) {
            outerRepInstances[i] = outer.get(i);
         }
         innerRepInstances = new RepInstance[inner.size()];
         for (int i = 0; i < innerRepInstances.length; i++) {
            innerRepInstances[i] = inner.get(i);
         }


         standardColors = FileUtilities.getGradient("ArchonGradient.txt");
      }
   }

   private class ProjectileGenerator implements LivingUnit.ProjectileGenerator {

      private static final int RADIUS = 30;
      private ArrayList<Area> parts;
      private ArrayList<Color> colors;

      @Override
      public Projectile generateProjectile(Color color) {
         parts = new ArrayList<>();
         colors = new ArrayList<>();

         Area right = new Area(Geometry.createEllipse(0, 0, 40, 30));
         Area left = new Area(Geometry.createEllipse(-5, 0, 40, 30));
         right.subtract(left);

         parts.add(right);
         colors.add(color);

         return new Projectile(Archon.this.position().clone(), Archon.this.movingAngle(),
                 PROJECTILE_SPEED, Archon.this.damage(), colors, parts, RADIUS);
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

   public static void resetEncircle() {
      encircle = null;
   }

   protected void setSilent(NightSilence silent) {
      this.silent = silent;
   }
}