package agent;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashMap;

import units.Unit;

public abstract class VisualAgent {
	
	protected int index;
	
	public void plot(Graphics2D a, Unit owner) {
		Image next = getNextRep();
		int width = next.getWidth(null);
		int height = next.getHeight(null);
		a.drawImage(next, 100, 100, 100+width, 100+height, 0, 0, width, height, null);
	}
	
	protected abstract Image getNextRep();
	
	public static final HashMap<String, InitConfiguration> INIT_CONFIG;
	
	static {
		INIT_CONFIG = new HashMap<String, InitConfiguration>();
		INIT_CONFIG.put(CockroachVisualAgent.class.getSimpleName(), new InitConfiguration("data\\img\\Creep.png", 16, 4, 500, 500));
		INIT_CONFIG.put(ArchonVisualAgent.class.getSimpleName(), new InitConfiguration("data\\img\\Archon.png", 40, 8, 500, 500));
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
