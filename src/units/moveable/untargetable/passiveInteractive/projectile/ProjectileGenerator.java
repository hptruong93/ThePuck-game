package units.moveable.untargetable.passiveInteractive.projectile;

public final class ProjectileGenerator {
	
	private Projectile sample;
	
	public ProjectileGenerator(Projectile sample) {
		this.sample = sample;
	}
	
	public Projectile generate() {
		return sample.clone();
	}
	
	public void changeSample(Projectile sample) {
		this.sample = sample;
	}
}
