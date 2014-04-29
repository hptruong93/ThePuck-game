package Main.Units.Living;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.LivingUnit.Attack;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.NonLiving.Projectile;
import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Creep extends LivingUnit {

   private int type;
   private static RepInstance[] repInstances;
   private static final double DISPLAY_RADIUS = 30;
   private static final double[] speeds = {0.07, 0.07, 0.06, 0.05, 0.04, 0.03};
   public static final int NUMBER_OF_TYPES = speeds.length;
   private static final int[] ranges = {50, 100, 125, 150, 200, 225};
   private static final double[] attackSpeeds = {0.25, 0.5, 0.75, 1, 1.05, 1.15}; //Attack speed In seconds
   private static final int[] damages = {100, 50, 70, 100, 300, 400};
   private static final int[] healths = {1500, 1000, 900, 700, 600, 500};
   public static final Color[] colors = {Color.CYAN, Color.GREEN, Color.BLUE, Color.RED, Color.MAGENTA, Color.BLACK};
   private static final int FRAGMENTS_SIZE = 15;
   private static final String NAME_DEFAULT = "Creep";

   public static void initialize() {
      new Creep(1);
   }

   private Creep(int a) {
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
   }

   public Creep() {// Create fake creep that has no representation
      System.out.println("Warning! Creating fake creep!");
      setPosition(new Pointt(-1, -1));
      this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));
      this.setColor(Color.WHITE);
      type = LivingUnit.FAKE_ENEMY_INDEX;
   }

   public Creep(Game game, Pointt position, int type) {
      super(position, 0, healths[type], ProcessingUnit.AI_SIDE());

      setGame(game);
      this.setTarget(game.puck());
      this.setAngularSpeed(ANGULAR_SPEED);

      currentRepIndex = 0;
      this.setDestination(target().position());

      this.type = type;

      this.setName(NAME_DEFAULT);
      this.setSpeed(speeds[type]);

      this.setRange(ranges[type]);
      this.setAttackSpeed(attackSpeeds[type]);
      this.setDamage(damages[type]);

      this.setColor(colors[type]);

      RepGenerator generator = new RepGenerator();
      generator.fillPartColors(this.color());
      currentRepIndex = 0;
      this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));

      attack = new Attack();
      attack.schedule();
   }

   protected boolean same(Creep creep) {
      return (this.position().samePlace(creep.position())) && (type == creep.type);
   }

   @Override
   synchronized public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      currentRepIndex = (currentRepIndex + 1) % repInstances.length;
      super.move(time, testUnits, 1);
   }

   @Override
   public void die(double damagingSpeed) {
      super.die(damagingSpeed);
      synchronized (game().visualEffects()) {
         ArrayList<Area> part;
         for (int i = 1; i < 10; i++) {
            part = new ArrayList<>();
            part.add(new Area(Geometry.generatePolygon(5, FRAGMENTS_SIZE, FRAGMENTS_SIZE)));
            game().visualEffects().add(new UniversalEffect(deadFragment(),
                    new RepInstance(part), this.color(), DEFAULT_FRAGMENT_FADE_TIME / 100, damagingSpeed,
                    Maths.randomAngle(), UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   @Override
   synchronized public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (!this.dead()) {
         Pointt display;
         a.setTransform(transform);
         display = this.displayPosition(focus);
         //Plot rep
         a.translate(display.getX(), display.getY());
         a.rotate(this.movingAngle() + Math.PI / 2);

         repInstances[currentRepIndex].plot(a, partColors);

         plotProjectile(a, transform);

         //Plot health bar
         a.setTransform(transform);
         this.plotHealthBar(a, display);
      } else {// Plot fragments
         super.plot(a, transform, focus);
      }
   }

   @Override
   public Area getRep() { //Simply a triangle
      if (type != FAKE_ENEMY_INDEX) {
         final int[] xPoly = {0, 20, -20};
         final int[] yPoly = {-36, 18, 18};
         Polygon rep = new Polygon(xPoly, yPoly, 3);
         return new Area(rep);
      } else {
         return new Area(new Polygon(new int[]{0, 0}, new int[]{0, 0}, 2));
      }
   }

   @Override
   public void setSkillEnable(boolean isActive) {
      //Do nothing
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      throw new RuntimeException("Not supposed to use this method.");
   }

   private class Attack extends LivingUnit.Attack {

      @Override
      public void run() {
         if (!Creep.this.target().dead()) {
            if (Creep.this.position().distance(Creep.this.target().position()) < Creep.this.range()) {
               if (Math.abs(Creep.this.movingAngle() - Creep.this.finalAngle()) < Creep.DEFAULT_SHOOTING_ANGLE) {
                  synchronized (projectiles) {
                     projectiles.add(new Projectile(position().clone(), Creep.this.movingAngle(), type, Creep.this.damage()));
                  }
               }
            }
         }
      }
   }

   private class RepGenerator implements Units.RepGenerator {

      private ArrayList<RepInstance> instances;
      private ArrayList<Area> current;
      private ArrayList<Area> originBody;
      private Area original;
      private static final int NUMBER_OF_INSTANCE = 15;
      private static final int NUMBER_OF_BODY_PARTS = 10;
      private static final int LEG_LENGTH = 7;
      private static final int LEG_WIDTH = 2;
      private static final int SHIFT = 1;
      private static final double LEG_ANGULAR = 0.16;

      private void fillPartColors(Color creepColor) {
         //Legs colors
         for (int i = 0; i < 18; i++) {
            partColors.add(Color.BLACK);
         }
         partColors.add(creepColor);
         Color stuff = creepColor.darker();
         for (int i = 0; i < NUMBER_OF_BODY_PARTS; i++) {
            partColors.add(stuff);
         }
      }

      @Override
      public void generateInstances() {
         instances = new ArrayList<>();
         originBody = new ArrayList<>();

         original = createOriginal();
         initializeBody();

         for (int i = 0; i < NUMBER_OF_INSTANCE; i++) {
            current = new ArrayList<>();
            addLegs();
            addBody();
            instances.add(new RepInstance(current));
         }

         repInstances = new RepInstance[instances.size()];
         for (int i = 0; i < repInstances.length; i++) {
            repInstances[i] = instances.get(i);
         }
      }

      private Area createLeg(double x, double y, double angle) {
         Area out = new Area(original);
         out.transform(AffineTransform.getRotateInstance(angle));
         out.transform(AffineTransform.getTranslateInstance(8.5, 0));
         out.transform(AffineTransform.getTranslateInstance(x, y));
         return out;
      }

      private void addBody() {
         current.addAll(originBody);
      }

      private void initializeBody() {
         Area body = new Area(Geometry.createEllipse(0, 0, 10, 30));

         originBody.add(body);

         Area topCircle = new Area(Geometry.createEllipse(0, 0, 28, 28));
         Area botCircle = new Area(Geometry.createEllipse(0, 0, 26, 26));
         topCircle.subtract(botCircle);
         topCircle.transform(AffineTransform.getTranslateInstance(0, 20));
         topCircle.intersect(body);
         topCircle.subtract(new Area(new Rectangle2D.Double(-20, 0, 300, 300)));
         topCircle.transform(AffineTransform.getTranslateInstance(0, -20));


         AffineTransform af = new AffineTransform();
         af.translate(0, 6);
         for (int i = 0; i < NUMBER_OF_BODY_PARTS; i++) {
            topCircle.transform(af);
            Area tam = new Area(topCircle);
            tam.intersect(body);
            originBody.add(tam);
         }
      }

      private void addLegs() {
         Area middle = createLeg(0, 0, LEG_ANGULAR * Math.random());
         current.add(middle);
         middle = createLeg(0, 0, LEG_ANGULAR * Math.random());
         middle.transform(AffineTransform.getScaleInstance(-1, 1));
         current.add(middle);


         for (int i = 1; i < 5; i++) {
            Area tam = createLeg(-2 * i, 6 * i, i * LEG_ANGULAR * Math.random());
            current.add(tam);

            tam = createLeg(-2 * i, 6 * i, i * LEG_ANGULAR * Math.random());
            tam.transform(AffineTransform.getScaleInstance(-1, 1));
            current.add(tam);
         }

         for (int i = 1; i < 5; i++) {
            Area tam = createLeg(-2 * i, -6 * i, -i * LEG_ANGULAR * Math.random());
            current.add(tam);

            tam = createLeg(-2 * i, -6 * i, -i * LEG_ANGULAR * Math.random());
            tam.transform(AffineTransform.getScaleInstance(-1, 1));
            current.add(tam);
         }
      }

      private Area createOriginal() {
         double angle = Math.PI / 4;
         Area legLeft = new Area(new Rectangle2D.Double(0, 0, LEG_LENGTH, LEG_WIDTH));
         Area legRight = new Area(legLeft);
         legRight.transform(AffineTransform.getRotateInstance(angle));
         legRight.transform(AffineTransform.getTranslateInstance(LEG_LENGTH - SHIFT, 0));
         legLeft.add(legRight);
         return new Area(legLeft);
      }
   }
   //Getter & Setter Auto-generated code
}
