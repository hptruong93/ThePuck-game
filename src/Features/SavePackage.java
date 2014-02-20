package Features;

import Main.Game;
import Main.MainScreen;
import Main.ProcessingUnit;
import Main.Units.Living.MainBuilding;
import Main.Units.Living.Puck.FirstSkill;
import Main.Units.Living.Puck.Puck;
import Main.Units.Living.Puck.SecondSkill;
import Main.Units.Living.Puck.ThirdSkill;
import Main.Units.Living.Puck.Ultimate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class SavePackage {//Information written in this order

//    //General information - Save file variables must be in this order
//    private int stage
//    private int difficulty;
//
//    //Puck information
//    private int numberOfKills;
//    private int puckRespawnTime;
//    private int puckInitialDamage;
//    private double puckNaturalHeal;
//    private double puckMaxHealth;
//    private double puckHealth;
//    private double puckBat;
//
//    //Puck First Skill information
//    private double puckFirstSkillInitialDamage;
//
//    //Puck Second Skill information
//    private double puckSecondSkillInitialDamage;
//    private double puckSecondSkillInitialSpeed;
//
//    //Puck Third Skill information
//    private int puckThirdSkillInitialCoolDown;
//
//    //Puck Ultimate information
//    private double puckUltimateInitialRadius;
//    private double puckUltimateInitialSpeed;
//    private double puckUltimateInitialLifeSteal;
//
//    //Main Building information
//    private double mainBuildingInitialHealth;
   public static void restoreDefault() {//Assume difficulty is adjusted in the current welcome screen
      ProcessingUnit.setStage(ProcessingUnit.DEFAULT_STAGE);

      //Puck
      Puck.setInitialKill(Puck.DEFAULT_KILLS);
      Puck.setInitialRespawnTime(Puck.DEFAULT_RESPAWN_TIME);
      Puck.setInitialDamage(Puck.DEFAULT_DAMAGE);
      Puck.setInitialNaturalHeal(Puck.DEFAULT_NATURAL_HEAL);
      Puck.setInitialMaxHealth(Puck.DEFAULT_MAX_HEALTH);
      Puck.setInitialHealth(Puck.DEFAULT_MAX_HEALTH);
      Puck.setInitialAttackSpeed(Puck.DEFAULT_BAT);

      //First skill
      FirstSkill.setInitialDamage(FirstSkill.INITIAL_DAMAGE);

      //Second skill
      SecondSkill.setInitialDamage(SecondSkill.INITIAL_DAMAGE);
      SecondSkill.setInitialSpeed(SecondSkill.SPEED);

      //Third skill
      ThirdSkill.setInitialCoolDown(ThirdSkill.COOL_DOWN);

      //Ultimate
      Ultimate.setInitialRadius(Ultimate.DEFAULT_RADIUS);
      Ultimate.setInitialSpeed(Ultimate.DEFAULT_SPEED);
      Ultimate.setInitialLifeSteal(Ultimate.DEFAULT_LIFE_STEAL);

      //Main Building
      MainBuilding.setInitialMaxHealth(MainBuilding.DEFAULT_HEALTH);
   }

   public static byte loadSavePackage(File file) {//Return the difficulty in the save file
      FileReader reader = null;
      byte output = -1;
      try {
         reader = new FileReader(file);
         BufferedReader br = new BufferedReader(reader);

         String in;
         try {
            //General
            in = br.readLine();
            ProcessingUnit.setStage(Integer.parseInt(in));
            in = br.readLine();
            output = Byte.parseByte(in);

            //Puck
            in = br.readLine();
            Puck.setInitialKill(Integer.parseInt(in));
            in = br.readLine();
            Puck.setInitialRespawnTime(Integer.parseInt(in));
            in = br.readLine();
            Puck.setInitialDamage(Double.parseDouble(in));
            in = br.readLine();
            Puck.setInitialNaturalHeal(Double.parseDouble(in));
            in = br.readLine();
            Puck.setInitialMaxHealth(Double.parseDouble(in));
            in = br.readLine();
            Puck.setInitialHealth(Double.parseDouble(in));
            in = br.readLine();
            Puck.setInitialAttackSpeed(Double.parseDouble(in));

            //First skill
            in = br.readLine();
            FirstSkill.setInitialDamage(Double.parseDouble(in));

            //Second skill
            in = br.readLine();
            SecondSkill.setInitialDamage(Double.parseDouble(in));
            in = br.readLine();
            SecondSkill.setInitialSpeed(Double.parseDouble(in));

            //Third skill
            in = br.readLine();
            ThirdSkill.setInitialCoolDown(Integer.parseInt(in));

            //Ultimate
            in = br.readLine();
            Ultimate.setInitialRadius(Double.parseDouble(in));
            in = br.readLine();
            Ultimate.setInitialSpeed(Double.parseDouble(in));
            in = br.readLine();
            Ultimate.setInitialLifeSteal(Double.parseDouble(in));

            //Main Building
            in = br.readLine();
            MainBuilding.setInitialMaxHealth(Double.parseDouble(in));

            //Checking
            in = br.readLine();
            if (!in.equals("end")) {
               JOptionPane.showMessageDialog(null, "Invalid save file!", "Warning", JOptionPane.WARNING_MESSAGE);
               return MainScreen.DEFAULT_DIFFICULTY;
            }
         } catch (Exception e) {
            DebugFile.printLog(e);
            JOptionPane.showMessageDialog(null, "Error while loading file!", "Error", JOptionPane.WARNING_MESSAGE);
         }
      } catch (IOException ex) {
         Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
         DebugFile.printLog(ex);

      } finally {
         try {
            reader.close();
            return output;
         } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            DebugFile.printLog(ex);
            System.exit(DebugFile.EXIT_FAIL);
         }
      }

      JOptionPane.showMessageDialog(null, "Cannot retrieve Color Gradient from " + file.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
      return MainScreen.DEFAULT_DIFFICULTY;
   }

   public static StringBuffer save(Game game, StringBuffer toBeWritten) {
      toBeWritten.delete(0, toBeWritten.length());
      String line = "\n";
      //General
      toBeWritten.append(ProcessingUnit.stage()).append(line);
      toBeWritten.append(game.difficulty()).append(line);

      //Puck
      toBeWritten.append(game.puck().numberOfKills()).append(line);
      toBeWritten.append(game.puck().respawnTime()).append(line);
      toBeWritten.append((int) game.puck().damage()).append(line);
      toBeWritten.append(game.puck().heal()).append(line);
      toBeWritten.append(game.puck().maxHealth()).append(line);
      toBeWritten.append(game.puck().health()).append(line);
      toBeWritten.append(game.puck().attackSpeed()).append(line);


      //First skill
      toBeWritten.append(FirstSkill.initialDamage()).append(line);

      //Second skill
      toBeWritten.append(game.puck().secondSkill().damage()).append(line);
      toBeWritten.append(game.puck().secondSkill().speed()).append(line);

      //Third skill
      toBeWritten.append(game.puck().thirdSkill().coolDown()).append(line);

      //Ultimate
      toBeWritten.append(game.puck().ultimate().radius()).append(line);
      toBeWritten.append(game.puck().ultimate().speed()).append(line);
      toBeWritten.append(game.puck().ultimate().lifeSteal()).append(line);

      //Main Building
      toBeWritten.append(game.mainBuilding().maxHealth()).append(line);

      toBeWritten.append("end");
      return toBeWritten;
   }
}