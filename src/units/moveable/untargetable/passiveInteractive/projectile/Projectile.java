package units.moveable.untargetable.passiveInteractive.projectile;

import main.engineInterface.ResourceFilter;
import units.Unit;
import units.immoveable.Building;
import units.moveable.livings.Damage;
import units.moveable.livings.Living;
import units.moveable.untargetable.Untargetable;
import utilities.geometry.Point;
import argo.jdom.JsonNode;

public abstract class Projectile extends Untargetable implements Runnable {

	protected Living owner;
	protected double damage;
	protected final int type;
	
	public Projectile(Living owner, Point position, double damage, InitConfig config, int side) {
		super(position, config.speed, config.turnRate, side);
		this.state.setTransparent(true);
		this.damage = damage;
		this.owner = owner;
		type = config.type;
	}
	
	@Override
	public void run() {
		ResourceFilter collide = new ResourceFilter() {
			
			@Override
			protected boolean filteringTest(Unit unit) {
				return collide(unit);
			}
		};
		
		for (Living living : collide.filterLiving(side)) {
			damageLiving(living);
		}
		
		for (Building building : collide.filterBuilding(side)) {
			damageBuilding(building);
		}
	}

	@Override
	protected abstract Projectile clone();
	
	protected void damageLiving(Living living) {
		Damage toDeal = new Damage(damage, type, owner);
		living.hp().damage(toDeal);
	}
	
	protected void damageBuilding(Building building) {
		
	}
	
	protected static class InitConfig {
		protected final double speed;
		protected final double turnRate;
		protected final int type;
		
		protected InitConfig(JsonNode info) {
			speed = Double.parseDouble(info.getNumberValue("speed"));
			turnRate = Double.parseDouble(info.getNumberValue("turnRate"));
			type = Integer.parseInt(info.getNumberValue("damageType"));
		}
	}
}
