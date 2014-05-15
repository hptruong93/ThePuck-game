package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.ArchonVisualAgent;


public class Archon extends Boss {

	public Archon(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new ArchonVisualAgent();
		// TODO Auto-generated constructor stub
	}
}
