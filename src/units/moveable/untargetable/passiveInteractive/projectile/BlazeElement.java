package units.moveable.untargetable.passiveInteractive.projectile;

import units.moveable.livings.Living;
import utilities.geometry.Point;

public class BlazeElement extends Projectile {

	public BlazeElement(Living owner, Point position, double damage, InitConfig config, int side) {
		super(owner, position, damage, config, side);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Projectile clone() {
		// TODO Auto-generated method stub
		return null;
	}
}
