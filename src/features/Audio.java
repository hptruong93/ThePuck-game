package features;


/**
 * Provide utitlity to play sound file This is a static class. No instance
 * should be created from outside
 * 
 * @author VDa
 * 
 */
public class Audio {

	public static String EXAMPLE_SOUND = "Bla.wav";
	
	public static void main(String[] args) {
		playSound(EXAMPLE_SOUND);
		
		while (true) {
			attemptReplay(EXAMPLE_SOUND);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Private constructor so that no instance is created
	 */
	private Audio() {
		throw new IllegalStateException("Cannot create instance of a static class Audio");
	}

	/**
	 * Load all sounds into memory.
	 */
	public static void init() {
		/*
		 * Don't do this yet. This needs the Clock class to be up
		 */
	}
	
	/**
	 * Attempt to loop the sound
	 * @param soundToLoop
	 */
	public void loopSound(String soundToLoop) {
		/*
		 * Don't do this yet. This needs the Clock class to be up
		 */
	}
	
	/**
	 * Start playing a sound from memory
	 * @param soundToPlay name of the sound
	 */
	public static void playSound(String soundToPlay) {
	}

	/**
	 * Attempt to replay a wave. If the sound is playing, do nothing
	 * @param soundToReplay the sound that will be replay if possible
	 */
	public static void attemptReplay(String soundToReplay) {
	}
}
