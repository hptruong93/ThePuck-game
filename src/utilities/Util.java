package utilities;

public class Util {
	
	/**
	 * Check if two doubles are equal. Since double can hardly be equal,
	 * if they are close to each other (see threshold in implementation) then
	 * we consider them to be equal
	 * @param a first double
	 * @param b second double
	 * @return if two doubles are equal.
	 */
	public static boolean equal(double a, double b) {
		return Math.abs(a - b) < 0.000001;
	}
	
	/**
    *
    * @param range
    * @return random number from 0 to range
    */
   public static double random(double range) {
      return range * Math.random();
   }

   /**
    * 
    * @param range
    * @return random number from -range to range;
    */
   public static double randomNegative(double range) {
      return 2 * range * Math.random() - range;
   }

   /**
    * 
    * @return a random angle from 0 to 2 * Math.PI
    */
   public static double randomAngle() {
      return Math.random() * Math.PI * 2;
   }
}