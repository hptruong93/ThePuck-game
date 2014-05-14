package units;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import utilities.geometry.Point;
import agent.SoundAgent;
import agent.VisualAgent;

public abstract class Unit {
	private static final double DEFAULT_RADIUS = 10;
	
	protected Point position;
	protected double movingAngle;
	
	protected SoundAgent soundAgent;
	protected VisualAgent visualAgent;

	protected int side;
	
	public Unit(Point position, int side) {
		this.position = position;
	}
	
	public void plot(Graphics2D a, AffineTransform defaultTransform) {
		visualAgent.plot(a, defaultTransform, this);
	}
	
	public boolean collide(Unit other) {
		return position.distance(other.position) < (radius() + other.radius());
	}
	
	/**********************************************/
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
	
	public Point position() {
		return position;
	}
	
	public double movingAngle() {
		return movingAngle;
	}
	
	public double side() {
		return side;
	}
	
	/**********************************************/
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public void setSide(int side) {
		this.side = side;
	}
}