package units.moveable.untargetable.passiveInteractive.projectile;

import units.Unit;
import units.moveable.livings.Living;
import utilities.geometry.Point;

public class BlazeElement extends Projectile {

	public BlazeElement(Living owner, Point position, Point target, double damage, InitConfig config, int side) {
		super(owner, position, target, damage, config, side);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Projectile clone(Unit target) {
		// TODO Auto-generated method stub
		return null;
	}
}
