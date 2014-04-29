package Main.Units.Living.Boss.Arachnardus;

import Buffs.Curse;
import Main.Units.Living.Boss.BossSkill;
import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.RepInstance;
import Buffs.Slow;
import Main.Units.NonLiving.Projectile;
import Main.Units.Units;
import Main.MainScreen;
import Features.Audio;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SpiderNet extends BossSkill {

   private static RepInstance instance;
   private static ArrayList<Color> standardColors;
   private double rotatingAngle;
   private LivingUnit target;
   private static final double SPEED = 0.09; //Distance moved per timeFrame
   private static final double ROTATING_SPEED = Math.toRadians(5);
   private static final Color DEFAULT_COLOR = Color.RED;
   private static final double RADIUS = 20; //Display radius
   private static final int SKILL_TIME = 3000; //Milliseconds
   private static final long INITIAL_START_TIME = -1;
   private static final int UNIT_TIME = 100; //Milliseconds
   private static final double ANGULAR_SPEED = Math.toRadians(360);
   public static final double RANGE = 350;

   public static void initialize() {
      new SpiderNet();
   }

   private SpiderNet() {// This is used to initialize
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
   }

   public SpiderNet(Game gameMap, LivingUnit target, Pointt position, Arachnardus owner) {
      super(gameMap);
      partColors = standardColors;

      this.setMoveCount(Projectile.INFINITE_MOVE_COUNT);
      this.setSpeed(SPEED);
      this.setPosition(position);
      this.setAngularSpeed(ANGULAR_SPEED);
      this.target = target;
      rotatingAngle = 0;
      this.setRadius(RADIUS / Geometry.DISPLAY_REAL_RATIO);
      this.setStartTime(INITIAL_START_TIME);
      this.setOwner(owner);

      this.createEffectsContainer();
      this.effect().add(new Slow(Slow.FREEZE, Curse.NO_STACK, SKILL_TIME, Curse.DEFAULT_START_TIME));
      Audio.playSound(Audio.SPIDERNET_THROW);
   }

   @Override
   public void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      if (target.dead() || elapsedTime() > SKILL_TIME) {
         removeFromAllContainer();
      }
   }

   @Override
   public void moveNoCollision(double time) {
      if (target.dead()) {
         this.removeFromAllContainer();
      } else if (!caught()) {
         rotatingAngle += Math.toRadians(ROTATING_SPEED);
         if (!target.dead()) {
            this.updateMovement(target.position());
         }
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
      instance.plot(a, partColors);
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {//Skill effect
      if (affectedUnit.dead()) {
         removeFromAllContainer();
         return;
      } else if (elapsedTime() > SKILL_TIME) {
         removeFromAllContainer();
      }

      if (!caught()) {
         if (touched) {
            synchronized (affectedUnit) {
               if (!affectedUnit.invulnerable()) {
                  setPosition(affectedUnit.position());
                  setStartTime(Clocks.masterClock.currentTime());
                  affectedUnit.curses().add(effect().get(0).clone());
                  schedule();
               } else {//Invulnerable --> Remove
                  removeFromAllContainer();
               }
            }
         }
      } else {
         this.setPosition(affectedUnit.position());
      }
   }

   @Override
   public void removeFromAllContainer() {
      super.removeFromAllContainer();
      synchronized (owner().webs()) {
         owner().webs().remove(this);
      }
   }

   @Override
   public Shape getRep() {
      return Geometry.createEllipse(0, 0, RADIUS, RADIUS);
   }

   private class RepGenerator implements Units.RepGenerator {

      @Override
      public void generateInstances() {
         standardColors = new ArrayList<>();
         ArrayList part = new ArrayList<>();

         Area whole = new Area();

         double width = 40;
         double height = 50;

         double ratio = width / height;

         Area big = Geometry.createRectangle(0, 0, 100, 100);
         Area standard = new Area(Geometry.createRectangle(0, 0, width, height));
         big.subtract(standard);


         double aa = width, bb = height;
         while (aa > 0) {
            Area a1 = new Area(Geometry.createEllipse(0, 0, aa, bb));
            Area a2 = new Area(Geometry.createEllipse(0, 0, aa - 0.5, bb - 0.5));
            a1.subtract(a2);

            whole.add(a1);

            aa -= 3;
            bb -= 3 / ratio;
         }

         Area line = Geometry.createRectangle(0, 0, 0.5, Math.max(width, height));
         int divide = 20;
         for (int i = 0; i < divide; i++) {
            line.transform(AffineTransform.getRotateInstance(Math.toRadians(360.0 / divide)));
            Area tam = new Area(line);
            tam.subtract(big);
            whole.add(tam);
         }

         part.add(whole);
         standardColors.add(DEFAULT_COLOR);
         instance = new RepInstance(part);
      }
   }

   private boolean caught() {
      return !(this.startTime() == INITIAL_START_TIME);
   }

   @Override
   protected long elapsedTime() {
      if (!caught()) {
         return 0;
      } else {
         return Clocks.masterClock.currentTime() - this.startTime();
      }
   }

   @Override
   protected Arachnardus owner() {
      return (Arachnardus) super.owner();
   }

   @Override
   protected final void setOwner(LivingUnit owner) {
      super.setOwner((Arachnardus) owner);
   }
}
