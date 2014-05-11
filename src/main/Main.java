package main;

import java.util.concurrent.TimeUnit;

import main.engineInterface.GameGraphics;
import main.engineInterface.GameLogic;
import main.engineInterface.GameMaster;
import main.userInterface.FrameMaster;
import units.moveable.livings.boss.Archon;
import utilities.geometry.Point;
import features.Clock;



public class Main {
	public static void main(String[] args) {
		FrameMaster.getGameScreen().setVisible(true);
		
		/**
		 * Start testing session
		 */
		Archon a = new Archon(new Point(50, 50), 100, 0.02, 0.001);
		a.setDestination(new Point(100, 100));
		GameMaster.addLiving(0, a);
		
		/**
		 * End testing session
		 */
		
		Clock.MASTER_CLOCK.start();
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameGraphics(), 100, TimeUnit.MILLISECONDS);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameLogic(), 50, TimeUnit.MILLISECONDS);
	}
}
