package curse;

import java.awt.Graphics2D;
import java.util.HashSet;

import agent.VisualAgent;

public class CurseContainer {
	private HashSet<Curse> content;
	private VisualAgent rep;

	public void plot(Graphics2D a) {
		rep.plot(a);
	}
}
