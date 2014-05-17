package units.moveable.untargetable.passiveInteractive.projectile;

import units.Unit;
import units.moveable.livings.Living;
import utilities.geometry.Point;
import agent.visualAgent.projectile.SarcophagidaeProjva;

public class SarcophagidaeProjectile extends ChasingProjectile {

	public SarcophagidaeProjectile(Living owner, Unit target, Point position, double damage, int side) {
		super(owner, target, position, damage, Projectile.INIT_CONFIG.get(SarcophagidaeProjectile.class.getSimpleName()), side);
		this.visualAgent = new SarcophagidaeProjva();
	}

	@Override
	protected Projectile clone(Unit target) {
		return new SarcophagidaeProjectile(owner, target, owner.position(), damage, owner.side());
	}
}
