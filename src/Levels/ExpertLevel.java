package Levels;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Boss.Arachnardus.Arachnardus;
import Main.Units.Living.Boss.Archon.Archon;
import Main.Units.Living.Boss.DragonFly.Eadirulatep;
import Main.Units.Living.Boss.DragonFly.Petaluridae;
import Main.Units.Living.Boss.Ryskor.Ryskor;
import Main.Units.Living.Boss.Sarcophagidae.Sarcophagidae;
import Main.Units.Living.Creep;
import Utilities.Maths;

public class ExpertLevel extends GameLevel {

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
      } else if (stage <= 28) {
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
      } else if (stage <= 29) {
         if (i == 1 && j == 1) {
            game.enemies().add(new Eadirulatep(game, spawnPos[det]));
         }
      } else if (stage <= 30) {
         if (i * j == 1) {
            game.enemies().add(new Eadirulatep(game, spawnPos[det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[1 - det]));
         }
      } else if (stage <= 31) {
         if (i * j == 1) {
            game.enemies().add(new Eadirulatep(game, spawnPos[det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[1 - det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[1 - det]));
         }
      } else if (stage <= 33) {
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
         game.enemies().add(new Creep(game, spawnPos[1 - det], Maths.RANDOM.nextInt(Creep.NUMBER_OF_TYPES)));
      } else if (stage <= 34) {
         if (i * j < 2) {
            game.enemies().add(new Eadirulatep(game, spawnPos[det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[1 - det]));
            game.enemies().add(new Eadirulatep(game, spawnPos[1 - det]));
         }
      } else if (stage <= 35) {
         if (i * j < 2) {
            game.enemies().add(new Sarcophagidae(game, spawnPos[det]));
         }
      } else if (stage <= 36) {
         if (i * j < 2) {
            game.enemies().add(new Sarcophagidae(game, spawnPos[det]));
            game.enemies().add(new Sarcophagidae(game, spawnPos[1 - det]));
         }
      } else if (stage <= 37) {
         if (i * j < 2) {
            game.enemies().add(new Sarcophagidae(game, spawnPos[det]));
            game.enemies().add(new Sarcophagidae(game, spawnPos[det]));
            game.enemies().add(new Sarcophagidae(game, spawnPos[1 - det]));
            game.enemies().add(new Sarcophagidae(game, spawnPos[1 - det]));
         }
      } else if (stage <= 38) {
         if (i == 1 && j == 1) {
            game.enemies().add(new Ryskor(game, spawnPos[det]));
         }
      } else if (stage <= 39) {
         if (i == 1 && j == 1) {
            game.enemies().add(new Ryskor(game, spawnPos[det]));
            game.enemies().add(new Ryskor(game, spawnPos[1 - det]));
         }
      } else if (stage <= 40) {
         if (i * j < 1) {
            game.enemies().add(new Ryskor(game, spawnPos[det]));
            game.enemies().add(new Ryskor(game, spawnPos[det]));
            game.enemies().add(new Ryskor(game, spawnPos[1 - det]));
            game.enemies().add(new Ryskor(game, spawnPos[1 - det]));
         }
      } else if (stage <= 41) {
         if (i * j < 3) {
            game.enemies().add(new Ryskor(game, spawnPos[det]));
            game.enemies().add(new Ryskor(game, spawnPos[1 - det]));
         }
      } else {
         ProcessingUnit.setLockFocus(ProcessingUnit.WIN_LOCK());
      }
   }
}