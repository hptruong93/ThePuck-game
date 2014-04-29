package Main.Units.Living.Boss.DragonFly;

import Features.Audio;
import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.Puck.SecondSkill;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;

public class Petaluridae extends DragonFly {

   private final HashSet<Venom> venoms;
   private static final byte VENOM_TYPE = 0;
   private static final double SPEED_INITIAL = 0.07;
   private static final double DISPLAY_RADIUS = 30;
   private static final double ATTACK_SPEED = 0.75; //Seconds
   private static final Color COLOR_DEFAULT = Color.BLUE;
   private static final double RANGE_INITIAL = 175;
   private static final double DAMAGE_INITIAL = 500;
   private static final double VENOM_RATE = 0.015;
   private static final String NAME_DEFAULT = "Petaluridae";

   public Petaluridae(Game game, Pointt position) {
      super(game, position);
      this.setName(NAME_DEFAULT);
      this.setAttackSpeed(ATTACK_SPEED);
      this.setSpeed(SPEED_INITIAL);
      this.setRange(RANGE_INITIAL);
      this.setDamage(DAMAGE_INITIAL);
      this.setColor(COLOR_DEFAULT);

      venoms = new HashSet<>();
      venomFire = new VenomFire();

      setProjectileGenerator(new ProjectileGenerator());
      attack = new Attack();
      attack.schedule();
   }

   @Override
   public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      currentRepIndex = (currentRepIndex + 1) % repInstances.length;
      super.move(time, testUnits, 1);
      if (diveable()) {
         if (threatened() && skillAble()) {
            dive = new IllusoryDive(this, game.puck());
            dive.schedule();
         }
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      super.plot(a, transform, focus);
      a.setTransform(transform);
      a.setPaint(Color.BLACK);
      if (!this.dead()) {
         Pointt display = this.displayPosition(focus);
         a.translate(display.getX(), display.getY());
         a.rotate(this.movingAngle());
         if (dive == null) {
            repInstances[currentRepIndex].plot(a, partColors);
            int j = currentRepIndex;
            for (int i = 0; i < 6; i++) {
               j = (j - 1) % repInstances.length;
               if (j < 0) {
                  j += repInstances.length;
               }
               a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) (6 - i) / 6));
               repInstances[j].plot(a, partColors);
            }
         }

         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
         plotProjectile(a, transform);
         plotAttackUnits(a, transform, focus);

         //Plot healthbar
         a.setTransform(transform);
         this.plotHealthBar(a, display);
      }
   }

   @Override
   protected boolean threatened() {
      if (game.puck().distance(this) < IllusoryDive.RANGE) {
         if (game.puck().secondSkill().activate()) {
            if (Math.abs(game.puck().position().angle(this.position()) - game.puck().movingAngle()) < SecondSkill.COVER_ANGLE / 2) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public Area getRep() {
      return new Area(Geometry.createEllipse(0, 0, DISPLAY_RADIUS, DISPLAY_RADIUS));
   }

   protected class VenomFire extends DragonFly.VenomFire {

      final int NUMBER_OF_VENOM = 18;

      @Override
      public void run() {
         for (int i = 0; i < NUMBER_OF_VENOM; i++) {
            double skewed = Math.random() * 45;
            Venom toAdd = new Venom(game, Petaluridae.this, position(), -1,
                    Math.toRadians(skewed + i * 360 / NUMBER_OF_VENOM), VENOM_TYPE);

            synchronized (projectiles) {
               projectiles.add(toAdd);
            }

            synchronized (venoms) {
               venoms.add(toAdd);
            }
         }
         Audio.playSound(Audio.VENOM);
      }
   }

   protected class Attack extends LivingUnit.Attack {

      @Override
      public void run() {
         try {
            if ((!target().dead())
                    && (Math.abs(movingAngle() - finalAngle()) <= DEFAULT_SHOOTING_ANGLE)) {
               if (distance(game.puck()) <= Petaluridae.this.range()) {
                  synchronized (projectiles) {
                     projectiles.add(projectileGenerator().generateProjectile());
                  }
               }

               if (skillAble()) {
                  if (Math.random() < VENOM_RATE) {
                     for (int i = 0; i < 3; i++) {
                        venomFire.scheduleOnce(500 * i);
                     }
                  }
               }
            }
         } catch (Exception e) {
            throw e;
         }
      }
   }

   @Override
   protected void setDive(Dive dive) {//Can only be called after calling removeDive()
      if (this.dive != null) {
         throw new RuntimeException("Dive has to be null for this call to be valid.");
      } else {
         this.dive = dive;
      }
   }

   @Override
   protected IllusoryDive dive() {
      return (IllusoryDive) dive;
   }
}