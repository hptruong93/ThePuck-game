package units.moveable.livings.boss;

import utilities.geometry.Point;
import agent.visualAgent.living.PuckVisualAgent;

public class Puck extends Boss {

	public Puck(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new PuckVisualAgent(); 
	}
}
