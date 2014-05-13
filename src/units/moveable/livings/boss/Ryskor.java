package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.RyskorVisualAgent;

public class Ryskor extends Boss{

	public Ryskor(Point position, double health, double speed, double turnRate) {
		super(position, health, speed, turnRate);
		this.visualAgent = new RyskorVisualAgent();
	}
}
