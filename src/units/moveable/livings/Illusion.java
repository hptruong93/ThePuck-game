package units.moveable.livings;

import utilities.geometry.Point;

public class Illusion extends Living {
	private final Living origin;
	
	public Illusion(Living origin, Point position, InitConfig initConfig, int side) {
		super(position, initConfig, side);
		this.origin = origin;
		// TODO Auto-generated constructor stub
	}
}
