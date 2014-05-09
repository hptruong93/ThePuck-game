package curse;

import units.moveable.targetable.livings.Living;

public abstract class Curse {
	protected abstract void apply(Living affected);
}
