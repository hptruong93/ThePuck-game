package main.userInterface;

public class FrameMaster {
	private static GameScreen gameScreen;
	private static Configuration configuration;
	private static WelcomeScreen welcomeScreen;
	
	static {
		gameScreen = new GameScreen();
		configuration = new Configuration();
		welcomeScreen = new WelcomeScreen();
	}
	
	public static GameScreen getGameScreen() {
		return gameScreen;
	}
	
	public static Configuration getConfiguration() {
		return configuration;
	}
	
	public static WelcomeScreen getWelcomeScreen() {
		return welcomeScreen;
	}
}
