package units.moveable.targetable.livings.boss;

import agent.ArchonVisualAgent;

public class Archon extends Boss {
	public Archon() {
		super();
		visualAgent = new ArchonVisualAgent();
	}
}
