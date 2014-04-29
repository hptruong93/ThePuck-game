package Main.Units.NonLiving;

import Buffs.Curse;
import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.Units;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

public abstract class ChasingProjectile extends Projectile {

   private LivingUnit chasing;
   private Game game;
   private LivingUnit owner;
   private ArrayList<Curse> effects;
   private static final double ANGULAR_SPEED = Math.toRadians(360);

   public ChasingProjectile(Game game, Pointt position, double speed, LivingUnit owner, LivingUnit chase) {
      super(position, 0, 0, owner.damage());
      this.game = game;
      this.owner = owner;
      this.setSpeed(speed);
      this.setAngularSpeed(ANGULAR_SPEED);
      this.setFinalAngle(this.movingAngle());
      this.chasing = chase;
   }

   @Override
   public final void moveNoCollision(double time) {
      if (chasing.dead()) {
         setRemoving(true);
      } else {
         setDestination(chasing.position());
         updateMovement(destination());
         super.moveNoCollision(time);

         if (position().distance(chasing.position()) <= (chasing.radius())) {// Touch chasing
            chasing.setHealth(chasing.health() - game.puck().damage(), Units.PHYSICAL_DAMAGE);
            setRemoving(true);
         } else if (this.position().samePlace(destination())) {
            setRemoving(true);
         }
         addVisualEffects();
      }
   }

   @Override
   public abstract void plot(Graphics2D a, AffineTransform transform, Pointt focus);

   protected abstract void addVisualEffects();

   @Override
   public abstract Area getRep();

   public final void createEffectsContainer() {
      effects = new ArrayList<>();
   }

   //Getter Auto-generated code
   protected final ArrayList<Curse> effects() {
      return effects;
   }

   protected final Game game() {
      return game;
   }

   public LivingUnit owner() {
      return owner;
   }

   public LivingUnit chasing() {
      return chasing;
   }
}
