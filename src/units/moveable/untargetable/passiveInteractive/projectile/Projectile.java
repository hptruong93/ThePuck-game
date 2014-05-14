package units.moveable.untargetable.passiveInteractive.projectile;

import main.engineInterface.ResourceFilter;
import units.Unit;
import units.immoveable.Building;
import units.moveable.livings.Living;
import units.moveable.untargetable.Untargetable;
import utilities.geometry.Point;

public abstract class Projectile extends Untargetable implements Runnable {

	public Projectile(Point position, double speed, double turnRate, int side) {
		super(position, speed, turnRate, side);
		this.state.setTransparent(true);
	}
	
	@Override
	public void run() {
		ResourceFilter collide = new ResourceFilter() {
			
			@Override
			protected boolean filteringTest(Unit unit) {
				return collide(unit);
			}
		};
		
//		for (Living living : collide.filterLiving(side))
	}

	protected void damageLiving(Living living) {
		
	}
	
	protected void damgeBuilding(Building building) {
		
	}
}
