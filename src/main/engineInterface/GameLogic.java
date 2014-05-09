package main.engineInterface;

import units.moveable.Moveable;
import units.moveable.untargetable.passiveInteractive.AOE;
import units.moveable.untargetable.passiveInteractive.projectile.Projectile;
import features.Log;

public class GameLogic implements Runnable {

	/**
	 * Process all the movements on the map. This includes
	 * <p>
	 * 1) All living movements (both AI & players)
	 * </p>
	 * <p>
	 * 2) All projectile movements
	 * </p>
	 * <p>
	 * 3) All AOE movements (only for those which move) AOE and Curses effects
	 * should be processed in their own thread
	 * </p>
	 */
	@Override
	public void run() {
		try {
			/**
			 * 1) Move all livings
			 */
			for (int side = 0; side < GameConfig.SIDE_COUNT; side++) {
				try {
					for (Moveable moveable : GameMaster.getLivings(side)) {
						moveable.move();
					}
				} finally {
					GameMaster.releaseLiving(side);
				}
			}

			/**
			 * 2) Move all projectiles
			 */
			try {
				for (Projectile projectile : GameMaster.getProjectiles()) {
					projectile.move();
				}
			} finally {
				GameMaster.releaseProjectiles();
			}

			/**
			 * 3) Move all AOE
			 */
			try {
				for (AOE aoe : GameMaster.getAOEs()) {
					aoe.move();
				}
			} finally {
				GameMaster.releaseAOEs();
			}
		} catch (Exception e) {
			Log.writeLog(e);
		}
	}
}
