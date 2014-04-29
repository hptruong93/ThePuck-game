package Utilities;

import Features.DebugFile;
import Features.SavePackage;
import Main.Game;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtilities {

   private static final SimpleDateFormat DEFAULT_TIME = new SimpleDateFormat("HH-mm-ss");
   private static final SimpleDateFormat DEFAULT_DATE = new SimpleDateFormat("dd - MMMM - yyyy", Locale.ENGLISH);

   public static ArrayList<Color> getGradient(String location) {
      ArrayList<Color> output = new ArrayList<>();
      FileReader reader = null;
      try {
         File file = new File(location);
         reader = new FileReader(file);
         BufferedReader br = new BufferedReader(reader);

         String in;
         int i = 0;
         while (true) {
            in = br.readLine();
            if (in.equals("end")) {
               break;
            }
            output.add(new Color(Integer.parseInt(in, 16)));
            i++;
         }
      } catch (IOException ex) {
         Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
         DebugFile.printLog(ex);
         System.exit(0);
      } finally {
         try {
            reader.close();
            return output;
         } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            DebugFile.printLog(ex);
            System.exit(0);
         }
      }
      throw new RuntimeException("Cannot retrieve Color Gradient from " + location);
   }

   public static void writeToFile(StringBuffer content, String fileName) {
      FileWriter fr = null;
      try {
         String name;
         if (fileName == null || "".equals(fileName)) {
            Calendar now = Calendar.getInstance();
            name = "";
            name = name.concat(DEFAULT_TIME.format(new Date(now.getTimeInMillis())));
            name = name.concat("-");
            name = name.concat(DEFAULT_DATE.format(new Date(now.getTimeInMillis())));
            name = name.concat(".ThePuckGame");
         } else {
            name = fileName;
         }
         File file = new File(name);
         file.createNewFile();
         fr = new FileWriter(file);
         BufferedWriter br = new BufferedWriter(fr);
         br.write(new String(content));
         br.flush();
      } catch (IOException ex) {
         Logger.getLogger(SavePackage.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         try {
            fr.close();
         } catch (IOException ex) {
            Logger.getLogger(SavePackage.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public static void writeToFile(StringBuffer content, File file, boolean append) {
      FileWriter fr = null;
      try {
         fr = new FileWriter(file, append);

         BufferedWriter br = new BufferedWriter(fr);
         br.write(new String(content));
         br.flush();
      } catch (IOException ex) {
         Logger.getLogger(SavePackage.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         try {
            fr.close();
         } catch (IOException ex) {
            Logger.getLogger(SavePackage.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
}