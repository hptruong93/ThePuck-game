package agent.visualAgent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.HashMap;

import units.Unit;
import utilities.FileUtility;
import utilities.geometry.Point;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

/**
 * Base class for any visual agent.
 * Any agent with different plotting method may override plot() method.
 * This class also contains all initialization of all VisualAgents through
 * HashMap<[class_name], [init_configuration]>
 * @see class InitConfiguration
 * @author VDa
 *
 */
public abstract class VisualAgent {
	
	private static final String INIT_FILE = "data\\init\\VisualAgent.json";
	private static final int DEFAULT_SIZE = 100;
	protected int index;
	
	/**
	 * Base plotting method. Visual aspects of the unit should be stored within the Visual Agent, while
	 * the physical aspects should be stored within the unit object.
	 * @param a graphics that will be used by the agent to plot.
	 * @param owner the unit that will be plot
	 */
	public void plot(Graphics2D a, AffineTransform defaultTransform, Unit owner) {
		a.setTransform(defaultTransform);
		Image next = getNextRep(owner);
		int width = next.getWidth(null);
		int height = next.getHeight(null);
		
		Point display = owner.position().realToDisplay(new Point (50, 50));
		AffineTransform transform = AffineTransform.getTranslateInstance(display.getX(), display.getY());
		transform.rotate(owner.movingAngle());
		a.transform(transform);
//		a.fill(new Ellipse2D.Double(-owner.radius(), -owner.radius(), 2 * owner.radius(), 2 * owner.radius()));

		AffineTransform flip = new AffineTransform();
		if (owner.movingAngle() > Math.PI/2 && owner.movingAngle() < 3 * Math.PI / 2) {
			flip.scale(1, -1);
		}
		
		flip.translate(-width / 2 , - height / 2);
		a.drawImage(next, flip, null);
	}
	
	/**
	 * Get next Image to plot. This should reflects the motion and actions of the Unit
	 * @param owner the Unit that the agent is representing
	 * @return the next Image to plot
	 */
	protected abstract Image getNextRep(Unit owner);
	
	public static final HashMap<String, InitConfig> INIT_CONFIG;
	
	/**
	 * Load the initial configuration into a hash map. Other visual agents
	 * will use this hash map to initialize. The hash map should have the keys
	 * representing the name of all the visual agents, the the values indicating
	 * the init configuration of each agent.
	 */
	static {
		INIT_CONFIG = new HashMap<String, InitConfig>();
//		JsonRootNode root = FileUtility.readJSON(new File(INIT_FILE));
//		for (JsonStringNode node : root.getFields().keySet()) {
//			INIT_CONFIG.put(node.getText(), new InitConfig(root.getFields().get(node)));
//		}
		
		JsonRootNode root = FileUtility.readJSON(new File(INIT_FILE));
		for (JsonStringNode node : root.getFields().keySet()) {
			JsonNode object = root.getFields().get(node);
			for (JsonStringNode className : object.getFields().keySet()) {
				INIT_CONFIG.put(className.getText(), new InitConfig(object.getFields().get(className)));
			}
		}
	}
	
	/**
	 * Class encapsulating initial configuration to load sprite sheet.
	 * This includes:
	 * 1) Number of instances in the sprite sheet
	 * 2) Number of column of images in the sprite sheet
	 * 3) The final width of one instance (after being scaled)
	 * 4) The final height of one instance (after being scaled)
	 * 5) The angle at which the image is rotated originally (standard image should face along the x axis)
	 * 6) The path to the image to load
	 * @author VDa
	 *
	 */
	public static class InitConfig {
		
		private final int instances, column, width, height;
		private final double initialAngle;
		private final String loadPath;
		
		private InitConfig(JsonNode info) {
			this.instances = Integer.parseInt(info.getNumberValue("instances"));
			this.column = Integer.parseInt(info.getNumberValue("columnNumber"));
			
			int tempWidth, tempHeight;
			try {
				tempWidth = Integer.parseInt(info.getNumberValue("width"));
				tempHeight = Integer.parseInt(info.getNumberValue("height"));
			} catch (Exception e) {
				tempWidth = DEFAULT_SIZE;
				tempHeight = DEFAULT_SIZE;
			}
			
			this.width = tempWidth;
			this.height = tempHeight;
			this.loadPath = info.getStringValue("path");
			this.initialAngle = Math.toRadians(Double.parseDouble(info.getNumberValue("initialAngle")));
		}
		
		private InitConfig(String loadPath, int instances, int column, int width, int height, double initialAngle) {
			this.instances = instances;
			this.column = column;
			this.width = width;
			this.height = height;
			this.loadPath = loadPath;
			this.initialAngle = initialAngle;
		}
		
		public double initialAngle() {
			return initialAngle;
		}
		
		public String loadPath() {
			return loadPath;
		}
		
		public int instances() {
			return instances;
		}
		
		public int column() {
			return column;
		}
		
		public int width() {
			return width;
		}
		
		public int height() {
			return height;
		}
	}
}
