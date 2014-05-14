package units.moveable.untargetable;

import units.moveable.Moveable;
import utilities.geometry.Point;


public abstract class Untargetable extends Moveable {

	public Untargetable(Point position, double speed, double turnRate, int side) {
		super(position, speed, turnRate, side);
	}
}
