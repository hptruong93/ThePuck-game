package utilities.geometry;

import utilities.Util;

/**
 * 2D line representation. Line is represented using equation ax + by + c = 0
 * This class is immutable
 * @author VDa
 *
 */
public class Line {
	private final double a, b, c;
	
	/**
	 * Constructor of the line in the form ax + by + c = 0
	 * @param a value a
	 * @param b value b
	 * @param c value c
	 */
	public Line(double a, double b, double c) {
		if (a == Double.NaN || b == Double.NaN || c == Double.NaN) {
			throw new IllegalArgumentException("NaN Line!!!");
		}
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	/**
	 * 
	 * @return the angle of the line
	 */
	public double getAngle() {
		if (Util.equal(this.b, 0)) {
			return Math.PI/2;
		} else {
			return Math.atan(-this.a/this.b);
		}
	}
	
	/**
	 * Determine intersection between two lines in form ax + by + c = 0
	 * @param line1 information about line 1 in form ax + by + c = 0
	 * @return intersection between two lines. Null if there is no intersection or infinite intersections
	 */
	public Point intersection(Line line1) {
		double a1 = line1.a, a2 = this.a, b1 = line1.b, b2 = this.b, c1 = -line1.c, c2 = -this.c;
		double det = a1 * b2 - a2 * b1; 
		if (Util.equal(det, 0)) {
			return null;
		} else {
			return new Point((b2 * c1 - b1 * c2)/det, (a1 * c2 - a2 * c1)/det);
		}
	}
	
	/**
	 * Check if this line and another line is identical.
	 * Two lines may have different representation ax + by + c = 0, but
	 * when simplified, they are the same
	 * @param other other line
	 * @return if these two lines are identical
	 */
	public boolean theSame(Line other) {
		double a1 = this.a, b1 = this.b, c1 = this.c, a2 = other.a, b2 = other.b, c2 = other.c;
		double reductionFactor1 = 1, reductionFactor2 = 1;
		if (a1 != 0) {
			reductionFactor1 = a1;
			if (a2 == 0) {
				return false;
			} else {
				reductionFactor2 = a2;
			}
		} else if (b1 != 0) {
			reductionFactor1 = b1;
			
			if (b2 == 0) {
				return false;
			} else {
				reductionFactor2 = b2;
			}
		} else if (c1 != 0) {
			reductionFactor1 = c1;
			
			if (c2 == 0) {
				return false;
			} else {
				reductionFactor2 = c2;
			}
		} else {
			return new Line(0,0,0).strictlyEqual(other);
		}
		
		a1 /= reductionFactor1;
		b1 /= reductionFactor1;
		c1 /= reductionFactor1;
		
		a2 /= reductionFactor2;
		b2 /= reductionFactor2;
		c2 /= reductionFactor2;
		
		return new Line(a1, b1, c1).strictlyEqual(new Line(a2, b2, c2));
	}
	
	/**
	 * Compare two lines in form ax + by + c = 0 and check if their a, b and c are identical
	 * @param other other line that will be compared to this line
	 * @return if a, b and c parameters are identical
	 */
	private boolean strictlyEqual(Line other) {
		return (Util.equal(a, other.a) && Util.equal(b, other.b) && Util.equal(c, other.c));
	}
	
	/**
	 * @return a value in expression ax + by + c = 0
	 */
	public double getA() {
		return a;
	}
	
	/**
	 * @return b value in expression ax + by + c = 0
	 */
	public double getB() {
		return b;
	}
	
	/**
	 * @return c value in expression ax + by + c = 0
	 */
	public double getC() {
		return c;
	}
}
