package units.moveable;

import units.Unit;
import ai.PathPlanner;

public class Moveable extends Unit {
	protected PathPlanner pathPlanner;
	
	public void move() {
		pathPlanner.move(this);
	}
}
