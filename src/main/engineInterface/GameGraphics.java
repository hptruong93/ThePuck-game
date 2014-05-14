package main.engineInterface;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.userInterface.FrameMaster;
import units.immoveable.Building;
import units.moveable.Moveable;
import units.moveable.untargetable.passiveInteractive.AOE;
import units.moveable.untargetable.visualEffect.VisualEffect;
import features.Log;

public class GameGraphics implements Runnable {

	private static final AffineTransform DEFAULT_TRANSFORM = new AffineTransform();
	
	/**
	 * Redraw the whole map. This should lead directly to the plotAll() method
	 * below to be invoked.
	 */
	@Override
	public void run() {
		FrameMaster.getGameScreen().repaintBoard();
	}

	/**
	 * Plot all elements on the map. Access to the resources from GameMaster
	 * must be thread-safe.
	 * 
	 * @param g
	 *            graphics of the component used to draw the element.
	 */
	public static void plotAll(Graphics g) {
		Graphics2D a = (Graphics2D) g;

		try {
			try {
				for (VisualEffect effect : GameMaster.getVisualEffects()) {
					effect.plot(a, DEFAULT_TRANSFORM);
				}
			} finally {
				GameMaster.releaseVisualEffect();
			}

			try {
				for (AOE aoe : GameMaster.getAOEs()) {
					aoe.plot(a, DEFAULT_TRANSFORM);
				}
			} finally {
				GameMaster.releaseAOEs();
			}

			for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
				try {
					for (Moveable move : GameMaster.getLivings(i)) {
						move.plot(a, DEFAULT_TRANSFORM);
					}
				} finally {
					GameMaster.releaseLiving(i);
				}

				try {
					for (Building building : GameMaster.getBuildings(i)) {
						building.plot(a, DEFAULT_TRANSFORM);
					}
				} finally {
					GameMaster.releaseBuilding(i);
				}
			}
		} catch (Exception e) {
			Log.writeLog(e);
		}
	}
}
