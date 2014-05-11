package agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import units.Unit;
import utilities.geometry.Point;

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
	
	private static final int DEFAULT_SIZE = 100;
	protected int index;
	
	/**
	 * Base plotting method. Visual aspects of the unit should be stored within the Visual Agent, while
	 * the physical aspects should be stored within the unit object.
	 * @param a graphics that will be used by the agent to plot.
	 * @param owner the unit that will be plot
	 */
	public void plot(Graphics2D a, Unit owner) {
		Image next = getNextRep();
		int width = next.getWidth(null);
		int height = next.getHeight(null);
		
		Point display = owner.position().realToDisplay(new Point (50, 50));
		AffineTransform transform = AffineTransform.getTranslateInstance(display.getX(), display.getY());
		transform.rotate(owner.movingAngle());
		a.transform(transform);

		AffineTransform flip = new AffineTransform();
		if (owner.movingAngle() > Math.PI/2 && owner.movingAngle() < 3 * Math.PI / 2) {
			flip.scale(1, -1);
		}
		
		flip.translate(-width / 2 , - height / 2);
		a.drawImage(next, flip, null);
	}
	
	protected abstract Image getNextRep();
	
	public static final HashMap<String, InitConfiguration> INIT_CONFIG;
	
	static {
		INIT_CONFIG = new HashMap<String, InitConfiguration>();
		INIT_CONFIG.put(CockroachVisualAgent.class.getSimpleName(), new InitConfiguration("data\\img\\Creep.png", 16, 4, DEFAULT_SIZE, DEFAULT_SIZE));
		INIT_CONFIG.put(ArchonVisualAgent.class.getSimpleName(), new InitConfiguration("data\\img\\Archon.png", 40, 8, DEFAULT_SIZE, DEFAULT_SIZE));
	}
	
	protected static class InitConfiguration {
		
		private final int instances, column, width, height;
		private final String loadPath;
		
		private InitConfiguration(String loadPath, int instances, int column, int width, int height) {
			this.instances = instances;
			this.column = column;
			this.width = width;
			this.height = height;
			this.loadPath = loadPath;
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
