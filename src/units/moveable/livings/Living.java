package units.moveable.livings;

import java.io.File;
import java.util.HashMap;

import main.engineInterface.GameConfig;
import units.Unit;
import units.moveable.Moveable;
import units.moveable.untargetable.passiveInteractive.projectile.ProjectileGenerator;
import utilities.FileUtility;
import utilities.geometry.Point;
import ai.AutoAttack;
import ai.MovementAI;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

public abstract class Living extends Moveable {
	private static final String INIT_FILE = "data\\init\\Living.json";
	public static final HashMap<String, InitConfig> INIT_CONFIG;
	
	private Unit targeted;
	protected ProjectileGenerator projectileFactory;
	protected AutoAttack attackAI;
	protected AttackManager attackManager;
	protected MovementAI movementAI;
	protected HpManager hp;
	
	public Living(Point position, InitConfig config, int side) {
		super(position, config.speed, config.turnRate, side);
		hp = new HpManager(this, config.health, config.maxHealth, config.regen);
		attackManager = new AttackManager(this, config.damage, config.attackSpeed, config.attackRange);
		
		if (side == GameConfig.AI_SIDE) {
			attackAI = new AutoAttack(this);
			attackAI.start();
			
			movementAI = new MovementAI(this);
			movementAI.start();
		}
	}
	
	public HpManager hp() {
		return hp;
	}
	
	public AttackManager attackAgent() {
		return attackManager;
	}
	
	public Unit targeted() {
		return targeted;
	}
	
	public void setTargeted(Unit targeted) {
		this.targeted = targeted;
	}
	/************************************************************************************/
	static {
		INIT_CONFIG = new HashMap<String, InitConfig>();
		JsonRootNode root = FileUtility.readJSON(new File(INIT_FILE));
		for (JsonStringNode node : root.getFields().keySet()) {
			JsonNode object = root.getFields().get(node);
			for (JsonStringNode className : object.getFields().keySet()) {
				INIT_CONFIG.put(className.getText(), new InitConfig(object.getFields().get(className)));
			}
		}
	}
	
	public static class InitConfig {
		protected final double health, maxHealth, regen;
		protected final double speed;
		protected final double turnRate;
		protected final double attackSpeed;
		protected final double damage;
		protected final double attackRange;
		
		public InitConfig(JsonNode info) {
			health = Double.parseDouble(info.getNumberValue("maxHealth"));
			maxHealth = health;
			regen = Double.parseDouble(info.getNumberValue("regen"));
			speed = Double.parseDouble(info.getNumberValue("speed"));
			turnRate = Double.parseDouble(info.getNumberValue("turnRate"));
			damage = Double.parseDouble(info.getNullableNumberValue("damage"));
			attackSpeed = 1 / Double.parseDouble(info.getNullableNumberValue("attackSpeed"));
			attackRange = Double.parseDouble(info.getNullableNumberValue("attackRange"));
		}
	}
}
