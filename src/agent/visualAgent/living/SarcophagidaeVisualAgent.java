package agent.visualAgent.living;

import java.awt.Image;
import java.util.ArrayList;

import agent.visualAgent.VisualAgent;
import agent.visualAgent.VisualAgent.InitConfig;
import units.Unit;
import utilities.SpriteSheetReader;

public class SarcophagidaeVisualAgent extends VisualAgent {
	private static ArrayList<Image> images;

	static {
		InitConfig init = VisualAgent.INIT_CONFIG.get(SarcophagidaeVisualAgent.class.getSimpleName());
		images = SpriteSheetReader.readImage(init.loadPath(), init.instances(), init.column(), init.width(), init.height(), init.initialAngle());
	}

	@Override
	protected Image getNextRep(Unit owner) {
		index = (index + 1) % images.size();
		return images.get(index);
	}
}
