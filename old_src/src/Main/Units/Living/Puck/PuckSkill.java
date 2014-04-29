package Main.Units.Living.Puck;

import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.Units;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.HashSet;

public abstract class PuckSkill extends Units implements Runnable {

   protected final int DEFAULT_START_TIME = 0;
   private Game game;
   private Puck owner;
   private int timerID;
   private int coolDown;
   private long startTime;
   private double damagingSpeed;
   private boolean disable;
   private int bonusType;

   public PuckSkill() {
   }

   public PuckSkill(Game game, int coolDown) {
      this.game = game;
      this.coolDown = coolDown;
      owner = game.puck();
   }

   public abstract void schedule();

   public void clearTask() {
      Clocks.masterClock.removeScheduledTask(timerID);
   }

   @Override
   public void moveNoCollision(double time) {
      throw new UnsupportedOperationException("Ambiguous skill! Identify the skill!");
   }

   protected double checkKill(HashSet<LivingUnit> enemies, Pointt focus) {//Should overwrite this...

      Pointt display;
      Area skill, crep;

      skill = new Area(this.getRep());
      display = this.displayPosition(focus);
      AffineTransform transform = AffineTransform.getTranslateInstance(display.getX(), display.getY());
      transform.rotate(this.movingAngle());
      skill.transform(transform);

      synchronized (enemies) {
         for (LivingUnit current : enemies) {
            if (current.dead() || current.repel()) {
               continue;
            }
            crep = new Area(current.getRep());

            display = current.displayPosition(focus);
            transform = AffineTransform.getTranslateInstance(display.getX(), display.getY());
            crep.transform(transform);

            crep.intersect(skill);

            if (!crep.isEmpty()) {//Touch the current
               castSkillEffect(current, true);
               if (current.health() <= 0) {
                  current.die(damagingSpeed);
                  this.applyKillBonus(current);
               }
            } else {
               castSkillEffect(current, false);
            }
         }
      }
      return 0;
   }

   protected void castSkillEffect(LivingUnit affected, boolean touched) {
      if (touched) {
         affected.setHealth(affected.health() - this.damage(), Units.MAGICAL_DAMAGE);

         if (affected.damageReturn()) {
            owner.setHealth(owner.health() - this.damage(), Units.MAGICAL_DAMAGE);
         }
      }
   }

   @Override
   public abstract void plot(Graphics2D a, AffineTransform transform, Pointt focus);

   @Override
   public void run() throws RuntimeException {
      moveNoCollision(Game.map.PROCESSING_RATE());
      checkKill(game.enemies(), game.focus());
   }

   protected abstract void applyKillBonus(LivingUnit killedUnit);

   public abstract void degrade();

   public void setTimerID(int timerID) {
      this.timerID = timerID;
   }

   protected abstract long elapsedTime();

   public double damagingSpeed() {
      return damagingSpeed;
   }

   protected void setDamagingSpeed(double damagingSpeed) {
      this.damagingSpeed = damagingSpeed;
   }

   protected Game game() {
      return game;
   }

   public int coolDown() {
      return coolDown;
   }

   protected void setCoolDown(int coolDown) {
      this.coolDown = coolDown;
   }

   public long startTime() {
      return startTime;
   }

   protected void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public boolean available() {
      return (Clocks.masterClock.currentTime() - startTime > coolDown) && (!disable);
   }

   public abstract void setActivate(boolean activate);

   public void setDisable(boolean disable) {
      this.disable = disable;
   }

   protected boolean isDisable() {
      return disable;
   }

   public int bonusType() {
      return bonusType;
   }

   public void setBonusType(int bonusType) {
      this.bonusType = bonusType;
   }

   protected void setOwner(Puck owner) {
      this.owner = owner;
   }

   protected Puck owner() {
      return owner;
   }
}