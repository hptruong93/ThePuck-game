package units.moveable.livings;

import main.engineInterface.GameMaster;
import units.Unit;
import units.moveable.untargetable.passiveInteractive.projectile.Projectile;
import features.Clock;

public class AttackManager {
	private final Living owner;
	private long lastAttack;
	private double damage;
	private double rate;
	private double range;

	public AttackManager(Living owner, double damage, double rate, double range) {
		this.lastAttack = 0;
		this.owner = owner;
		this.damage = damage;
		this.rate = rate;
		this.range = range;
	}

	public void attack(Unit target) {
		if (target == null) {
			return;
		}
		
		if (attackable()) {
			Projectile created = owner.projectileFactory.generate(target);
			GameMaster.addProjectile(created);
			created.start();
			lastAttack = Clock.MASTER_CLOCK.currentTime();
		}
	}

	private boolean attackable() {
		return (Clock.MASTER_CLOCK.currentTime() - lastAttack) > rate * 1000;
	}

	public double damage() {
		return damage;
	}

	public double rate() {
		return rate;
	}

	public double range() {
		return range;
	}

	/*****************************************************************/

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public void setRange(double range) {
		this.range = range;
	}
}
