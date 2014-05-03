package features;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Defining Macro for the game.
 * Provide default values once an instance is created.
 * Several keys are reserved and should not be assigned as macro in game.
 * @author VDa
 *
 */
public class Macro {

	   private final int[] hotKeys;
	   public static final int NUMBER_OF_MACROS = 17;
	   public static final int FIRST_SKILL_INDEX = 0;
	   public static final int SECOND_SKILL_INDEX = 1;
	   public static final int THIRD_SKILL_INDEX = 2;
	   public static final int ULTIMATE_SKILL_INDEX = 3;
	   public static final int BLINK_INDEX = 4;
	   public static final int ATTACK_INDEX = 5;
	   public static final int STOP_INDEX = 6;
	   public static final int FOCUS_INDEX = 7;
	   public static final int MUSIC_TOGGLE_INDEX = 8;
	   public static final int SOUND_TOGGLE_INDEX = 9;
	   public static final int SAVE_INDEX = 10;
	   public static final int SKILL_TOGGLE_INDEX = 11;
	   public static final int FIRST_SKILL_BONUS_TOGGLE_INDEX = 12;
	   public static final int SECOND_SKILL_BONUS_TOGGLE_INDEX = 13;
	   public static final int THIRD_SKILL_BONUS_TOGGLE_INDEX = 14;
	   public static final int ULTIMATE_SKILL_BONUS_TOGGLE_INDEX = 15;
	   public static final int PAUSE_INDEX = 16;
	   
	   private static final int DEFAULT_FIRST_SKILL = KeyEvent.VK_R;
	   private static final int DEFAULT_SECOND_SKILL = KeyEvent.VK_W;
	   private static final int DEFAULT_THIRD_SKILL = KeyEvent.VK_F;
	   private static final int DEFAULT_ULTIMATE_SKILL = KeyEvent.VK_C;
	   private static final int DEFAULT_BLINK_SKILL = KeyEvent.VK_SPACE;
	   private static final int DEFAULT_ATTACK = KeyEvent.VK_A;
	   private static final int DEFAULT_STOP = KeyEvent.VK_S;
	   private static final int DEFAULT_FOCUS = KeyEvent.VK_F1;
	   private static final int DEFAULT_SOUND_TOGGLE = KeyEvent.VK_F4;
	   private static final int DEFAULT_MUSIC_TOGGLE = KeyEvent.VK_F3;
	   private static final int DEFAULT_SAVE = KeyEvent.VK_F5;
	   private static final int DEFAULT_SKILL_TOGGLE = KeyEvent.VK_TAB;
	   private static final int DEFAULT_FIRST_SKILL_BONUS_TOGGLE = KeyEvent.VK_1;
	   private static final int DEFAULT_SECOND_SKILL_BONUS_TOGGLE = KeyEvent.VK_2;
	   private static final int DEFAULT_THIRD_SKILL_BONUS_TOGGLE = KeyEvent.VK_3;
	   private static final int DEFAULT_ULTIMATE_BONUS_TOGGLE = KeyEvent.VK_4;
	   private static final int DEFAULT_PAUSE = KeyEvent.VK_ESCAPE;
	   private static final HashSet<Integer> RESERVED = new HashSet<Integer>(Arrays.asList(KeyEvent.VK_CONTROL,
	           KeyEvent.VK_ALT));

	   /**
	    * Constructor that will provide default macro
	    */
	   public Macro() {//Default macro
	      hotKeys = new int[NUMBER_OF_MACROS];
	      hotKeys[FIRST_SKILL_INDEX] = DEFAULT_FIRST_SKILL;
	      hotKeys[SECOND_SKILL_INDEX] = DEFAULT_SECOND_SKILL;
	      hotKeys[THIRD_SKILL_INDEX] = DEFAULT_THIRD_SKILL;
	      hotKeys[ULTIMATE_SKILL_INDEX] = DEFAULT_ULTIMATE_SKILL;
	      hotKeys[BLINK_INDEX] = DEFAULT_BLINK_SKILL;
	      hotKeys[ATTACK_INDEX] = DEFAULT_ATTACK;
	      hotKeys[STOP_INDEX] = DEFAULT_STOP;
	      hotKeys[FOCUS_INDEX] = DEFAULT_FOCUS;
	      hotKeys[SOUND_TOGGLE_INDEX] = DEFAULT_SOUND_TOGGLE;
	      hotKeys[MUSIC_TOGGLE_INDEX] = DEFAULT_MUSIC_TOGGLE;
	      hotKeys[SAVE_INDEX] = DEFAULT_SAVE;
	      hotKeys[SKILL_TOGGLE_INDEX] = DEFAULT_SKILL_TOGGLE;
	      hotKeys[FIRST_SKILL_BONUS_TOGGLE_INDEX] = DEFAULT_FIRST_SKILL_BONUS_TOGGLE;
	      hotKeys[SECOND_SKILL_BONUS_TOGGLE_INDEX] = DEFAULT_SECOND_SKILL_BONUS_TOGGLE;
	      hotKeys[THIRD_SKILL_BONUS_TOGGLE_INDEX] = DEFAULT_THIRD_SKILL_BONUS_TOGGLE;
	      hotKeys[ULTIMATE_SKILL_BONUS_TOGGLE_INDEX] = DEFAULT_ULTIMATE_BONUS_TOGGLE;
	      hotKeys[PAUSE_INDEX] = DEFAULT_PAUSE;
	   }

	   /**
	    * 
	    * @return current hotkeys in the macro configuration
	    */
	   public int[] hotKeys() {
	      return hotKeys;
	   }

	   /**
	    * @return set of reserved keys
	    */
	   public static HashSet<Integer> RESERVED() {
	      return new HashSet<Integer>(RESERVED);
	   }
	}