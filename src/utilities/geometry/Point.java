package utilities.geometry;

import main.engineInterface.GameConfig;
import utilities.Util;

/**
 * This class encapsulate information and provide useful utilities to process 2D points.
 * This class is immutable
 * 
 * @author VDa
 */
public class Point {
	private final double x;
	private final double y;

	/**
	 * Constructor
	 * @param x x value of the point on the plane
	 * @param y y value of the point on the plane
	 * @throws IllegalArgumentException if x or y is NaN
	 */
	public Point(double x, double y) {
		if (x == Double.NaN || y == Double.NaN) {
			throw new IllegalArgumentException("NaN Point!!!");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor
	 * @param p java.awt.Point p
	 */
	public Point (java.awt.Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}
	
	/**
	 * 
	 * @param position
	 *            current position coordinate
	 * @param dest
	 *            destination coordinate
	 * @return angle in radian created by the segment starting from the other
	 *         point, ending at this point, and the x-axis
	 */
	public double angle(Point other) {
		if (Util.equal(other.x, x)) {
			if (other.y > y) {
				return Math.PI / 2;
			} else {
				return -Math.PI / 2;
			}
		}

		double m = (other.y - y) / (other.x - x);
		if (other.x > x)
			return Math.atan(m);
		return Math.atan(m) + Math.PI;
	}

	/**
	 * 
	 * @param other
	 *            other point
	 * @return line in form ax + by + c = 0 that passes through the two points
	 */
	public Line lineThrough(Point other) {
		double dx = this.x - other.x;
		if (dx == 0) {
			return new Line( 1, 0, x );
		} else {
			double dy = this.y - other.y;
			return new Line( dy, -dx, dx * y - dy * x );
		}
	}

	/**
	 * 
	 * @param line
	 *            line information in form ax + by + c = 0
	 * @return distance from the point to the line
	 */
	public double distanceToLine(Line line) {
		return Math.abs(line.getA() * x + line.getB() * y + line.getC()) / Math.sqrt(Math.pow(line.getA(), 2) + Math.pow(line.getB(), 2));
	}

	/**
	 * 
	 * @param other
	 *            other point
	 * @return distance from this point to the other point
	 */
	public double distance(Point other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
	}

	/**
	 * Calculate the mid point of the segment created by this point and other
	 * @param other the other end of the segment. This point is one end
	 * @return Mid point of the segment
	 */
	public Point midPoint(Point other) {
		return new Point(0.5 * (x + other.x), 0.5 * (y + other.y));
	}
	
	/**
    *
    * @param The other point
    * @return the point trisects the segment and is closer to this pointt (further away from pointt
    * p)
    */
   public Point trisectionPoint(Point p) {
      return new Point((2 * x) / 3 + p.x / 3, (2 * y) / 3 + p.y / 3);
   }
	
	/**
	 * Get the intersection between a line and a circle
	 * @param line line information in the form ax + by + c = 0
	 * @param centre centre of the circle
	 * @param radius radius of the circle
	 * @return array of intersections between line and the circle
	 */
	public static Point[] intersectionLineCircle(Line line, Circle circle) {
		/**
		 * H is the projection of centre on line
		 */
		double lineAngle = line.getAngle();
		Point H = line.intersection(circle.getCentre().lineThrough(lineAngle + Math.PI/2));
		double OH = H.distance(circle.getCentre());
		if (Util.equal(OH, circle.getRadius())) {//One intersection - tangent line
			return new Point[] {H};
		} else if (OH > circle.getRadius()) {//No intersection
			return new Point[] {};
		} else {//Two intersection
			/**
			 * Two points of intersection are A and B (order does not matter)
			 */
			double AH = Math.sqrt(circle.getRadius() * circle.getRadius() - OH * OH);
			return new Point[] {H.getFrontPoint(lineAngle, AH), H.getFrontPoint(lineAngle, -AH)}; 
		}
	}
	
	
	/**
	 * Check if this point belongs to the line segment created by joining start and end 
	 * @param start start of the segment
	 * @param end end point of the segment
	 * @return if this point belongs to the segment
	 */
	public boolean belongsToSegment(Point start, Point end) {
		return Util.equal(this.distance(start) + this.distance(end), start.distance(end)); 
	}
	
	/**
	 * Get a point that is mirror immage of this point through the line
	 * The process is to take the projection of the point on the line,
	 * then rotate the point using the projection as the pivot by 180 degree
	 * @param mirror the line that will be the mirror
	 * @return the mirrored point
	 */
	public Point getMirrored(Line mirror) {
		double perpendicularAngle = mirror.getAngle() + Math.PI/2;
		Line perpendicularLine = this.lineThrough(perpendicularAngle);
		
		Point projection = mirror.intersection(perpendicularLine);
		return this.getRotated(projection, Math.PI);
	}
	
	/**
	 * Create a line passing through this point, having an angle of input
	 * @param angle angle of the line
	 * @return line in form ax + by + c = 0
	 */
	public Line lineThrough(double angle) {
		Point temp = this.getFrontPoint(angle, 1);
		return this.lineThrough(temp);
	}
	
	/**
	 * 
	 * @param angle
	 *            the angle at which this point is facing
	 * @param distance
	 *            the distance in front
	 * @return a point that is "distance" away from this point, facing in the
	 *         "angle" direction
	 */
	public Point getFrontPoint(double angle, double distance) {
		return new Point(x + distance * Math.cos(angle), y + distance * Math.sin(angle));
	}

	/**
	 * Rotate this point around a pivot
	 * @param pivot the pivot of rotation
	 * @Param angle the angle in radian that the point will be rotated. Counterclockwise is positive.
	 * @return a point resulted in the rotation of this point
	 */
	public Point getRotated(Point pivot, double angle) {
		double currentAngle = pivot.angle(this);
		currentAngle += angle;
		return pivot.getFrontPoint(currentAngle, pivot.distance(this));
	}
	
	/**
	 * Create a point on a real map from a point in the minimap. "this" point will be
	 * considered as a point on minimap for conversion.
	 * @return a point representing this point in the real map. 
	 */
	public Point miniToReal() {
		return new Point(x * GameConfig.SCALE_X, y * GameConfig.SCALE_Y);
	}
	
	/**
	 * Create a point on a mini map from a point in the real map. "this" point will be
	 * considered as a point on real map for conversion.
	 * @return a point representing this point in the minimap. 
	 */
	public Point realToMini() {
		return new Point(x / GameConfig.SCALE_X, y / GameConfig.SCALE_Y);
	}
	
	/**
	 * Create a point on a mini map from a point on the display. "this" point will be
	 * considered as a point on display for conversion.
	 * @param focus the current focus on the minimap
	 * @return a point representing this point in the minimap. 
	 */
	public Point displayToMini(Point focus) {
		Point output = new Point(x / GameConfig.SCALE_MINI_X + focus.x - GameConfig.FOCUS_WIDTH/2, 
                y / GameConfig.SCALE_MINI_Y + focus.y - GameConfig.FOCUS_HEIGHT/2);
		return output;
	}
	
	/**
	 * Create a point on display from a point on the minimap. "this" point will be
	 * considered as a point on minimap for conversion.
	 * @param focus the current focus on the minimap
	 * @return a point representing this point in the minimap. 
	 */
	public Point miniToDisplay(Point focus) {
		Point output = new Point((x - focus.x + GameConfig.FOCUS_WIDTH/2) * GameConfig.SCALE_MINI_X,
				                 (y - focus.y + GameConfig.FOCUS_HEIGHT/2) * GameConfig.SCALE_MINI_Y);
		return output;
	}
	
	/**
	 * Create a point on display from a point on the real map. "this" point will be
	 * considered as a point on real map for conversion.
	 * @param focus the current focus on the minimap
	 * @return a point representing this point on the display. 
	 */
	public Point realToDisplay(Point focus) {
		return realToMini().miniToDisplay(focus);
	}
	
	/**
	 * Create a point on the real map from a point on display. "this" point will be
	 * considered as a point on display for conversion.
	 * @param focus the current focus on the minimap
	 * @return a point representing this point on the real map. 
	 */
	public Point displayToReal(Point focus) {
		return displayToMini(focus).miniToReal();
	}
	
	/**
	 * Check if two points are virtually the same on the plane
	 * This is effectively the equals method, but overriding equals would mean
	 * overriding hashCode, and computing hashCode for a Point is rather more
	 * complicated.
	 * @param other other point that will be compared to this one
	 * @return if both x and y of other point are very close to this point
	 * @see Util.equal for the close definition
	 */
	public boolean theSame(Point other) {
		return Util.equal(x, other.x) && Util.equal(y, other.y);
	}

	/**
	 * Provide a deep copy of a point.
	 * @return a deep copy of the current point
	 */
	@Override
	public Point clone() {
		return new Point(x, y);
	}

	/**
	 * @return x value of the point
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return y value of the point
	 */
	public double getY() {
		return y;
	}

	/**
	 * Utility method to provide a String representation of the current point.
	 * @return a String representation of the current point
	 */
	@Override
	public String toString() {
		return "x = " + x + ", y = " + y;
	}
}
