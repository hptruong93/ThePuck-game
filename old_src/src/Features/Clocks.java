package Features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Clocks implements Runnable {

   public static final Clocks masterClock = new Clocks();
   private long time;
   private ScheduledThreadPoolExecutor mainClock;
   public static int IDCounter;
   private final ScheduledThreadPoolExecutor scheduler;
   private final HashMap<Integer, ScheduledTasks> scheduledTasks;
   private final LinkedList<WaitingTask> waitingTasks;
   private static final long START_TIME = System.currentTimeMillis();
   private static final int INCREMENT = 50;
   public static final int DEFAULT_INTERVAL = 50;
   private static final int STARTING_ID = 10;
   private final int interval;


   private Clocks() {
      time = START_TIME;
      this.interval = DEFAULT_INTERVAL;
      scheduler = new ScheduledThreadPoolExecutor(50);
      mainClock = new ScheduledThreadPoolExecutor(1);
      waitingTasks = new LinkedList<>();
      scheduledTasks = new HashMap<>();
   }

   private Clocks(int interval) {
      time = START_TIME;
      this.interval = interval;
      scheduler = new ScheduledThreadPoolExecutor(50);
      mainClock = new ScheduledThreadPoolExecutor(1);
      waitingTasks = new LinkedList<>();
      scheduledTasks = new HashMap<>();
   }

   @Override
   synchronized public void run() {
      timerTick();
      runScheduledTasks();
   }

   synchronized public void start() {
      mainClock.scheduleAtFixedRate(this, 0, interval, TimeUnit.MILLISECONDS);
      for (ScheduledTasks current : scheduledTasks.values()) {
         current.schedule(scheduler);
      }
   }

   synchronized public void stop() {
      mainClock.shutdown();
      mainClock = new ScheduledThreadPoolExecutor(1);
      for (ScheduledTasks current : scheduledTasks.values()) {
         current.cancel(true);
      }
   }

   synchronized private void timerTick() {
      if (time < Long.MAX_VALUE - INCREMENT) {
         time += INCREMENT;
      } else {
         throw new RuntimeException("Counter overflow");
      }
   }

   synchronized private void runScheduledTasks() {
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

   synchronized public void removeScheduledTask(int id) {//Remove a task that has been scheduled periodically
      if (scheduledTasks.containsKey(id)) {
         scheduledTasks.get(id).cancel(false);
         scheduledTasks.remove(id);
      }
   }

   synchronized public int scheduleFixedRate(Runnable task, int rate, TimeUnit unit) {
      ScheduledFuture value = scheduler.scheduleAtFixedRate(task, 0, rate, unit);
      ScheduledFixedRateTask newComer = new ScheduledFixedRateTask(task, value, rate, unit);
      int output = nextID();
      scheduledTasks.put(output, newComer);
      return output;
   }

   synchronized public int scheduleFixedDelay(Runnable task, int delay, TimeUnit unit) {
      ScheduledFuture value = scheduler.scheduleWithFixedDelay(task, 0, delay, unit);
      ScheduledFixedDelayTask newComer = new ScheduledFixedDelayTask(task, value, delay, unit);
      int output = nextID();
      scheduledTasks.put(output, newComer);
      return output;
   }

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

   synchronized public void reset() {
      stop();
      time = System.currentTimeMillis();
      waitingTasks.clear();
      scheduledTasks.clear();
      IDCounter = STARTING_ID;
      start();
   }

   public long currentTime() {
      return time;
   }

   synchronized private int nextID() {
      IDCounter++;
      return IDCounter;
   }

   private static class ScheduledFixedRateTask implements ScheduledTasks {

      private Runnable task;
      private ScheduledFuture scheduleFuture;
      private int runRate;
      private TimeUnit unit;

      private ScheduledFixedRateTask(Runnable task, ScheduledFuture scheduleFuture, int runRate, TimeUnit unit) {
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

   private static class ScheduledFixedDelayTask implements ScheduledTasks {
      private Runnable task;
      private ScheduledFuture scheduleFuture;
      private int delay;
      private TimeUnit unit;

      private ScheduledFixedDelayTask(Runnable task, ScheduledFuture scheduleFuture, int delay, TimeUnit unit) {
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

   private static class WaitingTask {

      private Runnable task;
      private long startTime;

      private WaitingTask(Runnable task, long startTime) {
         this.task = task;
         this.startTime = startTime;
      }
   }

   private interface ScheduledTasks {
      void schedule(ScheduledThreadPoolExecutor scheduler);
      void cancel(boolean mayInterruptIfRunning);
   }
}