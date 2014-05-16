package units.moveable;

import java.util.HashSet;

import units.State;
import units.Unit;
import utilities.geometry.Geometry;
import utilities.geometry.Point;
import ai.PathPlanner;
import curse.CurseContainer;

public abstract class Moveable extends Unit {
	protected State state;
	protected HashSet<CurseContainer> curses;
	protected PathPlanner pathPlanner;
	
	protected Point destination;
	protected double speed;
	protected double turnRate;
	
	public Moveable(Point position, double speed, double turnRate, int side) {
		super(position, side);
		this.speed = speed;
		this.turnRate = turnRate;
		this.destination = position.clone();
		
		this.state = new State();
		this.pathPlanner = new PathPlanner(this);
	}
	
	/*******************************************************/
	/**
	 * Move the unit using the path planner
	 * @param moveTime the time that the movement occurs (in milliseconds)
	 * @param type movement type. See PathPlanner class for the constants
	 */
	public void move(int moveTime, int type) {
		pathPlanner.move(moveTime, type);
	}
	
	/*******************Getters*****************************/
	
	public Point destination() {
		return destination;
	}
	
	public double speed() {
		return speed;
	}
	
	public double turnRate() {
		return turnRate;
	}
	
	public State state() {
		return state;
	}
	
	/*******************Setters*****************************/
	
	public void setDestination(Point destination) {
		this.destination = destination;
	}
	
	public void setMovingAngle(double movingAngle) {
		this.movingAngle = Geometry.fix2Pi(movingAngle);
	}
	
	public void setSpeed(double speed) { 
		this.speed = speed;
	}
	
	public void setTurnRate(double rate) {
		this.turnRate = rate;
	}
}
