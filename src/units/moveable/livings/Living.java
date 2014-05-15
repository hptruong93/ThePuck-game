package units.moveable.livings;

import java.io.File;
import java.util.HashMap;

import units.moveable.Moveable;
import utilities.FileUtility;
import utilities.geometry.Point;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

public abstract class Living extends Moveable {
	private static final String INIT_FILE = "data\\init\\Living.json";
	protected static final HashMap<String, InitConfig> INIT_CONFIG; 
	protected HpManager hp;
	
	public Living(Point position, InitConfig config, int side) {
		super(position, config.speed, config.turnRate, side);
		hp = new HpManager(this, config.health, config.maxHealth, config.regen);
	}
	
	public HpManager hp() {
		return hp;
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
		
		public InitConfig(JsonNode info) {
			health = Double.parseDouble(info.getNumberValue("maxHealth"));
			maxHealth = health;
			regen = Double.parseDouble(info.getNumberValue("regen"));
			speed = Double.parseDouble(info.getNumberValue("speed"));
			turnRate = Double.parseDouble(info.getNumberValue("turnRate"));
			attackSpeed = 0;
		}
		
		public InitConfig(double health, double maxHealth, double regen, double speed, double turnRate) {
			this.health = health;
			this.maxHealth = maxHealth;
			this.regen = regen;
			this.speed = speed;
			this.turnRate = turnRate;
			this.attackSpeed = 0;
		}
	}
}
