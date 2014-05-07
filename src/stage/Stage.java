package stage;

import java.util.ArrayList;

import units.targetable.moveable.Living;

public abstract class Stage {
	protected abstract ArrayList<Living> generateCreeps();
}
