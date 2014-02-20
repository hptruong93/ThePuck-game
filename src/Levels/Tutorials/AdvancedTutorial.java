package Levels.Tutorials;

import Features.Macros;
import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Creep;
import Main.Units.NonLiving.DirectionMarker;
import Main.Units.Units;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.event.KeyEvent;

public class AdvancedTutorial extends Tutorial {

   DirectionMarker marker;
   private static final Pointt FIRST_MOVE_POSITION = new Pointt(230, 270);
   private static final Pointt SECOND_MOVE_POSITION = new Pointt(1000, 500);

   @Override
   protected boolean taskAccomplished(Game game, int stage) {
      if (stage <= 9) {
         return true;
      } else if (stage <= 10) {
         return game.enemies().isEmpty();
      } else if (stage <= 12) {
         return true;
      } else if (stage <= 13) {
         return game.enemies().isEmpty();
      } else if (stage <= 16) {
         return true;
      } else if (stage <= 17) {
         return game.enemies().isEmpty();
      } else if (stage <= 18) {
         return true;
      } else if (stage <= 20) {
         return game.enemies().isEmpty();
      } else if (stage <= 31) {
         return true;
      } else if (stage <= 32) {
         return game.enemies().isEmpty();
      }  else {
         return true;
      }
   }

   @Override
   protected void addEnemies(Game game, int stage) {
      if (stage <= 2) {
         game.infoMessage().setContent("Hi! Make sure to try out the basic tutorial before attempting this one.");
      } else if (stage <= 3) {
         game.infoMessage().setContent("Now moving with your second skill is awesome. But you may lose sight of your character");
      } else if (stage <= 4) {
         game.infoMessage().setContent("To instantly relocate the screen focus on the character, press "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.FOCUS_INDEX]));
      } else if (stage <= 5) {
         game.infoMessage().setContent("The point of the game is to move around without getting hit by enemies.");
      } else if (stage <= 6) {
         game.infoMessage().setContent("Now I'll try to explain the mechanics behind each skill.");
      } else if (stage <= 7) {
         game.infoMessage().setContent("Your first skill propagates as time passes by...");
      } else if (stage <= 8) {
         game.infoMessage().setContent("... Notice that the wave stays longer near the end of the skill.");
      } else if (stage <= 9) {
         game.infoMessage().setContent("This means that the skill will deal a lot of damage at its end.");
      } else if (stage <= 10) {
         Creep test = new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES));
         test.setMaxHealth(5000);
         test.setHealth(5000, Units.FORCE_CHANGE);
         game.enemies().add(test);

         game.infoMessage().setContent("Try to apply this and see the effect.");
      } else if (stage <= 11) {
         game.infoMessage().setContent("Your second skill does not make you invulnerable (you still take"
                 + " damages during the travel time...");
      } else if (stage <= 12) {
         game.infoMessage().setContent("However, it eliminates any projectile on your way.");
      } else if (stage <= 13) {
         Creep test = new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES));
         test.setMaxHealth(5000);
         test.setHealth(5000, Units.FORCE_CHANGE);
         game.enemies().add(test);

         game.infoMessage().setContent("Try now and see the effect. Kill the enemy when you're done.");
      } else if (stage <= 14) {
         game.infoMessage().setContent("Your third skill grants you invulnerability. Use this to dodge "
                 + "anything that you can think of.");
      } else if (stage <= 15) {
         game.infoMessage().setContent("Once activated, the skill will lose its effect if you move or blink away.");
      } else if (stage <= 16) {
         game.infoMessage().setContent("Finally, your ultimate. It pulls all enemies into the center of the circle.");
      } else if (stage <= 17) {
         Creep test = new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES));
         test.setMaxHealth(5000);
         test.setHealth(5000, Units.FORCE_CHANGE);
         game.enemies().add(test);

         game.infoMessage().setContent("Try it now! Also, your ultimate also deals damage on your enemy base on its"
                 + "max health (percentage damage) and heal you by a small lifesteal amount.");
      } else if (stage <= 18) {
         game.infoMessage().setContent("By now, you should have realized that you can combine your first and last skill.");
      } else if (stage <= 19) {
         for (int i = 0; i < 10; i++) {
            game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         }
         game.infoMessage().setContent("Try cast your first skill, and use your last skill to pull enemies into first skill wave end.");
      } else if (stage <= 20) {
         for (int i = 0; i < 10; i++) {
            game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         }
         game.infoMessage().setContent("Again, for practicing...");
      } else if (stage <= 21) {
         game.infoMessage().setContent("Notice your health under your name : The Puck");
      } else if (stage <= 22) {
         game.infoMessage().setContent("If your health goes below 0, you die (obviously)");
      } else if (stage <= 23) {
         game.infoMessage().setContent("When you kill and enemy with a skill, you will get bonus for that skill.");
      } else if (stage <= 24) {
         game.infoMessage().setContent("Specifically, it's the option under each skill icon.");
      } else if (stage <= 25) {
         game.infoMessage().setContent("For example, killing an enemy with first skill will increase first skill damage.");
      } else if (stage <= 26) {
         game.infoMessage().setContent("You can choose which skill bonus to get at any time.");
      } else if (stage <= 27) {
         game.infoMessage().setContent("Each skill will have a hotkey to switch between types of bonuses.");
      } else if (stage <= 28) {
         game.infoMessage().setContent("Second skill bonus switch hotkey is "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.SECOND_SKILL_BONUS_TOGGLE_INDEX]));
      } else if (stage <= 29) {
         game.infoMessage().setContent("Ultimate skill bonus switch hotkey is "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.ULTIMATE_SKILL_BONUS_TOGGLE_INDEX]));
      } else if (stage <= 30) {
         game.infoMessage().setContent("Also you can choose to gain health bonus instead of skill bonus.");
      } else if (stage <= 31) {
         game.infoMessage().setContent("This means your health pool will grow after each of your kill.");
      } else if (stage <= 32) {
         for (int i = 0; i < 10; i++) {
            game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         }
         game.infoMessage().setContent("Try switching to health bonus now, kill an enemy and see the effect.");
      } else if (stage <= 33) {
         game.infoMessage().setContent("Use hotkey "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.SKILL_TOGGLE_INDEX]) + " to switch between "
                 + "health bonus and skill bonus.");
      } else if (stage <= 34) {
         game.infoMessage().setContent("That's it for the tutorial. With these skills, you can play the normal mode already.");
      } else if (stage <= 35) {
         game.infoMessage().setContent("Good luck have fun playing...");
      } else {
         ProcessingUnit.setLockFocus(ProcessingUnit.WIN_LOCK());
      }
   }

   @Override
   protected void createEnemies(Game game, int stage, int i, int j) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
