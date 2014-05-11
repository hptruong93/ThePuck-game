package utilities.geometry;

public class Geometry {
	private static final double PI_2 = 2 * Math.PI;

	/**
	 * Ensure that the angle is from 0 to 2 pi
	 * 
	 * @param angle
	 *            an angle in radian
	 * @return the equivalent angle of the input angle, ranged from 0 to 2pi
	 */
	public static double fix2Pi(double angle) {
		if (angle >= PI_2) {
			while (angle >= PI_2) {
				angle -= PI_2;
			}
		} else if (angle <= 0) {
			while (angle <= 0) {
				angle += PI_2;
			}
		}
		return angle;
	}

	/**
	 * Calculate the least angle that unit has to turn to reach a destination angle
	 * 
	 * @param currentTheta
	 *            current angle of the unit. This angle must be from 0 to 2 * pi
	 * @param destTheta
	 *            destination angle that the unit wants to be at
	 * @return the least angle that one has to turn to read destination angle. If the turning 
	 * is to the right, the returned value will be negative.
	 */
	public static double calculateTurnAngle(double currentTheta, double destTheta) {
		double fixedDestTheta = fix2Pi(destTheta);

		double choice = fixedDestTheta - currentTheta;
		choice = fix2Pi(choice);

		if (choice > Math.PI) {// To right
			return -(Math.PI * 2 - choice);
		} else {
			return choice;
		}
	}
}
