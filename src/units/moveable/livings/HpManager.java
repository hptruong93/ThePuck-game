package units.moveable.livings;

public class HpManager {
	private final Living owner;
	private double regen;
	private double maxHealth;
	private double health;
	
	public HpManager(Living owner, double health, double initialHealth, double regen) {
		this.owner = owner;
		this.health = initialHealth;
		this.maxHealth = health;
		this.regen = regen;
	}
	
	public void regen() {
		setHealth(health + regen);
	}
	
	public double damage(Damage damage) {
		int type = damage.type();
		double damageDeal = damage.damage();
		
		if (type == Damage.PHYSICAL) {
			if (owner.state().isInvulnerable() || owner.state().isEthereal()) {
				damageDeal = 0;
			} else if (damageDeal > 0) {
				damageDeal = damageDeal * (1 - owner.state().magicResistance());
			}
		} else if (type == Damage.MAGIC) {
			if (owner.state().isInvulnerable() || owner.state().isRepel()) {
				damageDeal = 0;
			} else if (damageDeal > 0) {//If it is not heal
				damageDeal = damageDeal * (1 - owner.state().physicalResistance());
			}
		} else if (type == Damage.PURE) {
			if (owner.state().isInvulnerable()) {
				damageDeal = 0;
			}
		} else {
			throw new IllegalStateException("Invalid damage type " + type);
		}
		setHealth(health - damageDeal);
		return damageDeal;
	}
	
	/***************************************************************/
	private void setHealth(double health) {
		this.health = Math.min(maxHealth, Math.max(health, 0));
	}
	
	/***************************************************************/
	public void setRegen(double regen) {
		this.regen = regen;
	}
	
	public double currentHealth() {
		return health;
	}
	
	public double maxHealth() {
		return maxHealth;
	}
	
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}
}
