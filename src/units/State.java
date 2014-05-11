package units;


public class State {
	private boolean ethereal;
	private boolean repel;
	private boolean invulnerable;
	private boolean silent;
	/*********************/
	private boolean moving;
	private boolean moveable;
	
	public State() {
		moveable = true;
	}
	
	public boolean isEthereal() {
		return ethereal;
	}
	
	public boolean isRepel() {
		return repel;
	}
	
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public boolean isSilent() {
		return silent;
	}
	
	public boolean isMoving() {
		return moving;
	}
	
	public boolean moveable() {
		return moveable;
	}
	
	/***********************************************************/
	
	public synchronized void setEthereal(boolean ethereal) {
		this.ethereal = ethereal;
	}
	
	public synchronized void setRepel(boolean repel) {
		this.repel = repel;
	}
	
	public synchronized void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}
	
	public synchronized void setSilent(boolean silent) {
		this.silent = silent;
	}
	
	public synchronized void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public synchronized void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	
	/***********************************************************/
}