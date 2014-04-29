package Main.Units.Living.Boss.Sarcophagidae;

import Main.Game;
import Main.Units.Living.Illusion;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.MainBuilding;
import Main.Units.Living.Puck.Puck;
import Main.Units.NonLiving.Projectile;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SarcophagidaeShadow extends Illusion {

   SarcophagidaeShadow(Sarcophagidae original, Pointt position, double movingAngle, double healthScale, double damageScale) {
      super(original, position, movingAngle, healthScale, damageScale);
      setTarget(original.target());

      setProjectileGenerator(new ProjectileGenerator());
      attack = new Attack();
      attack.schedule();
   }

   @Override
   public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      super.move(time, testUnits, thisCanBeAnyInt);

      if (currentAttackIndex() == LivingUnit.READY_TO_ATTACK) {
         synchronized (projectiles) {
            projectiles.add(projectileGenerator().generateProjectile(Sarcophagidae.PROJECTILE_COLOR));
         }
         setCurrentAttackIndex(LivingUnit.NOT_ATTACKING_REP_INDEX);
      } else if (currentAttackIndex() == LivingUnit.NOT_ATTACKING_REP_INDEX) {
         currentRepIndex = (currentRepIndex + 1) % Sarcophagidae.repInstances.length;
      }

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

   @Override
   public void die(double damagingSpeed) {
      super.die(damagingSpeed);

      synchronized (original().illusions()) {
         original().illusions().remove(this);
      }

      synchronized (game.visualEffects()) {
         ArrayList<Area> part;
         for (int i = 0; i < Sarcophagidae.repInstances[0].parts().size(); i++) {
            part = new ArrayList<>();
            part.add(new Area(Sarcophagidae.repInstances[0].parts().get(i)));
            game.visualEffects().add(new UniversalEffect(position(),
                    new RepInstance(part), partColors.get(i), DEFAULT_FRAGMENT_FADE_TIME / 100,
                    damagingSpeed, Math.random() * Math.PI * 2, UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   @Override
   synchronized public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      super.plot(a, transform, focus);

      if (!this.dead()) {
         a.setTransform(transform);
         a.setPaint(Color.BLACK);
         Pointt display = this.displayPosition(focus);
         a.translate(display.getX(), display.getY());
         a.rotate(this.movingAngle());

         if (currentAttackIndex() < 0) {
            Sarcophagidae.repInstances[currentRepIndex].plot(a, partColors);
         } else {
            Sarcophagidae.attackInstances[currentAttackIndex()].plot(a, partColors);
            setCurrentAttackIndex(currentAttackIndex() + (int) ((Sarcophagidae.attackInstances.length * Game.map.TIME_FRAME()) / (bat() * 1000)));
            if (currentAttackIndex() >= Sarcophagidae.attackInstances.length) {
               setCurrentAttackIndex(LivingUnit.READY_TO_ATTACK);
            }
         }

         plotProjectile(a, transform);
         plotAttackUnits(a, transform, focus);

         a.setTransform(transform);
         plotHealthBar(a, display);
      } else {
         super.plot(a, transform, focus);
      }
   }

   private class Attack extends LivingUnit.Attack {

      @Override
      public void run() {
         if ((!SarcophagidaeShadow.this.target().dead())
                 && (Math.abs(movingAngle() - finalAngle()) <= LivingUnit.DEFAULT_SHOOTING_ANGLE)) {
            double distance = position().distance(target().position());

            if (distance < range()) {//Shoot normally
               if (currentAttackIndex() == LivingUnit.NOT_ATTACKING_REP_INDEX) {
                  setCurrentAttackIndex(LivingUnit.START_ATTACKING);
               }
            }
         }
      }
   }

   private class ProjectileGenerator implements LivingUnit.ProjectileGenerator {

      @Override
      public Projectile generateProjectile(Color color) {
         if (original().exponentialGrowth().activate()) {
            return new FreezingProjectile(game, position(), SarcophagidaeShadow.this, target(), damage());
         } else {
            return new Projectile(position().clone(), movingAngle(), Sarcophagidae.PROJECTILE_SPEED,
                    damage(), Sarcophagidae.projectileColors, Sarcophagidae.projectileInstance, Sarcophagidae.ProjectileGenerator.RADIUS);
         }
      }

      @Override
      public Projectile generateProjectile() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      @Override
      public Projectile generateProjectile(double movingAngle) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (original().exponentialGrowth().activate() && Math.random() < 0.5) {
         synchronized (game().visualEffects()) {
            game().visualEffects().add(new UniversalEffect(position().randomRange(25),
                    ExponentialGrowth.instance, Sarcophagidae.standardColors, ExponentialGrowth.APPEAR_TIME / 100,
                    ExponentialGrowth.APPEAR_SPEED, movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   @Override
   public Shape getRep() {
      return original().getRep();
   }

   @Override
   protected Sarcophagidae original() {
      return (Sarcophagidae) super.original();
   }
}