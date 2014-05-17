package ai;

import java.util.concurrent.TimeUnit;

import units.moveable.livings.Living;
import utilities.Util;
import utilities.geometry.Geometry;
import features.Clock;
import features.ReactiveRunnable;

public class AutoAttack implements Runnable, ReactiveRunnable {
	private int id = Clock.NOT_STARTED_ID;
	private final Living owner;
	
	public AutoAttack(Living owner) {
		this.owner = owner;
	}
	
	@Override
	public void start() {
		id = Clock.MASTER_CLOCK.scheduleFixedDelay(this, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void stop() {
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

		if (owner.targeted() != null) {
			double destAngle = Geometry.fix2Pi(owner.angle(owner.targeted()));
			if (owner.distance(owner.targeted()) < owner.attackAgent().range()
					&& Util.equal(owner.movingAngle(), destAngle, Math.toRadians(30))) {
				owner.attackAgent().attack(owner.targeted());
				owner.setDestination(owner.position());
			} else {
				owner.setDestination(owner.targeted().position());
			}
		}
		
	}
}
