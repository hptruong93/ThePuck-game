package units.moveable.livings;

import units.moveable.Moveable;
import utilities.geometry.Point;

public class Living extends Moveable {
	private double health;
	
	public Living(Point position, double health, double speed, double turnRate) {
		super(position, speed, turnRate);
	}
}
