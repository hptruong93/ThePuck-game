package Utilities;

import Main.Game;
import Main.ProcessingUnit;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Pointt {

   private double x;
   private double y;

   public Pointt(Point point) {
      if (Double.isNaN(point.x)) {
         throw new InvalidParameterException("x is Nan. Cannot create");
      }

      if (Double.isNaN(point.y)) {
         throw new InvalidParameterException("y is Nan. Cannot create");
      }

      this.x = point.x;
      this.y = point.y;
   }

   public Ellipse2D getRep() {
      return new Ellipse2D.Double(-5, - 5, 10, 10);
   }

   public Pointt(double x, double y) {

      if (Double.isNaN(x)) {
         throw new InvalidParameterException("x is Nan. Cannot create");
      }

      if (Double.isNaN(y)) {
         throw new InvalidParameterException("y is Nan. Cannot create");
      }

      this.x = x;
      this.y = y;
   }

   public double distance(Pointt p) {
      return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
   }

   public Pointt midPoint(Pointt p) {
      return new Pointt(0.5 * (x + p.x), 0.5 * (y + p.y));
   }

   /**
    *
    * @param The other point
    * @return the point trisects the segment and is closer to this pointt (further away from pointt
    * p)
    */
   public Pointt trisectionPoint(Pointt p) {
      double newX, newY;
      newX = (2 * x) / 3 + p.x / 3;
      newY = (2 * y) / 3 + p.y / 3;
      return new Pointt(newX, newY);
   }

   /**
    * @return angle(a this x-axis)
    */
   public double angle(Pointt a) {
      return Geometry.arcTan(a.y - y, a.x - x, a.x - x);
   }

   public boolean samePlace(Pointt p) {
      return (Math.abs(x - p.x) < 0.00001) && (Math.abs(y - p.y) < 0.00001);
   }

   /**
    *
    * @param pivot: pivot pointt for the rotation
    * @param angle: the angle of rotation in radiant
    * @warning rotation counter-clockwise for positive angle
    * @return a new pointt representing the rotated pointt
    */
   public Pointt getRotated(Pointt pivot, double angle) {
      if (this.samePlace(pivot)) {
         return new Pointt(this.x, this.y);
      }

      Pointt relative = new Pointt(this.x - pivot.x, this.y - pivot.y);
      double relativeAngle = Geometry.arcTan(relative.y, relative.x, relative.x);
      relativeAngle += angle;
      relative.x = this.distance(pivot) * Math.cos(relativeAngle);
      relative.y = this.distance(pivot) * Math.sin(relativeAngle);

      if (Double.isNaN(relative.x)) {
         throw new RuntimeException("Relative x is NaN "+ " :  pivot.x is " + pivot.x + " pivot.y is " + pivot.y + " relativeAngle is " + relativeAngle
                  + " angle input is " + angle);
      }

      if (Double.isNaN(relative.y)) {
         throw new RuntimeException("Relative y is NaN "+ " :  pivot.x is " + pivot.x + " pivot.y is " + pivot.y + " relativeAngle is " + relativeAngle
                  + " angle input is " + angle);
      }

      return new Pointt(relative.x + pivot.x, relative.y + pivot.y);
   }

   /**
    *
    * @param angle: angle in radiant
    * @return a Pointt which is "radius" distant from the original Pointt, with this.angle(return) =
    * angle
    */
   public Pointt getArcPointt(double radius, double angle) {
      Pointt output = new Pointt(x + radius, y);
      output = output.getRotated(this, angle);
      return output;
   }

   public void translate(Pointt translateUnit) {
      x += translateUnit.x;
      y += translateUnit.y;
   }

   public Pointt randomRange(double range) {
      return new Pointt(x + Maths.random(range) - range / 2, y + Maths.random(range) - range / 2);
   }

   public static double displayToReal(double displayLengthToRealLength) {
      return displayLengthToRealLength / Geometry.DISPLAY_REAL_RATIO;
   }

   public static double realToDisplay(double realLengthToDisplayLength) {
      return realLengthToDisplayLength * Geometry.DISPLAY_REAL_RATIO;
   }

   public Pointt realToMini() {
      Pointt output = new Pointt(0, 0);
      output.setX(x / Game.map.SCALE_X());
      output.setY(y / (Game.map.SCALE_Y()));
      return output;
   }

   public Pointt miniToReal() {
      Pointt output = new Pointt(0, 0);
      output.setX(x * Game.map.SCALE_X());
      output.setY(y * Game.map.SCALE_Y());
      return output;
   }

   public Pointt miniToDisplay(Pointt focus) {
      Pointt output = new Pointt(0, 0);
      output.setX((x - focus.x + Game.map.FOCUS_WIDTH() / 2) * Game.map.SCALE_MINIX());
      output.setY((y - focus.y + Game.map.FOCUS_HEIGHT() / 2) * Game.map.SCALE_MINIY());
      return output;
   }

   public Pointt displayToMini(Pointt focus) {
      Pointt output = new Pointt(0, 0);
      output.setX(x / Game.map.SCALE_MINIX() + focus.x - Game.map.FOCUS_WIDTH() / 2);
      output.setY(y / Game.map.SCALE_MINIY() + focus.y - Game.map.FOCUS_HEIGHT() / 2);
      return output;
   }

   public Pointt realToDisplay(Pointt focus) {
      return this.realToMini().miniToDisplay(focus);
   }

   public Pointt displayToReal(Pointt focus) {
      return this.displayToMini(focus).miniToReal();
   }

   public Pointt focus() {
      if (x < Game.map.FOCUS_WIDTH() / 2) {
         x = Game.map.FOCUS_WIDTH() / 2;
      } else {
         x = Math.min(Game.map.MINI_SIZEX() - Game.map.FOCUS_WIDTH() / 2, x);
      }

      if (y < Game.map.FOCUS_HEIGHT() / 2) {
         y = Game.map.FOCUS_HEIGHT() / 2;
      } else {
         y = Math.min(Game.map.MINI_SIZEY() + Game.map.MENU_BAR_WIDTH() - Game.map.MENU_BAR_WIDTH() - Game.map.FOCUS_HEIGHT() / 2, y);
      }
      return this;
   }

   @Override
   public Pointt clone() {
      return new Pointt(this.getX(), this.getY());
   }

   @Override
   public String toString() {
      return "x = " + x + ", y = " + y;
   }

   public void setX(double x) {
      if (Double.isNaN(x)) {
         throw new InvalidParameterException("Cannot set x to be NaN");
      }
      this.x = x;
   }

   public void setY(double y) {
      if (Double.isNaN(y)) {
         throw new InvalidParameterException("Cannot set y to be NaN");
      }
      this.y = y;
   }

   public void setXY(double x, double y) {
      if (Double.isNaN(x)) {
         throw new InvalidParameterException("Cannot set x to be NaN");
      }
      if (Double.isNaN(y)) {
         throw new InvalidParameterException("Cannot set y to be NaN");
      }

      this.x = x;
      this.y = y;
   }

   public void concur(Pointt p) {
      this.x = p.x;
      this.y = p.y;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }
}
