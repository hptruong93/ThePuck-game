package ai;

import java.util.concurrent.TimeUnit;

import main.engineInterface.GameConfig;
import main.engineInterface.ResourceFinder;
import units.Unit;
import units.moveable.livings.Living;
import features.Clock;
import features.ReactiveRunnable;

public class MovementAI implements Runnable, ReactiveRunnable {

	private int id = Clock.NOT_STARTED_ID; 
	private final Living owner;
	
	public MovementAI(Living owner) {
		this.owner = owner;
	}
	
	
	@Override
	public final void start() {
		id = Clock.MASTER_CLOCK.scheduleFixedDelay(this, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public final void stop() {
		if (id != Clock.NOT_STARTED_ID) {
			throw new IllegalStateException("Already started...");
		}
		id = Clock.MASTER_CLOCK.removeScheduledTask(id);
	}

	@Override
	public boolean isStopped() {
		return id == Clock.STOPPED_ID || id == Clock.FAILURE_ID;
	}

	@Override
	public void run() {
		if (isStopped()) {
			return;
		}
		
		if (owner.targeted() == null) {
			ResourceFinder finder = new ResourceFinder() {
				
				@Override
				protected boolean findingTest(Unit unit) {
					return true;
				}
			};
			
			Unit lockedUnit = null;
			
			for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
				if (i != owner.side()) {
					lockedUnit = finder.findLiving(i);
				}
				if (lockedUnit != null) {
					break;
				}
			}
			
			for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
				if (i != owner.side()) {
					lockedUnit = finder.findBuilding(i);
				}
				if (lockedUnit != null) {
					break;
				}
			}
			
			if (lockedUnit != null) {
				owner.setDestination(lockedUnit.position());
				owner.setTargeted(lockedUnit);
			}
		}
	}

}
