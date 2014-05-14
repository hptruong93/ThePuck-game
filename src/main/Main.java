package main;

import java.util.concurrent.TimeUnit;

import main.engineInterface.GameConfig;
import main.engineInterface.GameGraphics;
import main.engineInterface.GameLogic;
import main.engineInterface.GameMaster;
import main.userInterface.FrameMaster;
import units.moveable.livings.boss.Sarcophagidae;
import utilities.geometry.Point;
import features.Clock;




public class Main {
	public static void main(String[] args) {
		/**
		 * Start testing session
		 */
		Sarcophagidae a = new Sarcophagidae(new Point(50, 50), 100, 0.04, 1);
		GameMaster.addLiving(GameConfig.getPlayerID(), a);
		
		for (int i = 0; i < 5; i++) {
			Sarcophagidae b = new Sarcophagidae(new Point(100, 100), 100, 0.04, 1);
			GameMaster.addLiving(GameConfig.getPlayerID(), b);
		}
		
		/**
		 * End testing session
		 */
		
		Clock.MASTER_CLOCK.start();
		FrameMaster.getGameScreen().setVisible(true);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameGraphics(), 100, TimeUnit.MILLISECONDS);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameLogic(), 50, TimeUnit.MILLISECONDS);
	}
}
