package agent;

import java.awt.Image;
import java.util.ArrayList;

import units.Unit;
import units.moveable.Moveable;
import utilities.SpriteSheetReader;

public class RyskorVisualAgent extends VisualAgent {

	private static ArrayList<Image> images;
	
	static {
		InitConfiguration init = VisualAgent.INIT_CONFIG.get(RyskorVisualAgent.class.getSimpleName());
		images = SpriteSheetReader.readImage(init.loadPath(), init.instances(),init.column(), init.width(), init.height(), init.initialAngle());
	}
	
	@Override
	protected Image getNextRep(Unit owner) {
		if (owner instanceof Moveable) {
			if (((Moveable) owner).state().isMoving()) {
				index = (index + 1) % images.size();
			}
		}
		
		return images.get(index);
	}
}
