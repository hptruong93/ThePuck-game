package units.moveable.livings;

import utilities.geometry.Point;

public class Illusion extends Living {
	private final Living origin;
	
	public Illusion(Living origin, Point position, double health, double speed, double turnRate) {
		super(position, health, speed, turnRate);
		this.origin = origin;
		// TODO Auto-generated constructor stub
	}
}
