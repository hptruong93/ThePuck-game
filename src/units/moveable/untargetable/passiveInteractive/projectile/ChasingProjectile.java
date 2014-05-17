package units.moveable.untargetable.passiveInteractive.projectile;

import units.Unit;
import units.moveable.livings.Living;
import utilities.geometry.Point;

public abstract class ChasingProjectile extends Projectile {

	protected Unit target;
	
	public ChasingProjectile(Living owner, Unit target, Point position, double damage, InitConfig config, int side) {
		super(owner, position, position, damage, config, side);
		this.target = target;
		this.setDestination(target.position());
	}

	@Override
	public void move(int moveTime, int type) {
		this.setDestination(target.position());
		super.move(moveTime, type);
	}
	
	@Override
	protected abstract Projectile clone(Unit target);
}
