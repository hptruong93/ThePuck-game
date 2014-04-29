package Main.Units.NonLiving;

import Main.Game;
import Main.Units.Living.Creep;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.MainBuilding;
import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Projectile extends Units {

   private static final Area STANDARD_REP = new Area(new Ellipse2D.Double(-5, -5, 10, 10));
   private static final Area DEFAULT_SHAPE = new Area(new Ellipse2D.Double(-5, -5, 10, 10));
   private static final int MAX_MOVE_COUNT = 40;//Move MAX_MOVE_COUNT/10 seconds
   public static final int INFINITE_MOVE_COUNT = Integer.MAX_VALUE;
   private boolean removing;
   private int moveCount; //After certain moveNoCollision() called, remove the projectile
   private RepInstance[] repInstance;
   private Area unifiedRepInstance;

   public Projectile() {
      moveCount = MAX_MOVE_COUNT;
   }

   public Projectile(Pointt position, double movingAngle, int type, double damage) {//Standard projectile
      super(position, movingAngle, 0);
      this.setFinalAngle(movingAngle);

      final double[] speeds = {0.05, 0.07, 0.08, 0.09, 0.1, 0.15};
      final Color[] colors = Creep.colors;

      this.setDamage(damage);
      this.setSpeed(speeds[type]);
      this.setXVelocity(this.speed() * Math.cos(movingAngle));
      this.setYVelocity(this.speed() * Math.sin(movingAngle));

      moveCount = MAX_MOVE_COUNT;
      ArrayList<Area> part = new ArrayList<>();
      part.add(new Area(DEFAULT_SHAPE));
      partColors.add(colors[type]);
      repInstance = new RepInstance[1];
      repInstance[0] = new RepInstance(part);
      unifiedRepInstance = STANDARD_REP;
   }

   public Projectile(Pointt position, double movingAngle, double speed, double damage, ArrayList<Color> partColors, ArrayList<Area> rep, double displayRadius) {//Create custom projectile
      super(position, movingAngle, 0);
      this.setFinalAngle(movingAngle);

      this.setDamage(damage);
      setRadius(displayRadius / Geometry.DISPLAY_REAL_RATIO);
      this.setSpeed(speed);
      this.setXVelocity(this.speed() * Math.cos(movingAngle));
      this.setYVelocity(this.speed() * Math.sin(movingAngle));

      this.partColors = partColors;

      moveCount = MAX_MOVE_COUNT;
      repInstance = new RepInstance[1];
      repInstance[0] = new RepInstance(rep);
      unifiedRepInstance = new Area(Geometry.createEllipse(0, 0, displayRadius, displayRadius));
   }

   public Projectile(Pointt position, double movingAngle, double speed, double damage, ArrayList<Color> partColors, RepInstance[] instances, double displayRadius) {//Create custom projectile
      super(position, movingAngle, 0);
      this.setFinalAngle(movingAngle);

      this.setDamage(damage);
      setRadius(displayRadius / Geometry.DISPLAY_REAL_RATIO);
      this.setSpeed(speed);
      this.setXVelocity(this.speed() * Math.cos(movingAngle));
      this.setYVelocity(this.speed() * Math.sin(movingAngle));

      this.partColors = partColors;

      moveCount = MAX_MOVE_COUNT;
      repInstance = instances;
      unifiedRepInstance = new Area(Geometry.createEllipse(0, 0, displayRadius, displayRadius));
   }

   /*
    * Process started in a loop in creep. Passing in the looping position and relevant information.
    * Return -1 if the unit is removed, and 0 if it's not removed
    */
   public final int process(Game game, Iterator<Projectile> currentIterator, Area puckRep) {
      this.moveNoCollision(Game.map.PROCESSING_RATE());

      if (repInstance != null) {
         currentRepIndex = (currentRepIndex + 3) % repInstance.length;
      }

      moveCount--;
      synchronized (currentIterator) {
         if (moveCount <= 0) {
            removeFromAllContainer();
            return -1;
         }
      }

      Pointt display = this.displayPosition(game.focus());

      Area current = new Area(this.getRep().getBounds2D());
//        current.transform(AffineTransform.getRotateInstance(this.movingAngle())); //Assume circle so no rotation required
      current.transform(AffineTransform.getTranslateInstance(display.getX(), display.getY()));
      current.intersect(puckRep);

      synchronized (currentIterator) {
         if (!game.puck().dead() && !current.isEmpty()) {// Touch puck
            handleTouch(game);
            return -1;
         }

         if (!game.mainBuilding().dead() && this.position().distance(game.mainBuilding().position()) < game.mainBuilding().radius()) {
            //Touch main building, delete the projectile
            if (!game.mainBuilding().invulnerable()) {
               game.mainBuilding().setHealth(Math.max(game.mainBuilding().health() - damage(), 0), Units.PHYSICAL_DAMAGE);
               applyEffect(game.mainBuilding(), true);
            }
            removeFromAllContainer();
            return -1;
         }

         if ((this.position().getX() == 0) || (this.position().getY() == 0) || (this.position().getX() == Game.map.REAL_SIZEX()) || (this.position().getY() == (Game.map.REAL_SIZEY()))) {
            //Out of the map, remove as well
            removeFromAllContainer();
            return -1;
         }
      }

      return 0;
   }

   synchronized protected void handleTouch(Game game) {//Return true if the process should exit
      if (!game.puck().transparent()) {
         applyEffect(game.puck(), true);
         setRemoving(true);
      }
   }

   synchronized protected void applyEffect(LivingUnit enemy, boolean touched) {//Skill effect
      if (touched) {
         if (enemy.getClass() == MainBuilding.class) {
         } else {
            enemy.setHealth(enemy.health() - this.damage(), Units.PHYSICAL_DAMAGE);
            if (enemy.health() <= 0) {
               enemy.die(this.speed());
            }
         }
      }
   }

   @Override
   synchronized public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      a.setTransform(transform);
      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.rotate(this.movingAngle());
      repInstance[currentRepIndex].plot(a, partColors);
   }

   @Override
   public Shape getRep() {
      if (unifiedRepInstance == null) {
         return Geometry.createEllipse(0, 0, radius() * Geometry.DISPLAY_REAL_RATIO, radius() * Geometry.DISPLAY_REAL_RATIO);
      } else {
         return unifiedRepInstance;
      }
   }

   synchronized public void removeFromAllContainer() {
      setRemoving(true);
   }

   public void setMoveCount(int moveCount) {
      this.moveCount = moveCount;
   }

   public int moveCount() {
      return moveCount;
   }

   public boolean removing() {
      return removing;
   }

   public void setRemoving(boolean removing) {
      this.removing = removing;
   }
}
