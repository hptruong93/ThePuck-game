package Main.Units.Living;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MainBuilding extends LivingUnit {

   private static Area rep;
   public static final int SIZE = 100;
   public static final int REP_SIZE = 7;
   public static final int DEFAULT_HEALTH = 30000;
   private static final int NATURAL_HEAL = 10; //Per PROCESSING_RATE
   private static double initialMaxHealth = DEFAULT_HEALTH;

   public MainBuilding(Game game, Pointt position) {
      super(position, ProcessingUnit.PLAYER_SIDE());
      this.setColor(Color.BLACK);
      this.setHeal(NATURAL_HEAL);

      if (ProcessingUnit.TESTING) {
         this.setMaxHealth(2000000);
         this.setHealth(2000000, Units.FORCE_CHANGE);
      } else {
         this.setMaxHealth(initialMaxHealth);
         this.setHealth(initialMaxHealth / 2, Units.FORCE_CHANGE);
      }

      this.setRadius(Pointt.displayToReal(SIZE));
      this.setGame(game);
   }

   @Override
   synchronized public void moveNoCollision(double timeFrame) {// Increase health
      if (this.health() <= 0) {
         ProcessingUnit.setLockFocus(ProcessingUnit.GAME_OVER_LOCK());
      } else {
         this.setHealth(Math.min(DEFAULT_HEALTH, this.health() + heal()), Units.FORCE_CHANGE);
      }
   }

   @Override
   synchronized public void die(double damagingSpeed) {
      super.die(damagingSpeed);

      ArrayList<Area> part;
      part = new ArrayList<>();
      part.add(new Area(rep));
      game.visualEffects().add(new UniversalEffect(deadFragment(),
              new RepInstance(part), Color.BLACK, UniversalEffect.DEFAULT_COUNT_DOWN, damagingSpeed,
              Maths.randomAngle(), UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      a.setTransform(transform);
      a.setPaint(Color.BLACK);

      if (ProcessingUnit.lockFocus() == ProcessingUnit.WIN_LOCK()) {
         a.setFont(new Font("VNI-Algerian", 3, 48));
         a.setPaint(Color.RED);
         a.drawString("END GAME", ProcessingUnit.SCREEN().width / 2 - 300, ProcessingUnit.SCREEN().height / 3);
         return;
      } else if (ProcessingUnit.lockFocus() == ProcessingUnit.GAME_OVER_LOCK()) {
         a.setFont(new Font("VNI-Algerian", 3, 48));
         a.setPaint(Color.BLUE);
         a.drawString("GAME OVER", ProcessingUnit.SCREEN().width / 2 - 250, ProcessingUnit.SCREEN().height / 3);
         return;
      }

      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      if (!this.dead()) {
         a.fill(rep);
         a.setTransform(transform);
         a.translate(display.getX(), display.getY());
         a.setPaint(Color.BLACK);
         a.fill(this.healthBar());
         a.setPaint(Color.BLUE);
         a.fill(this.realHealthBar());
      } else {
         super.plot(a, transform, focus);
      }
   }

   public static void initialize() {
      double r1 = SIZE, r2 = r1 + 10;
      Ellipse2D outer = new Ellipse2D.Double(-r2, -r2, 2 * r2, 2 * r2);
      Ellipse2D inner = new Ellipse2D.Double(-r1, -r1, 2 * r1, 2 * r1);

      Area whole = new Area(outer);
      Area tam1 = new Area(inner);
      whole.subtract(tam1);

      double sideOuterCore = r1 / Math.sqrt(2);
      Rectangle2D outerCore = new Rectangle2D.Double(-sideOuterCore, -sideOuterCore, 2 * sideOuterCore, 2 * sideOuterCore);
      double sideInnerCore = sideOuterCore - 10;
      Rectangle2D innerCore = new Rectangle2D.Double(-sideInnerCore, -sideInnerCore, 2 * sideInnerCore, 2 * sideInnerCore);

      tam1 = new Area(innerCore);
      whole.add(new Area(outerCore));
      whole.subtract(tam1);

      Area total = new Area(whole);
      double factor = (sideOuterCore) / r2;

      for (int i = 0; i < 20; i++) {
         whole.transform(AffineTransform.getScaleInstance(factor, factor));
         whole.transform(AffineTransform.getRotateInstance(Math.PI / 4));
         total.add(whole);
      }
      rep = total;
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      throw new UnsupportedOperationException("Not suppose to call this.");
   }

   @Override
   public Area getRep() {
      return new Area(Geometry.createEllipse(0, 0, radius(), radius()));
   }

   @Override
   public void processCurses() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setSkillEnable(boolean isActive) {
      //Is an empty method. Not applicable for the main building
   }

   public static void setInitialMaxHealth(double initialMaxHealth) {
      MainBuilding.initialMaxHealth = initialMaxHealth;
   }
}