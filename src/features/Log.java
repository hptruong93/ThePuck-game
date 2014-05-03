package features;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import utilities.FileUtility;

/**
 * Loggin feature. All print statement should go here to log to file. This is a
 * static class. No instance should be created
 * 
 * @author VDa
 * 
 */
public class Log {

	private static final SimpleDateFormat DEFAULT_TIME = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat DEFAULT_DATE = new SimpleDateFormat("dd/MM/yyyy",
			Locale.ENGLISH);
	private static final File LOG_FILE = new File("ThePuckGame.log");

	/**
	 * Private constructor to prevent creation
	 */
	private Log() {
		throw new IllegalStateException("Cannot create an instance of static class Log");
	}

	/**
	 * Write log event of an exception
	 * Convert the exception stack trace into string first.
	 * @param e exception caught
	 */
	public static void writeLog(Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		writeLog(result.toString());
	}

	/**
	 * Write content to a log file using FileUtility
	 * @param content content that will be written
	 */
	public static void writeLog(String content) {
		try {
			LOG_FILE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Calendar now = Calendar.getInstance();
		StringBuffer toWrite = new StringBuffer("");
		toWrite.append(DEFAULT_TIME.format(new Date(now.getTimeInMillis()))).append(" - ");
		toWrite.append(DEFAULT_DATE.format(new Date(now.getTimeInMillis())));
		toWrite.append("\n").append(content).append("\n\n");

		FileUtility.writeToFile(toWrite, LOG_FILE, true);
	}
}