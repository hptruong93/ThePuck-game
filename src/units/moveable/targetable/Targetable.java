package units.moveable.targetable;

import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

import units.State;
import units.moveable.Moveable;
import curse.CurseContainer;

public class Targetable extends Moveable {
	private final ReentrantLock cursesLock = new ReentrantLock();
	
	protected HashSet<CurseContainer> curses;
	protected State state;
	
	@Override
	public void plot(Graphics2D a) {
		super.plot(a);
		cursesLock.lock();
		try {
			
		} finally {
			cursesLock.unlock();
		}
	}
}