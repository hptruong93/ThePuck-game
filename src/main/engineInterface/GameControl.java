package main.engineInterface;

import java.awt.event.InputEvent;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import units.Unit;
import units.immoveable.Building;
import units.moveable.livings.Living;
import utilities.geometry.Point;

public class GameControl {

	private Unit selectedUnit;
	private final ReentrantReadWriteLock selectedUnitLock;

	private Point focus;

	{
		selectedUnitLock = new ReentrantReadWriteLock();
		focus = new Point(50, 50);
	}

	public void mouseClicked(Point point, int modifier) {
		try {
			for (Living s : GameMaster.getLivings(GameConfig.getPlayerID())) {
				selectedUnit = s;
				break;
			}
		} finally {
			GameMaster.releaseLiving(GameConfig.getPlayerID());
		}

		if (modifier == InputEvent.BUTTON1_MASK) {
		} else if (modifier == InputEvent.BUTTON3_MASK) {
			if (isControllingUnit()) {
				if (selectedUnit instanceof Living) {
					((Living) selectedUnit).setDestination(point);
				}
			}
		}
	}

	private boolean isControllingUnit() {
		if (selectedUnit == null) {
			return false;
		} else {
			boolean output = false;
			if (selectedUnit instanceof Building) {
				try {
					output = GameMaster.getBuildings(GameConfig.getPlayerID()).contains(selectedUnit);
				} finally {
					GameMaster.releaseBuilding(GameConfig.getPlayerID());
				}
			} else if (selectedUnit instanceof Living) {
				try {
					output = GameMaster.getLivings(GameConfig.getPlayerID()).contains(selectedUnit);
				} finally {
					GameMaster.releaseLiving(GameConfig.getPlayerID());
				}
			} else {
				return false;
			}
			return output;
		}
	}

	/************** Selected Unit ****************/
	public Unit getSelectedUnit() {
		selectedUnitLock.readLock().lock();
		return selectedUnit;
	}

	public void setSelectedUnit(Unit newUnit) {
		this.selectedUnitLock.writeLock().lock();
		try {
			this.selectedUnit = newUnit;
		} finally {
			this.selectedUnitLock.writeLock().unlock();
		}
	}

	/************** Focus ************************/
	public Point focus() {
		return focus;
	}

	public void setFocus(Point newFocus) {
		focus = newFocus;
	}
}
