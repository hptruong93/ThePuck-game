package agent.visualAgent.projectile;

import java.awt.Image;
import java.util.ArrayList;

import units.Unit;
import utilities.SpriteSheetReader;
import agent.visualAgent.VisualAgent;

public class SarcophagidaeProjva extends VisualAgent {

	private static ArrayList<Image> images;

	static {
		InitConfig init = VisualAgent.INIT_CONFIG.get(SarcophagidaeProjva.class.getSimpleName());
		images = SpriteSheetReader.readImage(init.loadPath(), init.instances(), init.column(), init.width(), init.height(), init.initialAngle());
	}

	@Override
	protected Image getNextRep(Unit owner) {
		index = (index + 1) % images.size();
		return images.get(index);
	}
}
