package Main;

import Features.SavePackage;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.MainBuilding;
import Main.Units.Living.Puck.Puck;
import Main.Units.Living.Puck.SecondSkill;
import Main.Units.Living.Puck.Ultimate;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.Units;
import Features.Audio;
import Features.Clocks;
import Features.DebugFile;
import Levels.GameLevel;
import Main.Units.NonLiving.Marker;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class ProcessingUnit {

   private int mapTimerID;
   private final Dimension RUN_MODE;
   protected final StringBuffer saveContent;
   private static int lockFocus;
   private static int stage;
   private static final int UNLOCK = 0;
   private static final int WIN_LOCK = 1;
   private static final int GAME_OVER_LOCK = 2;
   private static final int PLAYER_SIDE = 0;
   private static final int AI_SIDE = 1;
   private final int processingRate = 100; //Millisecond
   private final int timeFrame = 23; //-- FPS: 1000/timeFrame
   private final int SCROLL_RATE = 3;
   private static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
   private final double MENU_BAR_WIDTH = 0;//20;
   private final int BORDER_WIDTH = 5;
   private final double REAL_SIZEX = 1250;
   private final double REAL_SIZEY = REAL_SIZEX / (125 / 75.0);
   private final double MINI_SIZEX = screen.width * 0.25;
   private final double MINI_SIZEY = screen.height * 0.25;
   private final double FOCUS_WIDTH = 100 * (screen.width / 1250.0);
   private final double FOCUS_HEIGHT = FOCUS_WIDTH / (5 / 3.0);
   private final double SCALE_X = REAL_SIZEX / MINI_SIZEX;
   private final double SCALE_Y = REAL_SIZEY / (MINI_SIZEY);
   private final double SCALE_MINIX = screen.width / FOCUS_WIDTH;
   private final double SCALE_MINIY = 2 * (screen.height / 3.0) / FOCUS_HEIGHT;
   private final Color MAP_COLOR = Color.GRAY;
   private final int GENERATE_RATE = 4; //Second
   private final int TUTORIAL_RATE = 6; //Second
   public static final int DEFAULT_STAGE = 0;
   public static final boolean TESTING = false;

   public ProcessingUnit() {
      saveContent = new StringBuffer();
      RUN_MODE = new Dimension(1, 1);
   }

   public void setupComponents(Game game) {
      try {
//        System.out.println(screen.width + "," + screen.height); //For graphics testing only
         game.setMainBuilding(new MainBuilding(game, new Pointt(REAL_SIZEX / 2, REAL_SIZEY - 0.5 * MainBuilding.SIZE)));
         game.setPuck(new Puck(game, new Pointt(REAL_SIZEX / 2, REAL_SIZEY / 2), 0));

         game.puck().setSecondSkill(new SecondSkill(game));
         game.puck().setUltimate(new Ultimate(game));

         game.bSecondSkill().setSkill(game.puck().secondSkill());
         game.bThirdSkill().setSkill(game.puck().thirdSkill());
         game.bUltimate().setSkill(game.puck().ultimate());

         SavePackage.save(game, saveContent);
         lockFocus = UNLOCK;
         setRunMode(game.difficulty());

         if (GameLevel.GAME_LEVELS.contains(game.difficulty())) {
            mapTimerID = Clocks.masterClock.scheduleFixedRate(new GenerateCreep(game), GENERATE_RATE, TimeUnit.SECONDS);
         } else {
            mapTimerID = Clocks.masterClock.scheduleFixedRate(new GenerateCreep(game), TUTORIAL_RATE, TimeUnit.SECONDS);
         }

         Thread.sleep(2000);
         game.setGraphicsID(Clocks.masterClock.scheduleFixedRate(new ProcessingUnit.GraphicsUnit(game), timeFrame, TimeUnit.MILLISECONDS));

         Thread.sleep(1000);
         game.setProcessorID(Clocks.masterClock.scheduleFixedRate(new ProcessingUnit.CentralProcessingUnit(game), processingRate, TimeUnit.MILLISECONDS));

         game.setFocus(game.puck().miniPosition());

         Audio.schedule();
      } catch (Exception e) {
         Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
         DebugFile.printLog(e);
         System.exit(DebugFile.EXIT_FAIL);
      }
   }

   private class GenerateCreep implements Runnable {

      private Game game;
      private Pointt[] spawnPos;
      Random random;
      private byte quitCount; //Will quit if this gets to 1

      GenerateCreep(Game game) {
         if (TESTING) {
            RUN_MODE.setSize(2, 2);
         }

         random = new Random(Clocks.masterClock.currentTime());
         this.game = game;
         spawnPos = new Pointt[2];
         spawnPos[0] = new Pointt(0, 0);
         spawnPos[1] = new Pointt(0, 0);
         quitCount = 0;
      }

      @Override
      public void run() {
         if (quitCount == 1) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               Logger.getLogger(ProcessingUnit.class.getName()).log(Level.SEVERE, null, ex);
               DebugFile.printLog(ex);
            }
            gameOver(game);
         } else if (lockFocus == GAME_OVER_LOCK || lockFocus == WIN_LOCK) {
            quitCount++;
         }


         boolean nextLevel;
         synchronized (game.enemies()) {
            nextLevel = GameLevel.generateEnemies(game, stage + 1);
         }


         if (nextLevel) {
            if (GameLevel.GAME_LEVELS.contains(game.difficulty())) {
               SavePackage.save(game, saveContent);
            }
            stage++;
         }
      }
   }

   private class CentralProcessingUnit implements Runnable {

      private Game game;

      CentralProcessingUnit(Game game) {
         this.game = game;
      }

      @Override
      synchronized public void run() {
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  //MainBuilding processing
                  game.mainBuilding().moveNoCollision(processingRate);
                  if (game.mainBuilding().dead()) {
                     game.setFocus(game.mainBuilding().miniPosition());
                  }

                  //Visual Effects processing
                  synchronized (game.visualEffects()) {
                     for (Iterator<UniversalEffect> it = game.visualEffects().iterator(); it.hasNext();) {
                        UniversalEffect current = it.next();
                        current.moveNoCollision(processingRate);
                        if (current.finish()) {
                           it.remove();
                        }
                     }
                  }

                  //Markers processing
                  synchronized (game.markers()) {
                     for (Iterator<Marker> it = game.markers().iterator(); it.hasNext();) {
                        Marker current = it.next();
                        if (!current.removeable()) {
                           current.move(processingRate, null, 1);
                        } else {
                           it.remove();
                        }

                     }
                  }

                  //Puck processing
                  ArrayList<Units> allUnits = new ArrayList<>();
                  synchronized (game.enemies()) {
                     allUnits.addAll(game.enemies());
                  }

                  game.puck().move(processingRate, allUnits, 1);

                  //Puck skills processing
                  game.puck().processSkills();

                  //PuckProjectile processing
                  game.puck().processProjectile();

                  //Enemy processing
                  allUnits.add(game.puck());

                  synchronized (game.enemies()) {
                     for (Iterator<LivingUnit> it = game.enemies().iterator(); it.hasNext();) {
                        LivingUnit current = it.next();
                        if (!current.dead()) {
                           current.move(processingRate, allUnits, 1);
                           synchronized (current.projectiles()) {
                              current.processProjectile();
                           }
                        } else {
                           it.remove();
                        }
                     }
                  }

                  game.bFirstSkill().setSkill(game.puck().firstSkill());
                  game.bFirstSkill().repaint();
                  game.bSecondSkill().repaint();
                  game.bThirdSkill().repaint();
                  game.bUltimate().repaint();
                  game.miniMap().repaint();
               } catch (NullPointerException ex) {
               } catch (Exception e) {
                  Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
                  DebugFile.printLog(e);
               }
            }
         });
      }
   }

   private class GraphicsUnit implements Runnable {

      private Game game;

      GraphicsUnit(Game game) {
         this.game = game;
      }

      @Override
      synchronized public void run() {
         //Check moveNoCollision focus
         if (MouseInfo.getPointerInfo().getLocation().getX() <= 20) {
            game.focus().setX(game.focus().getX() - SCROLL_RATE);
         } else if (MouseInfo.getPointerInfo().getLocation().getX() >= screen.width - 5) {
            game.focus().setX(game.focus().getX() + SCROLL_RATE);
         }
         if (MouseInfo.getPointerInfo().getLocation().getY() <= 20) {
            game.focus().setY(game.focus().getY() - SCROLL_RATE);
         } else if (MouseInfo.getPointerInfo().getLocation().getY() >= (screen.height - 100)) {
            game.focus().setY(game.focus().getY() + SCROLL_RATE);
         }
         game.setFocus(game.focus().clone().focus());
         try {
            game.board().repaint();
         } catch (NullPointerException ex) {
         } catch (Exception e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
            DebugFile.printLog(e);
         }
      }
   }

   protected void gameOver(Game game) {
      game.endGame();
      Audio.clearTask();
      game.welcomeScreen().endGame();
   }

   // Getter
   public int PROCESSING_RATE() {
      return processingRate;
   }

   public int SCROLL_RATE() {
      return SCROLL_RATE;
   }

   public static Dimension SCREEN() {
      return screen;
   }

   public double MENU_BAR_WIDTH() {
      return MENU_BAR_WIDTH;
   }

   public int BORDER_WIDTH() {
      return BORDER_WIDTH;
   }

   public double REAL_SIZEX() {
      return REAL_SIZEX;
   }

   public double REAL_SIZEY() {
      return REAL_SIZEY;
   }

   public double MINI_SIZEX() {
      return MINI_SIZEX;
   }

   public double MINI_SIZEY() {
      return MINI_SIZEY;
   }

   public double FOCUS_WIDTH() {
      return FOCUS_WIDTH;
   }

   public double FOCUS_HEIGHT() {
      return FOCUS_HEIGHT;
   }

   public double SCALE_X() {
      return SCALE_X;
   }

   public double SCALE_Y() {
      return SCALE_Y;
   }

   public double SCALE_MINIX() {
      return SCALE_MINIX;
   }

   public double SCALE_MINIY() {
      return SCALE_MINIY;
   }

   protected Color MAP_COLOR() {
      return MAP_COLOR;
   }

   public static int WIN_LOCK() {
      return WIN_LOCK;
   }

   public static int GAME_OVER_LOCK() {
      return GAME_OVER_LOCK;
   }

   public static int lockFocus() {
      return lockFocus;
   }

   public static void setLockFocus(int lockFocus) {
      ProcessingUnit.lockFocus = lockFocus;
   }

   public static int PLAYER_SIDE() {
      return PLAYER_SIDE;
   }

   public static int AI_SIDE() {
      return AI_SIDE;
   }

   public int TIME_FRAME() {
      return timeFrame;
   }

   public void setRunMode(byte difficulty) {
      RUN_MODE.setSize(GameLevel.RUN_MODES[difficulty][0], GameLevel.RUN_MODES[difficulty][1]);
   }

   public static int stage() {
      return stage;
   }

   public static void setStage(int stage) {
      ProcessingUnit.stage = stage;
   }

   public Dimension RUN_MODE() {
      return RUN_MODE;
   }
}