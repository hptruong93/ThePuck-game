package units;

import java.awt.Graphics2D;

import utilities.geometry.Point;
import agent.SoundAgent;
import agent.VisualAgent;

public abstract class Unit {
	protected Point position;
	protected double movingAngle;
	
	protected SoundAgent soundAgent;
	protected VisualAgent visualAgent;

	public Unit(Point position) {
		this.position = position;
	}
	
	public void plot(Graphics2D a) {
		visualAgent.plot(a, this);
	}
	
	/**********************************************/
	public Point position() {
		return position;
	}
	
	public double movingAngle() {
		return movingAngle;
	}
	
	/**********************************************/
	
	public void setPosition(Point position) {
		this.position = position;
	}
}