package features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Clock interface to control the game threads. All threads must be submitted to the MASTER_CLOCK.
 * Use the MASTER_CLOCK to access the interface.
 * This class allows more control over threads running in the game.
 * @author VDa
 *
 */
public class Clock implements Runnable {

	   public static final Clock MASTER_CLOCK = new Clock();
	   private long time;
	   private ScheduledThreadPoolExecutor mainClock;
	   private static int IDCounter;
	   private final ScheduledThreadPoolExecutor scheduler;
	   private final HashMap<Integer, ScheduledTasks> scheduledTasks;
	   private final LinkedList<WaitingTask> waitingTasks;
	   private static final long START_TIME = System.currentTimeMillis();
	   private static final int INCREMENT = 50;
	   public static final int DEFAULT_INTERVAL = 50;
	   private static final int STARTING_ID = 10;
	   private final int interval;

	   /**
	    * Private constructor with default interval
	    * This initializes components of the interface
	    */
	   private Clock() {
	      time = START_TIME;
	      this.interval = DEFAULT_INTERVAL;
	      scheduler = new ScheduledThreadPoolExecutor(50);
	      mainClock = new ScheduledThreadPoolExecutor(1);
	      waitingTasks = new LinkedList<>();
	      scheduledTasks = new HashMap<>();
	   }

	   /**
	    * Private constructor with interval
	    * This initializes components of the interface
	    * @param interval interval at which the main clock will be run
	    */
	   private Clock(int interval) {
	      time = START_TIME;
	      this.interval = interval;
	      scheduler = new ScheduledThreadPoolExecutor(50);
	      mainClock = new ScheduledThreadPoolExecutor(1);
	      waitingTasks = new LinkedList<>();
	      scheduledTasks = new HashMap<>();
	   }

	   /**
	    * Increase time count.
	    * Then check if there is any task waiting to be run at this new time
	    */
	   @Override
	   synchronized public void run() {
	      timerTick();
	      runWaitingTasks();
	   }

	   /**
	    * Start the clock by start the main clock itself, then
	    * scheduling all scheduled tasks.
	    */
	   synchronized public void start() {
	      mainClock.scheduleAtFixedRate(this, 0, interval, TimeUnit.MILLISECONDS);
	      for (ScheduledTasks current : scheduledTasks.values()) {
	         current.schedule(scheduler);
	      }
	   }

	   /**
	    * Stop the clock by stopping the mainClock first.
	    * Then all scheduled tasks are stopped
	    */
	   synchronized public void stop() {
	      mainClock.shutdown();
	      mainClock = new ScheduledThreadPoolExecutor(1);
	      for (ScheduledTasks current : scheduledTasks.values()) {
	         current.cancel(true);
	      }
	   }

	   /**
	    * The main clock ticks. This simulates real time
	    */
	   synchronized private void timerTick() {
	      if (time < Long.MAX_VALUE - INCREMENT) {
	         time += INCREMENT;
	      } else {
	    	 time = 0;
	         Log.writeLog(new RuntimeException("Counter overflow"));
	      }
	   }

	   /**
	    * Check if it is time to run a certain waiting task.
	    * i.e. check that the current counter exceeds the time that
	    * the task is to be run
	    */
	   synchronized private void runWaitingTasks() {
	      for (Iterator<WaitingTask> it = waitingTasks.iterator(); it.hasNext();) {
	         WaitingTask current = it.next();
	         if (current.startTime <= time) {
	            scheduler.schedule(current.task, 0, TimeUnit.DAYS);
	            it.remove();
	         } else {
	            break;
	         }
	      }
	   }

	   /**
	    * Remove a task from list of scheduled tasks. The scheduled task will be stopped,
	    * but will not be interrupted if running.
	    * @param id id of the task provided previously by the Clock interface
	    */
	   synchronized public void removeScheduledTask(int id) {//Remove a task that has been scheduled periodically
	      if (scheduledTasks.containsKey(id)) {
	         scheduledTasks.get(id).cancel(false);
	         scheduledTasks.remove(id);
	      }
	   }

	   /**
	    * Schedule a task at fixed rate. i.e. task will be run every period of time
	    * @param task the task that will be scheduled
	    * @param rate rate at which task will be run
	    * @param unit time unit applied to the rate
	    * @return the id of the scheduled task in the clock
	    */
	   synchronized public int scheduleFixedRate(Runnable task, int rate, TimeUnit unit) {
	      ScheduledFuture<?> value = scheduler.scheduleAtFixedRate(task, 0, rate, unit);
	      ScheduledFixedRateTask newComer = new ScheduledFixedRateTask(task, value, rate, unit);
	      int output = nextID();
	      scheduledTasks.put(output, newComer);
	      return output;
	   }

	   /**
	    * Schedule a task at fixed delay. i.e. the task will be run, and repeated after a fixed delay
	    * once it has completed 
	    * @param task the task that will be scheduled
	    * @param rate rate at which task will be run
	    * @param unit time unit applied to the rate
	    * @return the id of the scheduled task in the clock
	    */
	   synchronized public int scheduleFixedDelay(Runnable task, int delay, TimeUnit unit) {
	      ScheduledFuture<?> value = scheduler.scheduleWithFixedDelay(task, 0, delay, unit);
	      ScheduledFixedDelayTask newComer = new ScheduledFixedDelayTask(task, value, delay, unit);
	      int output = nextID();
	      scheduledTasks.put(output, newComer);
	      return output;
	   }

