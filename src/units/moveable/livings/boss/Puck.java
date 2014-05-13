package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.PuckVisualAgent;

public class Puck extends Boss {

	public Puck(Point position, double health, double speed, double turnRate) {
		super(position, health, speed, turnRate);
		this.visualAgent = new PuckVisualAgent(); 
	}
}
