package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.visualAgent.living.RyskorVisualAgent;

public class Ryskor extends Boss{

	public Ryskor(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new RyskorVisualAgent();
	}
}
