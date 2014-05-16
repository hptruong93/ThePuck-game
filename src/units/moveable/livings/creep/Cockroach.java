package units.moveable.livings.creep;

import units.moveable.livings.Living;
import utilities.geometry.Point;
import agent.visualAgent.living.CockroachVisualAgent;


public class Cockroach extends Creep {

	public Cockroach(Point position, int side) {
		super(position, Living.INIT_CONFIG.get(Cockroach.class.getSimpleName()), side);
		this.visualAgent = new CockroachVisualAgent();
	}
	
	public Cockroach(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new CockroachVisualAgent();
	}
}