	   /**
	    * Schedule a task to run once in the future. The task will be added to the waiting queue
	    * @param task task that will be run
	    * @param delay delay in milliseconds for the task
	    * @warning task submitted to the clock through this interface cannot be canceled
	    */
	   synchronized public void scheduleOnce(Runnable task, long delay) {//Delay in milliseconds
	      int insert = -1;

	      synchronized (waitingTasks) {
	         for (WaitingTask current : waitingTasks) {//Ascending order
	            if (current.startTime > delay + time) {
	               insert = waitingTasks.indexOf(current);
	               break;
	            }
	         }

	         if (insert == -1) {
	            waitingTasks.add(new WaitingTask(task, delay + time));
	         } else {
	            waitingTasks.add(insert, new WaitingTask(task, delay + time));
	         }
	      }
	   }

	   /**
	    * Reset the clock by stopping the system.
	    * Then all waiting tasks and scheduled tasks will be cleared.
	    * ID issues by the Clock interface will be reset, and therefore would not be unique if previous
	    * IDs were still stored
	    * Then starts the clock again.
	    */
	   synchronized public void reset() {
	      stop();
	      time = System.currentTimeMillis();
	      waitingTasks.clear();
	      scheduledTasks.clear();
	      IDCounter = STARTING_ID;
	      start();
	   }

	   /**
	    * 
	    * @return the current time of the clock interface
	    */
	   public long currentTime() {
	      return time;
	   }

	   /**
	    * Generate an id for a scheduled task.
	    * @return A unique id for the scheduled task.
	    */
	   synchronized private int nextID() {
	      IDCounter++;
	      return IDCounter;
	   }

	   /**
	    * Private class describing a scheduled fixed delay task.
	    * This contains information about the task that will be run periodically,
	    * the delay between each run, and the time unit applied to the delay.
	    * Last but not least, this contains a ScheduleFuture provided by the thread pool
	    * that will allow cancellation of the task if needed.
	    * @author VDa
	    *
	    */
	   private static class ScheduledFixedRateTask implements ScheduledTasks {

	      private final Runnable task;
	      private ScheduledFuture<?> scheduleFuture;
	      private final int runRate;
	      private final TimeUnit unit;

	      private ScheduledFixedRateTask(Runnable task, ScheduledFuture<?> scheduleFuture, int runRate, TimeUnit unit) {
	         this.task = task;
	         this.runRate = runRate;
	         this.scheduleFuture = scheduleFuture;
	         this.unit = unit;
	      }

	      @Override
	      public void schedule(ScheduledThreadPoolExecutor scheduler) {
	         scheduleFuture = scheduler.scheduleAtFixedRate(task, 0, runRate, unit);
	      }

	      @Override
	      public void cancel(boolean mayInterruptIfRunning) {
	         scheduleFuture.cancel(mayInterruptIfRunning);
	      }
	   }

	   /**
	    * Private class describing a scheduled fixed delay task.
	    * This contains information about the task that will be run periodically,
	    * the delay between each run, and the time unit applied to the delay.
	    * Last but not least, this contains a ScheduleFuture provided by the thread pool
	    * that will allow cancellation of the task if needed.
	    * @author VDa
	    *
	    */
	   private static class ScheduledFixedDelayTask implements ScheduledTasks {
	      private final Runnable task;
	      private ScheduledFuture<?> scheduleFuture;
	      private final int delay;
	      private final TimeUnit unit;

	      private ScheduledFixedDelayTask(Runnable task, ScheduledFuture<?> scheduleFuture, int delay, TimeUnit unit) {
	         this.task = task;
	         this.delay = delay;
	         this.scheduleFuture = scheduleFuture;
	         this.unit = unit;
	      }

	      @Override
	      public void schedule(ScheduledThreadPoolExecutor scheduler) {
	         scheduleFuture = scheduler.scheduleWithFixedDelay(task, 0, delay, unit);
	      }

	      @Override
	      public void cancel(boolean mayInterruptIfRunning) {
	         scheduleFuture.cancel(mayInterruptIfRunning);
	      }
	   }

	   /**
	    * Private class describing a Waiting task.
	    * This contains information about the task that will be run,
	    * together with the time at which the task should be started.
	    * @author VDa
	    *
	    */
	   private static class WaitingTask {

	      private final Runnable task;
	      private final long startTime;

	      private WaitingTask(Runnable task, long startTime) {
	         this.task = task;
	         this.startTime = startTime;
	      }
	   }

	   /**
	    * Private interface describing a Scheduled task.
	    * @author VDa
	    *
	    */
	   private interface ScheduledTasks {
	      void schedule(ScheduledThreadPoolExecutor scheduler);
	      void cancel(boolean mayInterruptIfRunning);
	   }
	}