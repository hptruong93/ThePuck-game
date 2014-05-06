package units;

public class State {
	private boolean ethereal;
	private boolean repel;
	private boolean invulnerable;
	private boolean silent;
	
	public boolean isEthereal() {
		return ethereal;
	}
	
	public synchronized void setEthereal(boolean ethereal) {
		this.ethereal = ethereal;
	}
	
	public boolean isRepel() {
		return repel;
	}
	
	public synchronized void setRepel(boolean repel) {
		this.repel = repel;
	}
	
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public synchronized void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}
	
	public boolean isSilent() {
		return silent;
	}
	
	public synchronized void setSilent(boolean silent) {
		this.silent = silent;
	}
}