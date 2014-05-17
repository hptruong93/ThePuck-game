package main;

import java.util.concurrent.TimeUnit;

import main.engineInterface.GameConfig;
import main.engineInterface.GameGraphics;
import main.engineInterface.GameLogic;
import main.engineInterface.GameMaster;
import main.userInterface.FrameMaster;
import units.moveable.livings.Living;
import units.moveable.livings.Living.InitConfig;
import units.moveable.livings.boss.Sarcophagidae;
import units.moveable.livings.creep.Cockroach;
import utilities.geometry.Point;
import features.Clock;




public class Main {
	public static void main(String[] args) {
		/**
		 * Start testing session
		 */
		InitConfig config = Living.INIT_CONFIG.get(Sarcophagidae.class.getSimpleName());
		
		Cockroach a = new Cockroach(new Point(10, 10), config, GameConfig.getPlayerID());
		GameMaster.addLiving(a.side(), a);

		Sarcophagidae b = new Sarcophagidae(new Point(150, 150), config, GameConfig.AI_SIDE);
		GameMaster.addLiving(b.side(), b);
		
		/**
		 * End testing session
		 */
		
		Clock.MASTER_CLOCK.start();
		FrameMaster.getGameScreen().setVisible(true);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameGraphics(), 50, TimeUnit.MILLISECONDS);
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameLogic(), 50, TimeUnit.MILLISECONDS);
	}
}
