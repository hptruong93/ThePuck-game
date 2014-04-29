package Main.Units.Living.Boss;

import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.Projectile;
import Buffs.Curse;
import Main.Units.Units;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class BossSkill extends Projectile implements Runnable {

   private static final int START_TIME_DEFAULT = 0;
   private ArrayList<Curse> effect;
   private HashMap<LivingUnit, HashSet<Curse>> underEffect; //Only for AOE skills
   private int timerID;
   private LivingUnit owner;
   private final Game game;
   private double damagingSpeed;
   private boolean disable;
   private long startTime;

   public BossSkill() {
      game = null;
      startTime = START_TIME_DEFAULT;
   }

   public BossSkill(Game game) {
      //Remember to schedule the skill. The constructor won't do it
      //Also remember to initialize effect by calling createEffectContainer().
      //This is to save memory in case the variable is not needed
      this.game = game;
      startTime = START_TIME_DEFAULT;
   }

   public void initializeUnderEffect() {//Call this only once, and only when need to use the hashmap
      underEffect = new HashMap<>();
   }

   abstract protected void schedule();

   public void clearTask() {
      Clocks.masterClock.removeScheduledTask(timerID);
   }

   /**
    * Movement may vary depends on the nature of the skill
    */
   @Override
   public void moveNoCollision(double time) {
      super.moveNoCollision(time);
   }

   synchronized protected void skillEffects(ArrayList<LivingUnit> enemy, Pointt focus) {
      Pointt display;
      Area skill, crep;
      skill = new Area(this.getRep());
      display = this.displayPosition(focus);
      AffineTransform af = AffineTransform.getTranslateInstance(display.getX(), display.getY());
      af.rotate(this.movingAngle());
      skill.transform(af);


      for (int i = 0; i < enemy.size(); i++) {
         LivingUnit current = enemy.get(i);
         if (current.dead()) {
            continue;
         }
         crep = new Area(current.getRep());

         display = current.displayPosition(focus);
         af = AffineTransform.getTranslateInstance(display.getX(), display.getY());
         af.rotate(current.movingAngle() + Math.PI / 2);
         crep.transform(af);

         crep.intersect(skill);

         applyEffect(current, !crep.isEmpty());
      }
   }

   @Override
   synchronized protected void handleTouch(Game game) {
      applyEffect(game.puck(), true);
      setRemoving(true);
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {//Skill effect
      if (touched) {
         affectedUnit.setHealth(affectedUnit.health() - this.damage(), Units.MAGICAL_DAMAGE);
         if (affectedUnit.health() <= 0) {
            affectedUnit.die(damagingSpeed);
         }
      }
   }

   @Override
   public abstract void plot(Graphics2D a, AffineTransform transform, Pointt focus);

   @Override
   public void run() throws RuntimeException {
      moveNoCollision(Game.map.PROCESSING_RATE());
      ArrayList enemies = new ArrayList<>();
      enemies.add(game.puck());
      skillEffects(enemies, game.focus());
   }

   @Override
   public void removeFromAllContainer() {
      clearTask();
      super.removeFromAllContainer();
   }

   protected abstract long elapsedTime();

   protected int timerID() {
      return timerID;
   }

   protected void setTimerID(int timerID) {
      this.timerID = timerID;
   }

   public double damagingSpeed() {
      return damagingSpeed;
   }

   protected void setDamagingSpeed(double damagingSpeed) {
      this.damagingSpeed = damagingSpeed;
   }

   protected Game game() {
      return game;
   }

   public boolean available() {
      return !disable;
   }

   public void setDisable(boolean disable) {
      this.disable = disable;
   }

   protected boolean isDisable() {
      return disable;
   }

   protected long startTime() {
      return startTime;
   }

   protected void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   protected ArrayList<Curse> effect() {
      return effect;
   }

   protected HashMap<LivingUnit, HashSet<Curse>> underEffect() {
      return underEffect;
   }

   protected void createEffectsContainer() {
      if (this.effect == null) {
         this.effect = new ArrayList<>();
      } else {
         throw new RuntimeException("Cannot initialize Curse twice");
      }
   }

   protected LivingUnit owner() {
      return owner;
   }

   protected void setOwner(LivingUnit owner) {
      this.owner = owner;
   }
}