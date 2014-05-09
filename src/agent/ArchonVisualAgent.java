package agent;

import java.awt.Image;
import java.util.ArrayList;

import utilities.SpriteSheetReader;

public class ArchonVisualAgent extends VisualAgent {

	private static ArrayList<Image> images;
	
	static {
		InitConfiguration init = VisualAgent.INIT_CONFIG.get(ArchonVisualAgent.class.getSimpleName());
		images = SpriteSheetReader.readImage(init.loadPath(), init.instances(),init.column(), init.width(), init.height());
	}
	
	@Override
	protected Image getNextRep() {
		index = (index + 1) % images.size();
		return images.get(index);
	}
}
