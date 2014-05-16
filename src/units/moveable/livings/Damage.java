package units.moveable.livings;

/**
 * Class encapsulating one instance of damage/ healing to the unit.
 * This includes
 * 1) The damage (negative damage means healing)
 * 2) The type of damage/ healing
 * 3) The source of the damage.
 * @author VDa
 *
 */
public class Damage {
	public static final int PHYSICAL = 0;
	public static final int MAGIC = 1;
	public static final int PURE = 2;
	
	private final double damage; //Negative means heal
	private final int type;
	private final Living source;
	
	public Damage(double damage, int type, Living source) {
		this.damage = damage;
		this.type = type;
		this.source = source;
	}
	
	public double damage() {
		return damage;
	}
	
	public int type() {
		return type;
	}
	
	public Living source() {
		return source;
	}
}
