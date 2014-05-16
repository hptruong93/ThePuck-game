package units.moveable.livings.boss;

import units.moveable.livings.Living;
import utilities.geometry.Point;
import agent.visualAgent.living.CockroachVisualAgent;
import agent.visualAgent.living.SarcophagidaeVisualAgent;

public class Sarcophagidae extends Boss {

	public Sarcophagidae(Point position, int side) {
		super(position, Living.INIT_CONFIG.get(Sarcophagidae.class.getSimpleName()), side);
		this.visualAgent = new CockroachVisualAgent();
	}
	
	public Sarcophagidae(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new SarcophagidaeVisualAgent();
	}
}
