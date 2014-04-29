package Levels;

import Levels.Tutorials.AdvancedTutorial;
import Levels.Tutorials.BasicTutorial;
import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Boss.Ryskor.Ryskor;
import Main.Units.Living.Puck.FirstSkill;
import Main.Units.Living.Puck.Puck;
import Utilities.Pointt;
import java.util.Arrays;
import java.util.HashSet;

public abstract class GameLevel {

   public static final byte FOR_KIDS = 0;
   public static final byte NEWBIE = 1;
   public static final byte NORMAL = 2;
   public static final byte EXPERT = 3;
   public static final byte BETTER_HAVE_GOOD_PROCESSOR = 4;
   public static final byte BASIC_TUTORIAL = 5;
   public static final byte ADVANCED_TUTORIAL = 6;
   public static final int[] MAX_HEALTH = {15000, 10000, 5000, 3000, 2000, 2147483647, 5000};
   public static final int[] FIRST_SKILL_INITIAL_DAMAGE = {300, 200, 150, 60, 60, 300, 60};
   public static final int[][] RUN_MODES = {{3, 3}, {5, 4}, {8, 5}, {10, 6}, {10, 10}, {2,2}, {2,2}};
   public static final HashSet<Byte> GAME_LEVELS = new HashSet(Arrays.asList(FOR_KIDS, NEWBIE, NORMAL, EXPERT, BETTER_HAVE_GOOD_PROCESSOR));
   public static final GameLevel[] LEVELS = new GameLevel[7];
   protected static final Pointt CHECK = new Pointt(30, 30);
   protected Pointt[] spawnPos;

   public static void initialize() {
      LEVELS[FOR_KIDS] = new ForKids();
      LEVELS[NEWBIE] = new NewBieLevel();
      LEVELS[NORMAL] = new NormalLevel();
      LEVELS[EXPERT] = new ExpertLevel();
      LEVELS[BETTER_HAVE_GOOD_PROCESSOR] = new InsaneLevel();
      LEVELS[BASIC_TUTORIAL] = new BasicTutorial();
      LEVELS[ADVANCED_TUTORIAL] = new AdvancedTutorial();
   }

   public GameLevel() {
      spawnPos = new Pointt[2];
      spawnPos[0] = new Pointt(0, 0);
      spawnPos[1] = new Pointt(0, 0);
   }

   public static boolean generateEnemies(Game game, int stage) {
      return LEVELS[game.difficulty()].createEnemies(game, stage);
   }

   public static void setDifficulty(byte difficulty) {
      if (ProcessingUnit.TESTING) {
         Puck.setInitialMaxHealth(2147483647);
         Puck.setInitialHealth(2147483647);
         FirstSkill.setInitialDamage(6000);
      } else {
         Puck.setInitialMaxHealth(MAX_HEALTH[difficulty]);
         Puck.setInitialHealth(MAX_HEALTH[difficulty]);
         FirstSkill.setInitialDamage(FIRST_SKILL_INITIAL_DAMAGE[difficulty]);
      }
      Game.map.setRunMode(difficulty);
   }

   protected boolean createEnemies(Game game, int stage) {
      if (!game.enemies().isEmpty()) {
         return false;
      } else {
         for (int i = 1; i < Game.map.RUN_MODE().width; i++) {
            for (int j = 1; j < Game.map.RUN_MODE().height; j++) {
               spawnPos[0].setXY(GameLevel.CHECK.getX() * i, GameLevel.CHECK.getY() * j);
               spawnPos[1].setXY(Game.map.REAL_SIZEX() - GameLevel.CHECK.getX() * i, GameLevel.CHECK.getY() * j);
               if (ProcessingUnit.TESTING) {
                  int det = 0;
                  game.enemies().add(new Ryskor(game, spawnPos[det]));
               } else {
                  createEnemies(game, stage, i, j);
               }
            }
         }
         return true;
      }
   }

   protected abstract void createEnemies(Game game, int stage, int i, int j);
}