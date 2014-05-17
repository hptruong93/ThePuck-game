package units.moveable.untargetable.passiveInteractive.projectile;

import units.Unit;

public final class ProjectileGenerator {
	
	private Projectile sample;
	
	public ProjectileGenerator(Projectile sample) {
		this.sample = sample;
	}
	
	public Projectile generate(Unit target) {
		return sample.clone(target);
	}
	
	public void changeSample(Projectile sample) {
		this.sample = sample;
	}
}
