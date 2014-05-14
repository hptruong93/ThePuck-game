package curse;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;

import units.moveable.livings.Living;
import agent.VisualAgent;

public class CurseContainer {
	private HashSet<Curse> content;
	private VisualAgent rep;

	public void plot(Graphics2D a, AffineTransform defaultTransform, Living owner) {
		rep.plot(a, defaultTransform, owner);
	}
}
