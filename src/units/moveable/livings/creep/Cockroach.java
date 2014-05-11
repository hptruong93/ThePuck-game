package units.moveable.livings.creep;

import utilities.geometry.Point;
import agent.CockroachVisualAgent;


public class Cockroach extends Creep {

	public Cockroach(Point position, double health, double speed, double turnRate) {
		super(position, health, speed, turnRate);
		this.visualAgent = new CockroachVisualAgent();
		// TODO Auto-generated constructor stub
	}

}
