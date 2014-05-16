package units.moveable.livings.boss;

import units.moveable.livings.Living;
import utilities.geometry.Point;
import agent.visualAgent.living.ArchonVisualAgent;
import agent.visualAgent.living.CockroachVisualAgent;


public class Archon extends Boss {

	public Archon(Point position, int side) {
		super(position, Living.INIT_CONFIG.get(Archon.class.getSimpleName()), side);
		this.visualAgent = new CockroachVisualAgent();
	}
	
	public Archon(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new ArchonVisualAgent();
		// TODO Auto-generated constructor stub
	}
}
