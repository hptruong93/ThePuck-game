package Levels;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Boss.Arachnardus.Arachnardus;
import Main.Units.Living.Boss.Archon.Archon;
import Main.Units.Living.Boss.DragonFly.Petaluridae;
import Main.Units.Living.Creep;
import Utilities.Maths;

public class ForKids extends GameLevel {

   @Override
   protected void createEnemies(Game game, int stage, int i, int j) {
      int det = Maths.RANDOM.nextInt(2);

      if (stage <= 2) {
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
      } else if (stage <= 5) {
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Arachnardus(game, spawnPos[det]));
      } else if (stage <= 8) {
         game.enemies().add(new Archon(game, spawnPos[det]));
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
      } else if (stage <= 11) {
         game.enemies().add(new Archon(game, spawnPos[det]));
         game.enemies().add(new Arachnardus(game, spawnPos[1 - det]));
         game.enemies().add(new Archon(game, spawnPos[det]));
      } else if (stage <= 14) {
         game.enemies().add(new Archon(game, spawnPos[det]));
         game.enemies().add(new Arachnardus(game, spawnPos[1 - det]));
         game.enemies().add(new Arachnardus(game, spawnPos[det]));
      } else if (stage <= 17) {
         game.enemies().add(new Petaluridae(game, spawnPos[det]));
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Petaluridae(game, spawnPos[1 - det]));
      } else if (stage <= 20) {
         game.enemies().add(new Petaluridae(game, spawnPos[det]));
         game.enemies().add(new Petaluridae(game, spawnPos[1 - det]));
      } else if (stage <= 23) {
         if (i % 2 == 0) {
            game.enemies().add(new Petaluridae(game, spawnPos[j % 2]));
         } else {
            game.enemies().add(new Arachnardus(game, spawnPos[det]));
         }
         game.enemies().add(new Archon(game, spawnPos[det]));
         game.enemies().add(new Arachnardus(game, spawnPos[1 - det]));
      } else if (stage <= 26) {
         if (i % 2 == 0) {
            game.enemies().add(new Petaluridae(game, spawnPos[1 - det]));
         } else {
            game.enemies().add(new Petaluridae(game, spawnPos[det]));
         }
         game.enemies().add(new Archon(game, spawnPos[det]));
         game.enemies().add(new Arachnardus(game, spawnPos[1 - det]));
      } else {
         ProcessingUnit.setLockFocus(ProcessingUnit.WIN_LOCK());
      }
   }
}
