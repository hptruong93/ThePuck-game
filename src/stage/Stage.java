package stage;

import java.util.ArrayList;

import units.moveable.livings.Living;

public abstract class Stage {
	protected abstract ArrayList<Living> generateCreeps();
}
