package curse;

import java.awt.Graphics2D;
import java.util.HashSet;

import units.moveable.livings.Living;
import agent.VisualAgent;

public class CurseContainer {
	private HashSet<Curse> content;
	private VisualAgent rep;

	public void plot(Graphics2D a, Living owner) {
		rep.plot(a, owner);
	}
}
