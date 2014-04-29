package Main.Units.Living;

import Main.Units.Units;
import Buffs.Curse;
import Main.Game;
import Main.MainScreen;
import Main.ProcessingUnit;
import Main.Units.Living.Puck.Puck;
import Main.Units.NonLiving.Projectile;
import Features.Audio;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class LivingUnit extends Units {

   protected Game game;
   private boolean selected;
   private boolean invulnerable;
   private boolean dead;
   private boolean damageReturn;
   private boolean inGhostForm;
   private boolean repel;
   private boolean healable;
   private boolean skillAble;
   private int side;
   private int numberOfKills;
   private int currentAttackRepIndex; //Assign to negative number to indicate not in attack mode
   private Color color;
   private double range;
   private double attackSpeed; // Attack speed in seconds
   private double bat; //Base attack masterClock in seconds
   private double heal;
   private String name;
   private LivingUnit target;
   protected Attack attack;
   private ProjectileGenerator projectileGenerator;
   protected final HashSet<Projectile> projectiles;
   private final HashSet<Curse> curses;
   private static Color HEALTH_BAR_COLOR = Color.BLUE;
   private static Color MAX_HEALTH_BAR_COLOR = Color.BLACK;
   private static final double DEAD_DEFAULT = 0.2;
   private static final Color SELECTED_ENEMY = Color.RED;
   private static final Color SELECTED_ALLIES = Color.GREEN;
   protected static final double DEFAULT_SHOOTING_ANGLE = Math.toRadians(30);
   protected static final int DEFAULT_FRAGMENT_FADE_TIME = 2500; //Milliseconds
   protected static final double ANGULAR_SPEED = Math.toRadians(0.15);// Per millisecond
   protected static final int START_ATTACKING = 0;
   protected static final int NOT_ATTACKING_REP_INDEX = -1;
   protected static final int READY_TO_ATTACK = Integer.MIN_VALUE;
   protected static final int FAKE_ENEMY_INDEX = -1;

   public LivingUnit() {//Fake LivingUnit
      super();
      curses = null;
      projectiles = null;
   }

   public LivingUnit(Pointt position, int side) {
      super(position);
      this.side = side;
      skillAble = true;
      healable = true;
      curses = new HashSet<>();
      currentAttackRepIndex = NOT_ATTACKING_REP_INDEX;
      projectiles = new HashSet<>();
   }

   LivingUnit(Pointt position, double movingAngle, double health, int side) {
      super(position, movingAngle, health);
      this.side = side;
      skillAble = true;
      healable = true;
      curses = new HashSet<>();
      currentAttackRepIndex = NOT_ATTACKING_REP_INDEX;
      projectiles = new HashSet<>();
   }

   public LivingUnit(Game game, Pointt position, double initialHealth, int side) {

      super(position, 0, initialHealth);
      this.side = side;
      skillAble = true;
      healable = true;
      curses = new HashSet<>();
      currentAttackRepIndex = NOT_ATTACKING_REP_INDEX;
      projectiles = new HashSet<>();

      this.game = game;
      this.target = game.puck();

      this.setAngularSpeed(ANGULAR_SPEED);
      this.setMaxHealth(initialHealth);
      this.setHealth(initialHealth, Units.FORCE_CHANGE);

      currentRepIndex = 0;
      setDestination(target().position());
   }

   synchronized public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      if (this.health() <= 0) {
         die(DEAD_DEFAULT);
      } else {
         processCurses();
      }

      if (side == ProcessingUnit.AI_SIDE()) {
         if (!this.dead()) {
            if (!this.target().dead()) {
               if (this.target().getClass() == MainBuilding.class && !game().puck().dead()) {
                  this.setTarget(game().puck());
               }
               this.updateMovement(this.target().position());
               double moveSpeed = this.speed();
               if (this.position().distance(this.target().position()) <= range()) {
                  this.setSpeed(0);
               }
               this.moveWithCollision(time, testUnits);
               this.setSpeed(moveSpeed);
            } else {//Target is dead, check to attack next available target: mainbuilding most likely
               if (this.target().getClass() == Puck.class) {
                  this.setTarget(game().mainBuilding());
               } else {
                  //End game
               }
            }
         }
      }
   }

   public void processCurses() {
      synchronized (curses()) {
         for (Iterator<Curse> it = curses().iterator(); it.hasNext();) {
            Curse current = it.next();
            if (current.removable(this, it)) {
               current.removeFrom(this, it);
            } else {
               current.applyEffect(this);
            }
         }
      }
   }

   synchronized public void processProjectile() {
      synchronized (projectiles) {
         if (projectiles == null) {
            return;
         }

         Pointt targetPosition = target.displayPosition(game().focus());
         Area targetArea = new Area(target.getRep());
         targetArea.transform(AffineTransform.getRotateInstance(target.movingAngle()));
         targetArea.transform(AffineTransform.getTranslateInstance(targetPosition.getX(), targetPosition.getY()));


         for (Iterator<Projectile> it = projectiles.iterator(); it.hasNext();) {
            Projectile current = it.next();
            if (current.removing()) {
               it.remove();
            } else {
               current.process(game(), it, targetArea);
            }
         }
      }

   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      try {
         if (!dead) {
            if (selected) {
               a.setTransform(transform);
               Pointt display = this.displayPosition(focus);
               if (side == ProcessingUnit.AI_SIDE()) {
                  a.setPaint(SELECTED_ENEMY);
               } else if (side == ProcessingUnit.PLAYER_SIDE()) {
                  a.setPaint(SELECTED_ALLIES);
               }
               a.draw(Geometry.createEllipse(display.getX(), display.getY(), radius() * Geometry.DISPLAY_REAL_RATIO, radius() * Geometry.DISPLAY_REAL_RATIO));
            }
         }
      } catch (NullPointerException e) {
      }
   }

   protected final void plotProjectile(Graphics2D a, AffineTransform transform) {
      synchronized (projectiles) {
         if (projectiles == null) {
            return;
         }

         for (Projectile current : projectiles) {
            current.plot(a, transform, game().focus());
         }
      }
   }

   /**
    * Warning: only plot Skills. Do NOT plot projectiles.
    */
   protected abstract void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus);

   /**
    * @Warning: setup the transform for the graphics before using this
    */
   protected final void plotHealthBar(Graphics2D a, Pointt display) {
      a.translate(display.getX(), display.getY());
      a.setPaint(MAX_HEALTH_BAR_COLOR);
      a.fill(this.healthBar());
      a.setPaint(HEALTH_BAR_COLOR);
      a.fill(this.realHealthBar());
   }

   public final void clearTasks() {
      if (attack != null) {
         synchronized (attack) {
            attack.stop();
         }
      }

      synchronized (projectiles) {
         projectiles.clear();
      }
   }

   public void die(double damagingSpeed) {
      clearTasks();
      setSkillEnable(false);
      setMoveable(false);
      setDead(true); //Declared dead, will be removed in ProcessingUnit -> Central processing unit
      Audio.playSound(Audio.DIE);
   }

   protected boolean inMotion() {
      return (this.position().samePlace(this.destination())) && (this.movingAngle() == this.finalAngle());
   }

   protected Pointt deadFragment() {
      return position().randomRange(2 * radius());
   }

   public void rescheduleAttack(long delay) {
      if (attack != null) {
         attack.schedule();
      }
   }

   public abstract class Attack implements Runnable {

      protected int timerID;

      public Attack() {
      }

      /**
       * @param delay in milliseconds
       */
      public void schedule() {
         Clocks.masterClock.removeScheduledTask(timerID);
         timerID = Clocks.masterClock.scheduleFixedRate(this, (int) (attackSpeed() * 1000), TimeUnit.MILLISECONDS);
      }

      /**
       * @param delay in milliseconds
       */
      public void scheduleOnce(long delay) {
         Clocks.masterClock.scheduleOnce(this, delay);
      }

      public void stop() {
         Clocks.masterClock.removeScheduledTask(timerID);
      }

      @Override
      public abstract void run();
   }

   public interface ProjectileGenerator {

      public Projectile generateProjectile();

      public Projectile generateProjectile(Color color);

      public Projectile generateProjectile(double movingAngle);
   }

   //Getter and setter
   public boolean dead() {
      return dead;
   }

   public void setSkillEnable(boolean isActive) {
      this.skillAble = isActive;
   }

   //Getter and Setter
   protected boolean skillAble() {
      return skillAble;
   }

   protected void setDead(boolean dead) {
      this.dead = dead;
   }

   @Override
   public void setHealth(double health, int damageType) {
      boolean ok = false;
      if (damageType == Units.FORCE_CHANGE) {
         ok = true;
      } else if (damageType == Units.HEAL) {
         ok = healable;
      } else if (!invulnerable) {
         if (damageType == Units.PHYSICAL_DAMAGE) {
            ok = !inGhostForm;
         } else if (damageType == Units.MAGICAL_DAMAGE) {
            ok = !repel;
         } else if (damageType == Units.PURE_DAMAGE) {
            ok = true;
         }
      }
      if (ok) {
         if (health > this.maxHealth()) {
            super.setHealth(this.maxHealth(), 1);
         } else {
            super.setHealth(health, 1);
         }
      }
   }

   public void increaseMaxHealth(double increment) {
      double newMax = Math.max(maxHealth() + increment, 0);
      setHealth((health() * newMax) / maxHealth(), Units.FORCE_CHANGE);
      setHeal((heal() * newMax) / maxHealth());
      this.setMaxHealth(newMax);
   }

   public void increaseRegen(double percentage) {
      percentage = percentage / 100;
      setHeal((heal / maxHealth() + percentage) * maxHealth());
   }

   protected void setColor(Color color) {
      this.color = color;
   }

   public Color color() {
      return color;
   }

   public final void setTarget(LivingUnit target) {
      this.target = target;
   }

   public final LivingUnit target() {
      return target;
   }

   public boolean invulnerable() {
      return invulnerable;
   }

   public void setInvulnerable(boolean invulnerable) {
      this.invulnerable = invulnerable;
   }

   public HashSet<Curse> curses() {
      return curses;
   }

   public int side() {
      return side;
   }

   public boolean damageReturn() {
      return damageReturn;
   }

   public boolean inGhostForm() {
      return inGhostForm;
   }

   public boolean repel() {
      return repel;
   }

   public void setDamageReturn(boolean damageReturn) {
      this.damageReturn = damageReturn;
   }

   public void setInGhostForm(boolean inGhostForm) {
      this.inGhostForm = inGhostForm;
   }

   public void setRepel(boolean repel) {
      this.repel = repel;
   }

   public double heal() {
      return heal;
   }

   protected void setHeal(double heal) {
      this.heal = heal;
   }

   protected boolean healable() {
      return healable;
   }

   public void setHealable(boolean healable) {
      this.healable = healable;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected, HashSet<LivingUnit> selectedUnits) {
      this.selected = selected;
      if (selected) {
         selectedUnits.add(this);
      }
   }

   protected void setName(String name) {
      this.name = name;
   }

   public String name() {
      return name;
   }

   public void setAttackSpeed(double attackSpeed) {
      this.attackSpeed = attackSpeed;
   }

   public double attackSpeed() {
      return attackSpeed;
   }

   protected int currentAttackIndex() {
      return currentAttackRepIndex;
   }

   protected void setCurrentAttackIndex(int currentAttackRepIndex) {
      this.currentAttackRepIndex = currentAttackRepIndex;
   }

   protected double bat() {
      return bat;
   }

   protected void setBat(double bat) {
      this.bat = bat;
   }

   public HashSet<Projectile> projectiles() {
      return projectiles;
   }

   public Game game() {
      return game;
   }

   public void setGame(Game game) {
      this.game = game;
   }

   protected void setRange(double range) {
      this.range = range;
   }

   public double range() {
      return range;
   }

   protected ProjectileGenerator projectileGenerator() {
      return projectileGenerator;
   }

   protected void setProjectileGenerator(ProjectileGenerator projectileGenerator) {
      this.projectileGenerator = projectileGenerator;
   }

   public int numberOfKills() {
      return numberOfKills;
   }

   public void increaseNumberOfKill(int increment) {
      numberOfKills += increment;
   }
}
