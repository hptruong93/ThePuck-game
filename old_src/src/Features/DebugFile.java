package Features;

import Utilities.FileUtilities;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DebugFile {

   public static final File file = new File("Log.txt");
   private static final SimpleDateFormat DEFAULT_TIME = new SimpleDateFormat("HH : mm : ss");
   private static final SimpleDateFormat DEFAULT_DATE = new SimpleDateFormat("dd - MMMM - yyyy", Locale.ENGLISH);
   public static final int EXIT_SUCCESS = 0;
   public static final int EXIT_FAIL = 1;

   public static void initialize() {//Should be the very first thing to start with
      file.setWritable(true);
   }

   public static void printLog(Throwable e) {
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      e.printStackTrace(printWriter);
      StringBuffer log = new StringBuffer();

      Calendar now = Calendar.getInstance();

      log.append(DEFAULT_DATE.format(new Date(now.getTimeInMillis())));
      log.append("\n");
      log.append(DEFAULT_TIME.format(new Date(now.getTimeInMillis())));
      log.append("\n");
      log.append(result.toString());
      log.append("\n").append("\n").append("\n");

      FileUtilities.writeToFile(log, file, true);
   }
}
