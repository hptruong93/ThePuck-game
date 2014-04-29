package Main.Units.Living.Boss.DragonFly;

import Buffs.Curse;
import Main.Game;
import Main.Units.Living.Boss.BossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.RepInstance;
import Buffs.Poison;
import Buffs.Slow;
import Main.Units.Living.MainBuilding;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Venom extends BossSkill {

   private byte type;
   private static RepInstance[] instance;
   private static ArrayList<Color>[] standardColors;
   protected static final byte THORN_TYPE = 0;
   protected static final byte CIRCLE_TYPE = 1;
   private static final double DISPLAY_RADIUS = 30;
   private static final double PROJECTILE_SPEED = 0.09;
   private static final double DAMAGE_OVER_UNIT_TIME = 50;
   private static final double SLOW_FACTOR = 5;
   private static final Color DEFAULT_COLOR = new Color(0x00, 0x8B, 0x00);
   private static final int SKILL_TIME = 3000; //Milliseconds
   private static final int UNIT_TIME = 100; //Milliseconds
   public static final double RANGE = 300;
   private static final long INITIAL_START_TIME = 0;

   public static void initialize() {
      new Venom();
   }

   private Venom() {
      initProjectile();
   }

   Venom(Game game, DragonFly owner, Pointt source, double speed, double movingAngle, byte type) {//speed < 0 --> Default speed
      super(game);
      this.setPosition(source.clone());
      this.setMovingAngle(movingAngle);
      this.setFinalAngle(movingAngle);
      this.setAngularSpeed(Math.toRadians(360));
      if (speed < 0) {
         this.setSpeed(PROJECTILE_SPEED);
      } else {
         this.setSpeed(speed);
      }
      this.setDamage(0);
      this.setOwner(owner);
      this.type = type;
      setStartTime(INITIAL_START_TIME);

      this.createEffectsContainer();
      effect().add(new Slow(SLOW_FACTOR, Curse.NO_STACK, SKILL_TIME, Curse.DEFAULT_START_TIME));
      effect().add(new Poison(DAMAGE_OVER_UNIT_TIME, Curse.INFINITE_STACK, SKILL_TIME, Curse.DEFAULT_START_TIME));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      throw new UnsupportedOperationException("Invalid call. Should be processed with owner projectiles.");
   }

   @Override
   public void moveNoCollision(double time) {
      super.moveNoCollision(time);
   }

   @Override
   synchronized protected void skillEffects(ArrayList<LivingUnit> enemy, Pointt focus) {
      for (int i = 0; i < enemy.size(); i++) {
         LivingUnit current = enemy.get(i);
         if (current.dead()) {
            continue;
         }

         applyEffect(current, current.distance(this) < current.radius());
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (infected()) {
         return;
      }
      a.setTransform(transform);
      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.rotate(this.movingAngle());
      instance[type].plot(a, standardColors[type]);
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {//Skill effect
      if (affectedUnit.getClass() == MainBuilding.class && touched) {
         this.removeFromAllContainer();
      } else {
         synchronized (affectedUnit.curses()) {
            if (!infected() && touched) {
               if (!affectedUnit.invulnerable()) {
                  for (int i = 0; i < effect().size(); i++) {
                     affectedUnit.curses().add(effect().get(i).clone());
                  }
                  this.removeFromAllContainer();
               } else {
                  this.removeFromAllContainer();
               }
            }
         }
      }
   }

   private static void initProjectile() {
      standardColors = new ArrayList[2];
      instance = new RepInstance[2];

      ArrayList<Area> type0;
      type0 = new ArrayList<>();

      int[] polyX = {20, -10, -10};
      int[] polyY = {0, -5, 5};
      Polygon defaultShape = new Polygon(polyX, polyY, 3);
      type0.add(new Area(defaultShape));

      instance[THORN_TYPE] = new RepInstance(type0);

      standardColors[THORN_TYPE] = new ArrayList<>();
      standardColors[THORN_TYPE].add(DEFAULT_COLOR);


      ArrayList<Area> type1 = new ArrayList<>();

      double radius = DISPLAY_RADIUS / Geometry.DISPLAY_REAL_RATIO;
      double innerRadius = radius / 5;
      double middleRadius = radius / 7.5;

      Area center = new Area(Geometry.createEllipse(0, 0, innerRadius, innerRadius));
      type1.add(center);

      Area outer = new Area(Geometry.createEllipse(0, -radius + innerRadius, innerRadius, innerRadius));
      type1.add(outer);
      for (int i = 0; i < 7; i++) {
         outer.transform(AffineTransform.getRotateInstance(Math.toRadians(45)));
         type1.add(new Area(outer));
      }

      Area middle = new Area(Geometry.createEllipse(0, 0.5 * (radius - innerRadius), middleRadius, middleRadius));
      middle.transform(AffineTransform.getRotateInstance(Math.toRadians(45.0 / 2)));
      type1.add(middle);
      for (int i = 0; i < 7; i++) {
         middle.transform(AffineTransform.getRotateInstance(Math.toRadians(45)));
         type1.add(new Area(middle));
      }

      instance[CIRCLE_TYPE] = new RepInstance(type1);
      standardColors[CIRCLE_TYPE] = new ArrayList<>();
      for (int i = 0; i < type1.size(); i++) {
         standardColors[1].add(DEFAULT_COLOR);
      }
   }

   @Override
   public Shape getRep() {
      if (type == 0) {
         return Geometry.createEllipse(0, 0, 20, 20);
      } else {
         return Geometry.createEllipse(0, 0, DISPLAY_RADIUS, DISPLAY_RADIUS);
      }
   }

   @Override
   public void removeFromAllContainer() {
      super.removeFromAllContainer();
      synchronized (owner().venoms()) {
         owner().venoms().remove(this);
      }
   }

   private boolean infected() {
      return !(this.startTime() == INITIAL_START_TIME);
   }

   @Override
   protected long elapsedTime() {
      if (!infected()) {
         return 0;
      } else {
         return Clocks.masterClock.currentTime() - this.startTime();
      }
   }

   @Override
   protected DragonFly owner() {
      return (DragonFly) super.owner();
   }

   @Override
   protected final void setOwner(LivingUnit owner) {
      super.setOwner(owner);
   }
}