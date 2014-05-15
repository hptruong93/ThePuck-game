package units.moveable.livings;

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
