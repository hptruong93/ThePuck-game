package units.moveable.livings.boss;

import units.moveable.livings.Living;
import utilities.geometry.Point;
import agent.visualAgent.living.CockroachVisualAgent;

public class Arachnadus extends Boss {

	public Arachnadus(Point position, int side) {
		super(position, Living.INIT_CONFIG.get(Arachnadus.class.getSimpleName()), side);
		this.visualAgent = new CockroachVisualAgent();
	}
	
	public Arachnadus(Point position, InitConfig config, int side) {
		super(position, config, side);
		// TODO Auto-generated constructor stub
	}
}
