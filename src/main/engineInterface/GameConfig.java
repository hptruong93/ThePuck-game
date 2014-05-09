package main.engineInterface;

import java.awt.Dimension;
import java.awt.Toolkit;

public class GameConfig {
	public static final int SIDE_COUNT = 2;
	public static final int AI_SIDE = 1;
	
	public static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int MAP_WIDTH = 1600;
	public static final int MAP_HEIGHT = (int) (MAP_WIDTH * 9.0 /16);
	
	public static final int MINIMAP_WIDTH = (int) (SCREEN.width * 1.0 / 4);
	public static final int MINIMAP_HEIGHT = (int) (SCREEN.height * 1.0 / 3);
	
	public static final int DISPLAY_WIDTH = (int) (SCREEN.width * 1.0 / 1);
	public static final int DISPLAY_HEIGHT = (int) (SCREEN.height * 2.0 / 3);
	
	public static final double FOCUS_WIDTH = 100 * (SCREEN.width / 1250.0);
	public static final double FOCUS_HEIGHT = FOCUS_WIDTH / (5 / 3.0);
	
	public static final double SCALE_X = MAP_WIDTH / MINIMAP_WIDTH;
	public static final double SCALE_Y = MAP_HEIGHT / MINIMAP_HEIGHT;
	
	public static final double SCALE_MINI_X = DISPLAY_WIDTH / FOCUS_WIDTH;
	public static final double SCALE_MINI_Y = DISPLAY_HEIGHT / FOCUS_HEIGHT;
}
