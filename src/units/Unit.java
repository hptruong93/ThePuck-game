package units;

import java.awt.Graphics2D;

import utilities.geometry.Point;
import agent.SoundAgent;
import agent.VisualAgent;

public abstract class Unit {
	protected Point position;
	protected SoundAgent soundAgent;
	protected VisualAgent visualAgent;
	
	public void plot(Graphics2D a) {
		visualAgent.plot(a);
	}
}