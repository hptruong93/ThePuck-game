package units.moveable.livings.boss;

import units.moveable.livings.Living;
import units.moveable.untargetable.passiveInteractive.projectile.ProjectileGenerator;
import units.moveable.untargetable.passiveInteractive.projectile.SarcophagidaeProjectile;
import utilities.geometry.Point;
import agent.visualAgent.living.CockroachVisualAgent;
import agent.visualAgent.living.SarcophagidaeVisualAgent;

public class Sarcophagidae extends Boss {

	public Sarcophagidae(Point position, int side) {
		super(position, Living.INIT_CONFIG.get(Sarcophagidae.class.getSimpleName()), side);
		this.visualAgent = new CockroachVisualAgent();
		this.projectileFactory = new ProjectileGenerator(new SarcophagidaeProjectile(this, this, position, attackManager.damage(), side));
	}
	
	public Sarcophagidae(Point position, InitConfig config, int side) {
		super(position, config, side);
		this.visualAgent = new SarcophagidaeVisualAgent();
		this.projectileFactory = new ProjectileGenerator(new SarcophagidaeProjectile(this, this, position, attackManager.damage(), side));
	}
}
