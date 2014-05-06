package units.targetable;

import java.util.HashSet;

import units.State;
import units.Unit;
import curse.CurseContainer;

public class Targetable extends Unit {
	protected HashSet<CurseContainer> curses;
	protected State state;
}