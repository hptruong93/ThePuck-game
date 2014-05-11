package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.ArchonVisualAgent;


public class Archon extends Boss {

	public Archon(Point position, double health, double speed, double turnRate) {
		super(position, health, speed, turnRate);
		this.visualAgent = new ArchonVisualAgent();
		// TODO Auto-generated constructor stub
	}
}
