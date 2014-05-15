package agent;

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
	
	protected abstract Image getNextRep(Unit owner);
	
	public static final HashMap<String, InitConfig> INIT_CONFIG;
	
	static {
		INIT_CONFIG = new HashMap<String, InitConfig>();
		JsonRootNode root = FileUtility.readJSON(new File(INIT_FILE));
		for (JsonStringNode node : root.getFields().keySet()) {
			INIT_CONFIG.put(node.getText(), new InitConfig(root.getFields().get(node)));
		}
	}
	
	protected static class InitConfig {
		
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
		
		protected double initialAngle() {
			return initialAngle;
		}
		
		protected String loadPath() {
			return loadPath;
		}
		
		protected int instances() {
			return instances;
		}
		
		protected int column() {
			return column;
		}
		
		protected int width() {
			return width;
		}
		
		protected int height() {
			return height;
		}
	}
}
