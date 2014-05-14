package units.moveable.untargetable.visualEffect;

import units.moveable.untargetable.Untargetable;
import utilities.geometry.Point;

public abstract class VisualEffect extends Untargetable {

	public VisualEffect(Point position, double speed, double turnRate) {
		super(position, speed, turnRate, 0); //Side does not really matter for visual effect
		// TODO Auto-generated constructor stub
	}

}
