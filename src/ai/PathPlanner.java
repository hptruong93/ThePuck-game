package ai;

import java.security.InvalidParameterException;

import main.engineInterface.GameConfig;
import main.engineInterface.ResourceFinder;
import units.Unit;
import units.moveable.Moveable;
import utilities.Util;
import utilities.geometry.Geometry;
import utilities.geometry.Point;

/**
 * This class controls all movement of units on the map. If a unit is moveable,
 * it has to have an instance of this class as an attribute.
 * </br>
 * The unit mentioned in context of this class refers to the moveable that this
 * Path planner is controlling.
 * @author VDa
 * 
 */
public class PathPlanner {
	public static final int NORMAL_MOVE = 0;
	public static final int FORCED_MOVE = 1;
	public static final int NORMAL_NO_COLLISION = 2;
	public static final int FORCED_NO_COLLISION = 3;
	
	private static final double PUSH_BACK_SPEED = 0.015;
	
	private final Moveable moveable;
	private Unit previousTouch;
	private double offset;

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
	 * Move the moveable with collision detection. If the movement without collision
	 * brings the unit to collision with another unit, the path planner will
	 * attempt to move perpendicular to the collided unit.
	 * </br>
	 * This method also resolves situation where the current unit is already collided
	 * with another unit before it attempts to move. The planner simply pushes the two
	 * further away. 
	 * 
	 * @param moveTime
	 *            the time that the movement occurs in milliseconds
	 */
	private void moveWithCollision(int moveTime) {
		if (moveable.state().isTransparent()) {
			moveNoCollision(moveTime);
			return;
		}
		
		Point initialPosition = moveable.position().clone();

		Unit collide = collide();
		if (collide != null) {
			MovementState current = saveMovement();
			double newAngle = initialPosition.angle(collide.position()) + Math.PI;
			if (newAngle == Math.PI - Math.PI / 2) {
				newAngle += Util.randomNegative(Math.PI/4);
			}

			moveable.setSpeed(PUSH_BACK_SPEED);
			moveable.setDestination(initialPosition.getFrontPoint(newAngle, moveTime * PUSH_BACK_SPEED));
			moveNoCollision(moveTime);
			loadMovement(current);
			moveable.setDestination(moveable.position().clone());
		} else {
			moveNoCollision(moveTime);

			collide = collide();
			if (collide != null) {// If there is anything on the way. Try to
									// move perpendicular to it
				if (collide != previousTouch) {// Choose a random direction
					offset = Math.random() < 4 ? Math.PI / 2 : -Math.PI / 2;
				}

				Point currentDestination = moveable.destination().clone();
				double destinationAngle = initialPosition.angle(collide.position());
				Point tempDestination = initialPosition.getFrontPoint(destinationAngle + offset, moveTime * moveable.speed());

				moveable.setMovingAngle(destinationAngle);
				moveable.setPosition(initialPosition.clone());
				moveable.setDestination(tempDestination);

				moveNoCollision(moveTime);

				moveable.setDestination(currentDestination);
				previousTouch = collide;
			} else {
				offset = 0;
				previousTouch = null;
			}
		}
	}

	/**
	 * Get a unit that is colliding with this unit.
	 * @return A unit that is colliding with this unit
	 */
	private Unit collide() {
		ResourceFinder collisionFinder = new ResourceFinder() {
			@Override
			protected boolean findingTest(Unit unit) {
				if (unit != moveable && moveable.collide(unit)) {
					return true;
				} else {
					return false;
				}
			}
		};

		for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
			Unit found = collisionFinder.findAll(i);
			if (found != null) {
				return found;
			}
		}
		return null;
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

	/**
	 * Load a movement state back on the unit
	 * @param state the state that will be loaded
	 * @see MovementState class
	 */
	private void loadMovement(MovementState state) {
		moveable.setMovingAngle(state.movingAngle);
		moveable.setDestination(state.destination);
		moveable.setSpeed(state.speed);
	}

	/**
	 * Save a movement state of the unit
	 * @return a saved instance of the movement state
	 */
	private MovementState saveMovement() {
		return new MovementState(moveable.speed(), moveable.movingAngle(), moveable.destination().clone());
	}

	/**
	 * Movement state of the unit.
	 * This includes 
	 * 1) The destination of the unit
	 * 2) The angle at which the unit is facing
	 * 3) The speed at which the unit is moving
	 * </br>
	 * The position of the unit is irrelevant to the context. Therefore it would not
	 * be included.
	 * 
	 * @author VDa
	 *
	 */
	private class MovementState {
		private final double movingAngle;
		private final Point destination;
		private final double speed;

		private MovementState(double speed, double movingAngle, Point destination) {
			this.speed = speed;
			this.movingAngle = movingAngle;
			this.destination = destination;
		}
	}
}
