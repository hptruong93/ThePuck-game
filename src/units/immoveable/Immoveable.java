package units.immoveable;

import units.Unit;
import utilities.geometry.Point;

public abstract class Immoveable extends Unit {

	public Immoveable(Point position, double health, int side) {
		super(position, side);
	}

}
