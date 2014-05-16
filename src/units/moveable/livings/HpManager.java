package units.moveable.livings;

/**
 * Class encapsulating the hit point information of a unit.
 * This class provides interface to assess damage dealt to the unit
 * based on its state, and the hit point regeneration of the unit.
 * @author VDa
 *
 */
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
	
	/**
	 * Regenerate the unit by an amount. This based on the regen of the unit and its state.
	 * This is equivalent to apply a PURE type healing to the unit
	 */
	public void regen() {
		damage(new Damage(-regen, Damage.PURE, null));
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
