package units.targetable.moveable.livings.boss;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import utilities.SpriteSheetReader;
import agent.VisualAgent;

public class ArchonVisualAgent implements VisualAgent {

	private static ArrayList<Image> images;
	private int index;
	
	static {
		images = SpriteSheetReader.readImage("data\\img\\Archon.png", 40,8, 500, 500);
	}
	
	@Override
	public void plot(Graphics2D a) {
		int width = images.get(index).getWidth(null);
		int height = images.get(index).getHeight(null);
		
		a.drawImage(images.get(index), 100, 100, 100+width, 100+height, 0, 0, width, height, null);
		index = (index + 1) % images.size();
	}

}
