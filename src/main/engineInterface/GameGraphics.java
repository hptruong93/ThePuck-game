package main.engineInterface;

import java.awt.Graphics;
import java.awt.Graphics2D;

import main.userInterface.FrameMaster;
import units.targetable.immoveable.Building;
import units.targetable.moveable.Moveable;
import units.untargetable.passiveInteractive.AOE;
import units.untargetable.visualEffect.VisualEffect;

public class GameGraphics implements Runnable {

	@Override
	public void run() {
		FrameMaster.getGameScreen().repaintBoard();
	}
	
	public static void plotAll(Graphics g) {
		Graphics2D a = (Graphics2D) g;
		
		for (VisualEffect effect : GameMaster.getVisualEffects()) {
			effect.plot(a);
		}
		
		for (AOE aoe : GameMaster.getAOEs()) {
			aoe.plot(a);
		}
		
		for (int i = 0; i < GameConfig.SIDE_COUNT; i++) {
			for (Moveable move : GameMaster.getMoveable(i)) {
				move.plot(a);
			}
			
			for (Building building : GameMaster.getBuildings(i)) {
				building.plot(a);
			}
		}
	}
}
