package main;

import java.util.concurrent.TimeUnit;

import main.engineInterface.GameGraphics;
import main.userInterface.FrameMaster;
import features.Clock;



public class Main {
	public static void main(String[] args) {
		FrameMaster.getGameScreen().setVisible(true);
		
		Clock.MASTER_CLOCK.start();
		Clock.MASTER_CLOCK.scheduleFixedDelay(new GameGraphics(), 100, TimeUnit.MILLISECONDS);
	}
}
