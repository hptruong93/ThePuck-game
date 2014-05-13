package units.moveable.livings.boss;

import agent.SarcophagidaeVisualAgent;
import utilities.geometry.Point;

public class Sarcophagidae extends Boss {

	public Sarcophagidae(Point position, double health, double speed, double turnRate) {
		super(position, health, speed, turnRate);
		this.visualAgent = new SarcophagidaeVisualAgent();
	}
}
