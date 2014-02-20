package Features;

import Levels.GameLevel;
import Main.Game;
import Main.ProcessingUnit;
import Main.MainScreen;
import Main.Units.Living.Boss.Arachnardus.Arachnardus;
import Main.Units.Living.Boss.Archon.Archon;
import Main.Units.Living.Boss.DragonFly.Petaluridae;
import Main.Units.Living.Boss.Ryskor.Ryskor;
import Main.Units.Living.Boss.Sarcophagidae.Sarcophagidae;
import Main.Units.Living.Creep;
import Main.Units.Living.MainBuilding;
import Main.Units.Living.Puck.Puck;
import Main.Units.NonLiving.Marker;
import javax.swing.JProgressBar;

public class Initialize extends Thread {

   private JProgressBar loading;
   private static boolean alreadyInitialized;
   private final MainScreen caller;
   public static int[] testInit = new int[9];
   public static final byte AUDIO = 0;
   public static final byte MAIN_BUILDING = 1;
   public static final byte PUCK = 2;
   public static final byte CREEP = 3;
   public static final byte ARCHON = 4;
   public static final byte ARACHNARDUS = 5;
   public static final byte PETALURIDAE = 6;
   public static final byte RYSKOR = 7;
   public static final byte VIEYKEL = 8;

   public Initialize(JProgressBar loading, MainScreen caller) {
      this.loading = loading;
      this.caller = caller;
   }

   @Override
   public void run() {
      synchronized (caller) {
         generalInitialize();
      }
   }

   public void generalInitialize() {
      if (ProcessingUnit.TESTING) {
         Initialize.testInit[Initialize.AUDIO] = 1;
         Initialize.testInit[Initialize.MAIN_BUILDING] = 1;
         Initialize.testInit[Initialize.PUCK] = 1;
         Initialize.testInit[Initialize.CREEP] = 1;
         Initialize.testInit[Initialize.ARCHON] = 0;
         Initialize.testInit[Initialize.ARACHNARDUS] = 0;
         Initialize.testInit[Initialize.PETALURIDAE] = 1;
         Initialize.testInit[Initialize.RYSKOR] = 1;
         Initialize.testInit[Initialize.VIEYKEL] = 1;
      } else {
         for (int i = 0; i < 9; i++) {
            Initialize.testInit[i] = 1;
         }
      }



      if (!alreadyInitialized) {
         long star = Clocks.masterClock.currentTime();

         loading.setString("0% - Loading Audio");
         loading.setValue(0);
         GameLevel.initialize();

         loading.setString("9% - Loading Audio");
         loading.setValue(9);
         Marker.initialize();

         loading.setString("18% - Loading Audio");
         loading.setValue(18);
         if (testInit[0] == 1) {
            Audio.initialize();
         }

         loading.setString("27% - Initializing Main Building");
         loading.setValue(27);
         if (testInit[1] == 1) {
            MainBuilding.initialize();
         }

         loading.setString("36% - Initializing Puck");
         loading.setValue(36);
         if (testInit[2] == 1) {
            Puck.initialize();
         }

         loading.setString("45% - Initializing Creep");
         loading.setValue(45);
         if (testInit[3] == 1) {
            Creep.initialize();
         }

         loading.setString("54% - Initializing Archon");
         loading.setValue(54);
         if (testInit[4] == 1) {
            Archon.initialize();
         }

         loading.setString("63% - Initializing Arachnardus");
         loading.setValue(63);
         if (testInit[5] == 1) {
            Arachnardus.initialize();
         }

         loading.setString("72% - Initializing Petaluridae");
         loading.setValue(72);
         if (testInit[6] == 1) {
            Petaluridae.initialize();
         }

         loading.setString("81% - Initializing Ryskor");
         loading.setValue(81);
         if (testInit[7] == 1) {
            Ryskor.initialize();
         }

         loading.setString("90% - Initializing Vieykel");
         loading.setValue(90);
         if (testInit[8] == 1) {
            Sarcophagidae.initialize();
         }


         loading.setString("Initialization Completed");
         loading.setValue(100);

         System.out.println(Clocks.masterClock.currentTime() - star);
         alreadyInitialized = true;
      } else {
         loading.setString("Initialization Completed");
         loading.setValue(100);
      }

      if (caller.game() == null) {
         caller.setGame(new Game(caller));
      }

      caller.game().setMacros(caller.macros());
      Game.map.setupComponents(caller.game());
      caller.game().setVisible(true);

      caller.setVisible(false);
   }
}