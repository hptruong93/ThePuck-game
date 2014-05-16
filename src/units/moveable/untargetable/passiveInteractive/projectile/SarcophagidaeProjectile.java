package units.moveable.untargetable.passiveInteractive.projectile;

import units.moveable.livings.Living;
import utilities.geometry.Point;
import agent.visualAgent.projectile.SarcophagidaeProjva;

public class SarcophagidaeProjectile extends ChasingProjectile {

	public SarcophagidaeProjectile(Living owner, Living target, Point position, double damage, int side) {
		super(owner, target, position, damage, Projectile.INIT_CONFIG.get(SarcophagidaeProjectile.class.getSimpleName()), side);
		// TODO Auto-generated constructor stub
		this.visualAgent = new SarcophagidaeProjva();
	}
}
