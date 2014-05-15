package units.moveable.untargetable.passiveInteractive.projectile;

import units.moveable.livings.Living;
import utilities.geometry.Point;

public class ChasingProjectile extends Projectile {

	protected Living target;
	
	public ChasingProjectile(Living owner, Living target, Point position, double damage, InitConfig config, int side) {
		super(owner, position, damage, config, side);
		this.target = target;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Projectile clone() {
		// TODO Auto-generated method stub
		return null;
	}
}
