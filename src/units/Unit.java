package units;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import utilities.geometry.Point;
import agent.SoundAgent;
import agent.visualAgent.VisualAgent;

public abstract class Unit {
	private static final double DEFAULT_RADIUS = 15;
	
	protected Point position;
	protected double movingAngle;
	
	protected SoundAgent soundAgent;
	protected VisualAgent visualAgent;

	protected int side;
	
	public Unit(Point position, int side) {
		this.position = position;
		this.side = side;
	}
	
	/**
	 * Plot the unit using its visual agent.
	 * @param a the graphic that is used for plotting
	 * @param defaultTransform default transformation of the graphic. Used to reset
	 * the graphic to the 'blank' state.
	 */
	public void plot(Graphics2D a, AffineTransform defaultTransform) {
		visualAgent.plot(a, defaultTransform, this);
	}
	
	/**
	 * Check if the unit is colliding with another unit. To detect collision, consider two units as two
	 * circle with radius defined by method radius().
	 * Special unit(s) may have different method of checking for collision
	 * @param other the other unit that will be checked for collision with this unit
	 * @return if the two units collide
	 */
	public boolean collide(Unit other) {
		return position.distance(other.position) < (radius() + other.radius());
	}
	
	/************************************************************************************/
	/**
	 * For collision detection, every unit is represented by a circle. This method
	 * returns the radius of that circle. Use this only for collision detection.
	 * Children classes can override this method to define their own radius.
	 * @return the radius of the circle representing the unit in the collision detection
	 * context.
	 */
	public double radius() {
		return DEFAULT_RADIUS;
	}
	
	public double distance(Unit other) {
		return position.distance(other.position);
	}
	
	public double angle(Unit other) {
		return position.angle(other.position);
	}
	
	/**********************Getters*******************************************************/
	public Point position() {
		return position;
	}
	
	public double movingAngle() {
		return movingAngle;
	}
	
	public int side() {
		return side;
	}
	
	/**********************Setters*******************************************************/
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public void setSide(int side) {
		this.side = side;
	}
}