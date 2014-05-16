package agent.visualAgent.living;

import java.awt.Image;
import java.util.ArrayList;

import agent.visualAgent.VisualAgent;
import agent.visualAgent.VisualAgent.InitConfig;
import units.Unit;
import units.moveable.Moveable;
import utilities.SpriteSheetReader;

public class CockroachVisualAgent extends VisualAgent {

	private static ArrayList<Image> images;
	
	static {
		InitConfig init = VisualAgent.INIT_CONFIG.get(CockroachVisualAgent.class.getSimpleName());
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
