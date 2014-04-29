package Levels.Tutorials;

import Features.Macros;
import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Creep;
import Main.Units.NonLiving.DirectionMarker;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.event.KeyEvent;

public class BasicTutorial extends Tutorial {

   DirectionMarker marker;
   private static final Pointt FIRST_MOVE_POSITION = new Pointt(230, 270);
   private static final Pointt SECOND_MOVE_POSITION = new Pointt(1000, 500);

   @Override
   protected boolean taskAccomplished(Game game, int stage) {
      if (stage <= 2) {
         return true;
      } else if (stage <= 3) {
         return game.puck().distance(marker) < marker.radius();
      } else if (stage <= 4) {
         return true;
      } else if (stage <= 5) {
         return game.puck().distance(marker) < marker.radius();
      } else if (stage <= 8) {
         return true;
      } else if (stage <= 10) {
         return game.enemies().isEmpty();
      } else if (stage <= 11) {
         return true;
      } else if (stage <= 12) {
         return game.puck().blinked();
      } else if (stage <= 14) {
         return true;
      } else if (stage <= 15) {
         return game.enemies().isEmpty() && game.puck().usedFirstSkill();
      } else if (stage <= 16) {
         return game.enemies().isEmpty() && game.puck().usedSecondSkill();
      } else if (stage <= 17) {
         return game.enemies().isEmpty() && game.puck().usedThirdSkill();
      } else if (stage <= 18) {
         return game.enemies().isEmpty() && game.puck().usedUltimate();
      } else if (stage <= 23) {
         return true;
      } else {
         return true;
      }
   }

   @Override
   protected void addEnemies(Game game, int stage) {
      if (stage <= 2) {
         game.infoMessage().setContent("You must have no idea how to play so you are playing this tutorial.");
         game.puck().setSkillEnable(false);
         game.puck().setMoveable(false);
      } else if (stage <= 3) {
         game.puck().setMoveable(true);
         marker = new DirectionMarker(FIRST_MOVE_POSITION.clone());

         game.infoMessage().setContent("The very first thing to do is to move around. Right click "
                 + " on a position on screen to move to that position. Now move to the blue circle on screen");
         synchronized (game.markers()) {
            game.markers().add(marker);
         }
      } else if (stage <= 4) {
         game.infoMessage().setContent("Notice the minimap on the bottom left corner of the screen. It shows "
                 + " your relative position, and your enemies (if there are any) positions.");
      } else if (stage <= 5) {
         marker.setPosition(SECOND_MOVE_POSITION.clone());
         game.infoMessage().setContent("Use the minimap (drag your mouse on it) to find the blue circle.");
      } else if (stage <= 6) {
         game.infoMessage().setContent("See the black square on the minimap? It's the Main Building that you have to protect.");
      } else if (stage <= 7) {
         marker.setPosition(game.mainBuilding().position());
         game.infoMessage().setContent("Now move into the main building");
      } else if (stage <= 8) {
         game.infoMessage().setContent("Your mission is to protect the main building. Enemies will only attack the main building after you die.");
      } else if (stage <= 9) {
         game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.infoMessage().setContent("An enemy has just been spawned. You can right click on the enemy to kill it.");
      } else if (stage <= 10) {
         game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.infoMessage().setContent("Try again. This time try to dodge the enemy's projectile.");
      } else if (stage <= 11) {
         game.infoMessage().setContent("You can use your blink movement to dodge projectiles better.");
      } else if (stage <= 12) {
         game.infoMessage().setContent("Press " + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.BLINK_INDEX])
                 + " and then click anywhere to move to that position instantly.");
      } else if (stage <= 13) {
         game.infoMessage().setContent("Shooting alone is not enough. In fact it's the weakest type of attack in this game");
      } else if (stage <= 14) {
         game.infoMessage().setContent("You have four helpful skills that you can use to kill enemies.");
      } else if (stage <= 15) {
         game.puck().setSkillEnable(true);
         game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.infoMessage().setContent("To use the first skill, press "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.FIRST_SKILL_INDEX])
                 + " and then click on the position that you want to cast skill.");
      } else if (stage <= 16) {
         game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.infoMessage().setContent("To use the second skill, press "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.SECOND_SKILL_INDEX]));
      } else if (stage <= 17) {
         game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.infoMessage().setContent("To use the third skill, press "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.THIRD_SKILL_INDEX]));
      } else if (stage <= 18) {
         game.enemies().add(new Creep(game, spawnPos[0], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.infoMessage().setContent("To use the ultimate, press "
                 + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.ULTIMATE_SKILL_INDEX])
                 + " and then click on the position that you want to cast skill.");
      } else if (stage <= 19) {
         game.infoMessage().setContent("You can save the game at any time. (Even when you already lost). A new save file"
                 + "will appear in the game directory.");
      } else if (stage <= 20) {
         game.infoMessage().setContent("Pressing " + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.SAVE_INDEX])
                 + " will allow you to go back at the beginning of the level once save file is loaded.");
      } else if (stage <= 21) {
         game.infoMessage().setContent("Also, press " + KeyEvent.getKeyText(game.macros().hotKeys()[Macros.PAUSE_INDEX])
                 + " to pause the game at any time.");
      } else if (stage <= 22) {
         game.infoMessage().setContent("Now you can start playing the game. But make sure to try out the advanced tutorial"
                 + "for more information.");
      } else if (stage <= 23) {
         game.infoMessage().setContent("Good luck and have fun.");
      } else {
         ProcessingUnit.setLockFocus(ProcessingUnit.WIN_LOCK());
      }
   }

   @Override
   protected void createEnemies(Game game, int stage, int i, int j) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
