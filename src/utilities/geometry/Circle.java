package utilities.geometry;

/**
 * The class describes a circle on 2D plane.
 * This class is immutable
 * @author VDa
 *
 */
public class Circle {
	
	private final Point centre;
	private final double radius;
	
	/**
	 * Constructor for the circle
	 * @param centre centre of the circle. Must not be null
	 * @param radius radius of the circle. Must be greater than 0
	 */
	public Circle(Point centre, double radius) {
		if (radius == Double.NaN) {
			throw new IllegalArgumentException("NaN Circle!!!");
		}
		
		if (radius <= 0) {
			throw new IllegalArgumentException("Radius is non-positive");
		}
		
		if (centre == null) {
			throw new IllegalArgumentException("Null centre");
		}
		
		this.centre = centre;
		this.radius = radius;
	}
	
	/**
	 * 
	 * @return centre of the circle
	 */
	public Point getCentre() {
		return centre;
	}
	
	/**
	 * 
	 * @return radius of the circle
	 */
	public double getRadius() {
		return radius;
	}
}
