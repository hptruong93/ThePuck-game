package Utilities;

import java.util.Random;

public class Maths {

   public static final Random RANDOM = new Random(System.currentTimeMillis());

   /**
    * Find equation of parabola that passes through (x1,y1), (x2,y2) and (x3,y3) return [a,b,c] in
    * equation y = ax^2 + bx + c.
    */
   public static double[] findParabola(double x1, double y1,
           double x2, double y2,
           double x3, double y3) {
      return solveSystem(x1 * x1, x1, 1, y1,
              x2 * x2, x2, 1, y2,
              x3 * x3, x3, 1, y3);
   }

   /**
    * Solve 3 x 3 system [x,y,z] in form of ax + by + cz = d
    */
   private static double[] solveSystem(double a1, double b1, double c1, double d1,
           double a2, double b2, double c2, double d2,
           double a3, double b3, double c3, double d3) {
      double[] result = new double[3];

      double D = determinant(a1, b1, c1, a2, b2, c2, a3, b3, c3);
      double Dx = determinant(d1, b1, c1, d2, b2, c2, d3, b3, c3);
      double Dy = determinant(a1, d1, c1, a2, d2, c2, a3, d3, c3);
      double Dz = determinant(a1, b1, d1, a2, b2, d2, a3, b3, d3);

      result[0] = Dx / D;
      result[1] = Dy / D;
      result[2] = Dz / D;

      return result;
   }

   private static double determinant(double a, double b, double c,
           double d, double e, double f,
           double g, double h, double i) {
      return a * determinant(e, f, h, i) - b * determinant(d, f, g, i) + c * determinant(d, e, g, h);
   }

   private static double determinant(double a, double b, double c, double d) {
      return a * c - b * d;
   }

   /**
    *
    * @param range
    * @return random number from 0 to range
    */
   public static double random(double range) {
      return range * Math.random();
   }

   public static double randomNegative(double range) {
      return range * Math.random() - range/2;
   }

   public static double randomAngle() {
      return Math.random() * Math.PI * 2;
   }
}
