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
		Sarcophagidae a = new Sarcophagidae(new Point(50, 50), 100, 0.02, 1);
		GameMaster.addLiving(GameConfig.getPlayerID(), a);
		
		Sarcophagidae b = new Sarcophagidae(new Point(100, 100), 100, 0.02, 1);
		GameMaster.addLiving(GameConfig.getPlayerID(), b);
		
		Sarcophagidae c = new Sarcophagidae(new Point(100, 100), 100, 0.02, 1);
		GameMaster.addLiving(GameConfig.getPlayerID(), c);
		
		Sarcophagidae d = new Sarcophagidae(new Point(100, 100), 100, 0.02, 1);
		GameMaster.addLiving(GameConfig.getPlayerID(), d);
		
		/**
		 * End testing session
		 */
		
		Clock.MASTER_CLOCK.start();
		FrameMaster.getGameScreen().setVisible(true);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameGraphics(), 100, TimeUnit.MILLISECONDS);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameLogic(), 50, TimeUnit.MILLISECONDS);
	}
}
