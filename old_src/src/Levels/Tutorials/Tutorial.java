package Levels.Tutorials;

import Levels.GameLevel;
import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Creep;
import Utilities.Maths;
import java.util.ArrayList;

public abstract class Tutorial extends GameLevel {

   private final ArrayList<String> messages;

   Tutorial() {
      messages = new ArrayList<>();
   }

   @Override
   protected final boolean createEnemies(Game game, int stage) {
      if (!taskAccomplished(game, stage - 1)) {
         return false;
      } else {
         addEnemies(game, stage);
         return true;
      }
   }

   protected abstract boolean taskAccomplished(Game game, int stage);

   protected abstract void addEnemies(Game game, int stage);

   protected ArrayList<String> messages() {
      return messages;
   }
}
