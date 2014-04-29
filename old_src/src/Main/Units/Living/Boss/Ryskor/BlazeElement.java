package Main.Units.Living.Boss.Ryskor;

import Buffs.Curse;
import Buffs.Poison;
import Buffs.Slow;
import Buffs.Stun;
import Main.Game;
import Main.MainScreen;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.Projectile;
import Main.Units.RepInstance;
import Main.Units.Units;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlazeElement extends AdvancedBossSkill {

   private static RepInstance instance;
   private static final int UNIT_TIME = 100; //Milliseconds
   private static final double ANGULAR_SPEED = Math.toRadians(360);
   private static final double ROTATING_SPEED = Math.toRadians(-5);
   private static final double INITIAL_SPEED = 0.1;
   private static final double SPEED_INCREMENT = 0.001;
   private static final int INITIAL_DAMAGE = 300; //Impact damage
   private static final double INITIAL_DAMAGING_SPEED = 1;
   private static final int SLOW_DURATION = 2000; //Milliseconds
   private static final double SLOW_FACTOR = Math.pow(Math.E, (2 * Game.map.PROCESSING_RATE() * Math.log(10)) / SLOW_DURATION);
   //Number 8 above can be any constant that is big enough to give an illusion that
   //the unit has stopped after SLOW_DURATION
   private static final int STUN_DURATION = 2000; //Milliseconds
   private static final double TOTAL_FREEZING_DAMAGE = 1000;
   private static final double FREEZING_DAMAGE_OVER_TIME = TOTAL_FREEZING_DAMAGE / ((SLOW_DURATION + STUN_DURATION) / (Game.map.PROCESSING_RATE() / 2));
   private static final byte SLOW_INDEX = 0; //Has to concur with order of add in the effect() ArrayList
   private static final byte STUN_INDEX = 1;
   private static final byte FREEZING_DAMAGE_INDEX = 2;
   public static final double DISPLAY_RADIUS = 20;
   private static ArrayList<Color> standardColors;
   private double rotatingAngle;
   private Blaze container;

   public static void initialize() {
      new BlazeElement();
   }

   private BlazeElement() {
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
   }

   public BlazeElement(Game game, LivingUnit target, Blaze container) {
      super(game, target);
      rotatingAngle = 0;
      this.setMoveCount(Projectile.INFINITE_MOVE_COUNT);
      this.setAngularSpeed(ANGULAR_SPEED);
      this.setSpeed(INITIAL_SPEED);
      this.setDamage(INITIAL_DAMAGE);
      this.setRadius(DISPLAY_RADIUS / Geometry.DISPLAY_REAL_RATIO);
      this.setPosition(container.owner().position().clone());
      this.setDamagingSpeed(INITIAL_DAMAGING_SPEED);
      this.setOwner(null);
      this.container = container;
      createEffectsContainer();
      effect().add(new Slow(SLOW_FACTOR, Curse.INFINITE_STACK, SLOW_DURATION, Curse.DEFAULT_START_TIME));
      effect().add(new Stun(STUN_DURATION, false, Curse.DEFAULT_START_TIME));
      effect().add(new Poison(FREEZING_DAMAGE_OVER_TIME, Curse.INFINITE_STACK, SLOW_DURATION + STUN_DURATION, Curse.DEFAULT_START_TIME));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      if (container.owner().dead() || target().dead()) {
         this.removeFromAllContainer();
      }
   }

   @Override
   public void moveNoCollision(double time) {
      if (!target().dead()) {
         updateMovement(target().position());
         setSpeed(speed() + SPEED_INCREMENT);
         super.moveNoCollision(time);
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      a.setTransform(transform);
      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());

      a.rotate(rotatingAngle);
      rotatingAngle += ROTATING_SPEED;
      instance.plot(a, standardColors);
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      if (touched) {
         if (!target().dead()) {
            if (target().invulnerable()) {
               setRemoving(true);
            } else {
               target().setHealth(target().health() - damage(), Units.MAGICAL_DAMAGE);
               synchronized (target().curses()) {
                  target().curses().add(effect().get(SLOW_INDEX).clone());
                  target().curses().add(effect().get(FREEZING_DAMAGE_INDEX).clone());
               }

               Clocks.masterClock.scheduleOnce(new FollowedStun(), SLOW_DURATION);
               setRemoving(true);
            }
         } else {
            setRemoving(true);
         }
      }
   }

   private class FollowedStun implements Runnable {//Stun does not penetrate repel

      @Override
      public void run() {
         if (!target().dead() && !target().invulnerable()) {
            if (!target().repel()) {
               target().curses().add(effect().get(STUN_INDEX).clone());
            }
         }
      }
   }

   @Override
   public Shape getRep() {
      return Geometry.createEllipse(0, 0, this.radius() * Geometry.DISPLAY_REAL_RATIO, this.radius() * Geometry.DISPLAY_REAL_RATIO);
   }

   @Override
   protected long elapsedTime() {
      throw new UnsupportedOperationException("Not needed");
   }

   @Override
   synchronized public void removeFromAllContainer() {
      synchronized (container) {
         container.removeElement(this);
      }
      super.removeFromAllContainer();
   }

   private class RepGenerator implements Units.RepGenerator {

      @Override
      public void generateInstances() {
         ArrayList<Area> parts = new ArrayList<>();

         double radius = DISPLAY_RADIUS;
         Pointt top = new Pointt(0, -radius); //Top
         Pointt botLeft = new Pointt((-Math.sqrt(3) * radius) / 2, radius / 2); //Bot left
         Pointt botRight = new Pointt((Math.sqrt(3) * radius) / 2, radius / 2); //Bot right
         Triangle starting = new Triangle(top, botLeft, botRight);
         parts.add(starting.rep);

         ArrayList<Triangle> temporary = new ArrayList<>();
         Triangle created;

         created = starting.createBigMiddleLeft();
         parts.add(created.rep);
         temporary.add(created);

         created = starting.createBigMiddleRight();
         parts.add(created.rep);
         temporary.add(created);

         created = starting.createBigMiddleBot();
         parts.add(created.rep);
         temporary.add(created);

         temporary.add(starting.createSmallTop());
         temporary.add(starting.createSmallBotLeft());
         temporary.add(starting.createSmallBotRight());


         int iteration = 0;
         while (iteration < 3) {
            ArrayList<Triangle> temporary1 = new ArrayList<>();
            for (int j = 0; j < temporary.size(); j++) {
               Triangle current = temporary.get(j);

               created = current.createBigMiddleLeft();
               parts.add(created.rep);
               temporary1.add(created);

               created = current.createBigMiddleRight();
               parts.add(created.rep);
               temporary1.add(created);

               temporary1.add(current.createSmallTop());
               temporary1.add(current.createSmallBotLeft());
               temporary1.add(current.createSmallBotRight());
            }
            temporary = temporary1;

            iteration++;
         }

         instance = new RepInstance(parts); //There are 376 parts using 3 iterations
         standardColors = new ArrayList<>();
         for (int i = 0; i < parts.size(); i++) {
            standardColors.add(Color.BLUE);
         }
//            colors = OtherUtilities.getGradient("BlazeElement.txt");
      }

      private class Triangle {

         Pointt p1, p2, p3;
         Area rep;

         private Triangle(Pointt p1, Pointt p2, Pointt p3) {
            //Top has to be p1, Left has to be p2, Right has to be p3
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;

            GeneralPath output = new GeneralPath();
            output.moveTo(p1.getX(), p1.getY());
            output.lineTo(p2.getX(), p2.getY());
            output.lineTo(p3.getX(), p3.getY());
            output.closePath();
            rep = new Area(output);
         }

         private Triangle createBigMiddleLeft() {
            Pointt t2 = p1.trisectionPoint(p2);
            Pointt t1 = p2.trisectionPoint(p1);
            Pointt t3 = completeEquilateral(t2, t1);
            return new Triangle(t3, t1, t2);
         }

         private Triangle createBigMiddleRight() {
            Pointt t1 = p3.trisectionPoint(p1);
            Pointt t2 = p1.trisectionPoint(p3);
            Pointt t3 = completeEquilateral(t1, t2);
            return new Triangle(t3, t2, t1);
         }

         private Triangle createSmallTop() {//One third of big triangle, and on top
            return new Triangle(p1, p1.trisectionPoint(p2), p1.trisectionPoint(p3));
         }

         private Triangle createSmallBotLeft() {
            return new Triangle(p2, p2.trisectionPoint(p3), p2.trisectionPoint(p1));
         }

         private Triangle createSmallBotRight() {
            return new Triangle(p3, p3.trisectionPoint(p1), p3.trisectionPoint(p2));
         }

         private Triangle createBigMiddleBot() {
            Pointt t1 = p2.trisectionPoint(p3);
            Pointt t2 = p3.trisectionPoint(p2);
            Pointt t3 = completeEquilateral(t1, t2);
            return new Triangle(t3, t2, t1);
         }
      }

      private Pointt completeEquilateral(Pointt p1, Pointt p2) {//The third pointt will be the rotated image of p2 with p1: pivot
         return p2.getRotated(p1, Math.toRadians(60));//Clock-wise rotation
      }
   }
}
