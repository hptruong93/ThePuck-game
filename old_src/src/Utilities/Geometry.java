package Utilities;

import Main.Game;
import Main.ProcessingUnit;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class Geometry {

   public static final double DISPLAY_REAL_RATIO = 100.0 / convertLength(100.0); //Length ratio
//   public static final double DISPLAY_REAL_RATIO = 1.0/ (Game.map.REAL_SIZEX() *
//           (Game.map.FOCUS_WIDTH()/Game.map.MINI_SIZEX()) / ProcessingUnit.SCREEN().getWidth());
   private static ProcessingUnit map = Game.map;

   /*
    * Generate random polygon (can be convex or concave)
    */
   public static Polygon generatePolygon(int sides, int boundX, int boundY) {
      Polygon poly = new Polygon();
      Random generate = new Random();
      if (sides <= 2) {
         sides = generate.nextInt(8) + 3;
      }
      for (int i = 0; i < sides; i++) {
         poly.addPoint(generate.nextInt(boundX), generate.nextInt(boundY));
      }
      return poly;
   }

   public static GeneralPath regular(int n, double radius, double innerRadius, int canBeAnyInt) {
      GeneralPath output = new GeneralPath();
      ArrayList<Pointt> outt = regular(n, radius, 0);
      ArrayList<Pointt> inn = regular(n, innerRadius, Math.toRadians(360 / (n * 2.0)));
      output.moveTo(outt.get(0).getX(), outt.get(0).getY());
      for (int i = 0; i < outt.size(); i++) {
         output.lineTo(inn.get(i).getX(), inn.get(i).getY());
         if (i + 1 >= outt.size()) {
            break;
         }
         output.lineTo(outt.get(i + 1).getX(), outt.get(i + 1).getY());
      }
      output.closePath();
      return output;
   }

   public static ArrayList<Pointt> regular(int n, double radius, double rotatedAngle) {
      ArrayList<Pointt> output = new ArrayList<>();
      Pointt starting = new Pointt(0, radius);
      Pointt origin = new Pointt(0, 0);
      starting = starting.getRotated(new Pointt(0, 0), rotatedAngle);
      for (int i = 0; i < n; i++) {
         starting = starting.getRotated(origin, Math.toRadians(360.0 / (n)));
         output.add(starting.clone());
      }
      return output;
   }

   public static double convertLength(double displayLengthToRealLength) {
      Pointt focus = new Pointt(0, 0);
      Pointt origin = new Pointt(0, 0);
      focus = focus.focus();
      return new Pointt(0, displayLengthToRealLength).displayToReal(focus).distance(origin);
   }

   public static double arcTan(double y, double x, double xPosition) {
      if (Math.abs(x) < 0.00001) {
         return Math.PI / 2 * toNumber(y >= 0);
      } else {
         double result = Math.atan(y / x);
         if (Double.isNaN(result)) {
            throw new RuntimeException("Arctan return NaN. y = " + y + ", x = " + x + ", xPosition = " + xPosition);
         }
         if (xPosition < 0) {
            return result + Math.PI;
         } else {
            return result;
         }
      }
   }

   /*
    * fix x value to the map to avoid going out of the map
    */
   public static double fixX(double x) {
      if (x <= 0) {
         return 0.01; //to avoid divide by 0 error
      }
      if (x > map.REAL_SIZEX()) {
         return map.REAL_SIZEX();
      }
      return x;
   }

   /*
    * fix y value to the map to avoid going out of the map
    */
   public static double fixY(double y) {
      if (y < 0) {
         return 0.01;// to avoid divide by 0
      }
      if (y > map.REAL_SIZEY()) {
         return map.REAL_SIZEY();
      }
      return y;
   }

   /**
    * Width and height are just 'radii' of the ellipse
    */
   public static Ellipse2D createEllipse(double xCenter, double yCenter, double width, double height) {
      return new Ellipse2D.Double(xCenter - width, yCenter - height, 2 * width, 2 * height);
   }

   public static Area createRectangle(double cenX, double cenY, double width, double height) {
      return new Area(new Rectangle2D.Double(cenX - width, cenY - height, 2 * width, 2 * height));
   }

   private static int toNumber(boolean a) {
      if (a) {
         return 1;
      } else {
         return -1;
      }
   }
}
