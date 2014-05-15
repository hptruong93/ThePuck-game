package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.SarcophagidaeVisualAgent;

public class Sarcophagidae extends Boss {

	public Sarcophagidae(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new SarcophagidaeVisualAgent();
	}
}
