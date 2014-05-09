package stage;

import java.util.ArrayList;

import units.moveable.targetable.livings.Living;

public abstract class Stage {
	protected abstract ArrayList<Living> generateCreeps();
}
