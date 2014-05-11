package ai;

import java.security.InvalidParameterException;

import units.moveable.Moveable;
import utilities.Util;
import utilities.geometry.Geometry;

/**
 * This class controls all movement of units on the map. If a unit is moveable,
 * it has to have an instance of this class as an attribute.
 * 
 * @author VDa
 * 
 */
public class PathPlanner {
	public static final int NORMAL_MOVE = 0;
	public static final int FORCED_MOVE = 1;
	public static final int NORMAL_NO_COLLISION = 2;
	public static final int FORCED_NO_COLLISION = 3;
	private final Moveable moveable;

	public PathPlanner(Moveable moveable) {
		this.moveable = moveable;
	}

	/**
	 * Move the moveable that the path planner is controlling. If the unit is
	 * not forced to move, path planner will check if the unit can move. If
	 * moving is possible, then path planner will move the unit. Otherwise
	 * nothing is executed. If the unit is forced to move, path planner will
	 * invoke move regardless.
	 * 
	 * @param type
	 *            type of movement, specified using constant declared above
	 * @param moveTime
	 *            the time that the movement occurs in milliseconds
	 */
	public void move(int moveTime, int type) {
		if (type % 2 != 0) {
			if (type == FORCED_NO_COLLISION) {
				moveNoCollision(moveTime);
			} else if (type == FORCED_MOVE) {
				moveWithCollision(moveTime);
			}
			moveNoCollision(moveTime);
		} else if (moveable.state().moveable()) {
			if (type == NORMAL_NO_COLLISION) {
				moveNoCollision(moveTime);
			} else if (type == NORMAL_MOVE) {
				moveWithCollision(moveTime);
			}
		}
	}

	/**
	 * Move the moveable with collision detection. The path planner will 
	 * @param moveTime the time that the movement occurs in milliseconds
	 */
	private void moveWithCollision(int moveTime) {
		moveNoCollision(moveTime);
	}
	
	/**
	 * Move the moveable without considering any collision. First the path
	 * planner will check if the moveable is facing the right direction. If not,
	 * it will turn the unit to the right direction. If there is any time left
	 * for the movement, it will move the unit closer to the destination. If the
	 * unit is already facing the right direction, path planner simply moves the
	 * unit based on the time and speed.
	 * 
	 * If the distance between the unit and its destination is too small, no
	 * movement occurs and unit will be set to be not moving (moving = false)
	 * 
	 * @param moveTime
	 *            the time that the movement occurs in milliseconds
	 * @warning Path planner will NOT check if the unit can move or not. Caller
	 *          should have already checked this
	 */
	private void moveNoCollision(double moveTime) {
		if (moveTime < 0 || moveTime == Double.NaN) {
			throw new InvalidParameterException("Cannot move the unit, time is " + moveTime);
		}

		double destinationAngle = Geometry.fix2Pi(moveable.position().angle(moveable.destination()));
		double moveDistance = moveable.position().distance(moveable.destination());
		moveDistance = Math.min(moveable.speed() * moveTime, moveDistance);
		if (Util.equal(moveDistance, 0)) {
			moveDistance = 0;
			moveable.state().setMoving(false);
		} else {
			moveable.state().setMoving(true);
		}
		
		if (Util.equal(destinationAngle, moveable.movingAngle())) {
			moveable.setPosition(moveable.position().getFrontPoint(destinationAngle, moveDistance));
		} else if (moveDistance != 0) {
			double distance = Geometry.calculateTurnAngle(moveable.movingAngle(), destinationAngle);
			
			if (moveable.turnRate() * moveTime < Math.abs(distance)) {
				moveable.setMovingAngle(moveable.movingAngle() + Math.signum(distance) * moveable.turnRate() * moveTime);
			} else {
				moveable.setMovingAngle(destinationAngle);
				moveNoCollision(moveTime - Math.abs(distance) / moveable.turnRate());
			}
		}
	}
}
