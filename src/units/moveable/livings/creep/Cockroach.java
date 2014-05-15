package units.moveable.livings.creep;

import utilities.geometry.Point;
import agent.CockroachVisualAgent;


public class Cockroach extends Creep {

	public Cockroach(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new CockroachVisualAgent();
		// TODO Auto-generated constructor stub
	}
}
