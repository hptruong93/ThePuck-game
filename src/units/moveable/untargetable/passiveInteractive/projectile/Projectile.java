package units.moveable.untargetable.passiveInteractive.projectile;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import main.engineInterface.GameConfig;
import main.engineInterface.GameMaster;
import main.engineInterface.ResourceFilter;
import units.Unit;
import units.immoveable.Building;
import units.moveable.livings.Damage;
import units.moveable.livings.Living;
import units.moveable.untargetable.Untargetable;
import utilities.FileUtility;
import utilities.geometry.Point;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import features.Clock;

public abstract class Projectile extends Untargetable implements Runnable {

	private static final String INIT_FILE = "data\\init\\Projectile.json";
	private static int DEFAULT_PROCESSING_TIME;
	public static final HashMap<String, InitConfig> INIT_CONFIG;
	protected Living owner;
	protected double damage;
	protected final int type;
	private int id;

	public Projectile(Living owner, Point position, Point target, double damage, InitConfig config, int side) {
		super(position, config.speed, config.turnRate, side);
		this.state.setTransparent(true);
		this.damage = damage;
		this.owner = owner;
		type = config.type;
		this.setDestination(position.getFrontPoint(position.angle(target), 9999));
		id = Clock.NOT_STARTED_ID;
	}

	@Override
	public void run() {
		if (isStopped()) {
			return;
		}
		
		ResourceFilter collide = new ResourceFilter() {
			@Override
			protected boolean filteringTest(Unit unit) {
				return collide(unit);
			}
		};

		for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
			if (i != side) {
				for (Living living : collide.filterLiving(i)) {
					damageLiving(living);
					stop();
					return;
				}
				
				for (Building building : collide.filterBuilding(i)) {
					damageBuilding(building);
					stop();
					return;
				}
				
			}
		}
	}

	public void start() {
		if (id > 0) {
			throw new IllegalStateException("Already started projectile...");
		}
		id = Clock.MASTER_CLOCK.scheduleFixedDelay(this, DEFAULT_PROCESSING_TIME, TimeUnit.MILLISECONDS);
	}
	
	private void stop() {
		GameMaster.removeProjectile(this);
		id = Clock.MASTER_CLOCK.removeScheduledTask(id);
	}
	
	/**
	 * The Master clock may not stop the thread immediately
	 * when the removeScheduledTask() method is called. Therefore is important
	 * to ignore any activity of the runnable once the  
	 * @return
	 */
	private boolean isStopped() {
		return id == Clock.STOPPED_ID || id == Clock.FAILURE_ID;
	}
	
	@Override
	protected abstract Projectile clone();

	protected void damageLiving(Living living) {
		Damage toDeal = new Damage(damage, type, owner);
		living.hp().damage(toDeal);
	}

	protected void damageBuilding(Building building) {

	}

	/***********************************************************************/
	static {
		DEFAULT_PROCESSING_TIME = -1;
		INIT_CONFIG = new HashMap<String, Projectile.InitConfig>();
		JsonRootNode root = FileUtility.readJSON(new File(INIT_FILE));
		for (JsonStringNode node : root.getFields().keySet()) {
			if (node.getText().equals("DEFAULT_PROCESSING_TIME")) {
				DEFAULT_PROCESSING_TIME = Integer.parseInt(root.getFields().get(node).getText());
			} else {
				INIT_CONFIG.put(node.getText(), new InitConfig(root.getFields().get(node)));
			}
		}
	}

	protected static class InitConfig {
		protected final double speed;
		protected final double turnRate;
		protected final int type;

		protected InitConfig(JsonNode info) {
			speed = Double.parseDouble(info.getNumberValue("speed"));
			turnRate = Double.parseDouble(info.getNumberValue("turnRate"));
			String typeString = info.getStringValue("damageType");
			if (typeString.equals("PHYSICAL")) {
				type = Damage.PHYSICAL;
			} else if (typeString.equals("MAGIC")) {
				type = Damage.MAGIC;
			} else if (typeString.equals("PURE")) {
				type = Damage.PURE;
			} else {
				throw new IllegalArgumentException("Invalid type argument in Projectile.json");
			}
		}
	}
}
