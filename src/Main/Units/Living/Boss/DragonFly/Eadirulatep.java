package Main.Units.Living.Boss.DragonFly;

import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.LivingUnit.Attack;
import Main.Units.Living.MainBuilding;
import Main.Units.Living.Puck.FirstSkill;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Audio;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Eadirulatep extends DragonFly {

   private int splitFire;
   private SoulLink soulLink;
   private PathOfVenoms pathOfVenoms;
   private static final byte VENOM_TYPE = Venom.CIRCLE_TYPE;
   private static final double SPLIT_ANGLE_INCREMENT = Math.toRadians(15);
   private static final int MAX_SPLIT = 16;
   private static final double SPEED_INITIAL = 0.05;
   private static final double DISPLAY_RADIUS = 30;
   private static final double ATTACK_SPEED = 0.75; //Seconds
   private static final Color COLOR_DEFAULT = Color.RED;
   private static final double RANGE_INITIAL = 175;
   private static final double DAMAGE_INITIAL = 500;
   private static final String NAME_DEFAULT = "Eadirulatep";

   public Eadirulatep(Game game, Pointt position) {
      super(game, position);
      this.setName(NAME_DEFAULT);
      this.setAttackSpeed(ATTACK_SPEED);
      this.setSpeed(SPEED_INITIAL);
      this.setRange(RANGE_INITIAL);
      this.setDamage(DAMAGE_INITIAL);
      this.setColor(COLOR_DEFAULT);

      splitFire = 1;

      venomFire = new VenomFire(SoulLink.VENOM_BLAST_COUNT);
      soulLink = new SoulLink(game, this);
      pathOfVenoms = new PathOfVenoms(game, this);

      setProjectileGenerator(new ProjectileGenerator());
      attack = new Attack();
      attack.schedule();
   }

   @Override
   public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      setHealth(health() + heal(), Units.HEAL);

      if (!pathOfVenoms.activate()) {
         if (pathOfVenoms.available()) {
            pathOfVenoms.setActivate(true, false);
         } else if (!soulLink.activate()) {
            if (!target().dead()) {
               if (soulLink.available() && this.distance(game.puck()) < soulLink.radius()) {
                  soulLink.setActivate(true, false);
               }
            }
         } else {
            super.move(time, testUnits, 1);
            addMoveEffect();
         }
      } else {
         if (!target().dead() && (target().getClass() != MainBuilding.class)) {
            double angle = this.position().angle(target().position());
            double range = 5 * target().radius();
            updateMovement(new Pointt(target().position().getX() + range * Math.cos(angle), target().position().getY() + range * Math.sin(angle)));
            super.moveNoCollision(time);
         } else {
            super.move(time, testUnits, 1);
            addMoveEffect();
         }
      }

      if (threatened() && diveable()) {
         dive = new SoulDive(game, this, game.puck(), Clocks.masterClock.currentTime());
         dive.schedule();
      } else {
         super.move(time, testUnits, 1);
         addMoveEffect();
      }
   }

   private void addMoveEffect() {
      ArrayList<Area> temp = new ArrayList<>();
      temp.add(DragonFly.RepGenerator.BODY_TOP);
      temp.add(DragonFly.RepGenerator.BODY_BOT);

      synchronized (game.visualEffects()) {
         game().visualEffects().add(new UniversalEffect(position().clone(),
                 new RepInstance(temp), DragonFly.standardColors.get(0),
                 UniversalEffect.DEFAULT_COUNT_DOWN, 0, movingAngle() + Math.PI/2,
                 UniversalEffect.STAND_STILL, UniversalEffect.FIX_BLUR, UniversalEffect.FIX_SCALE));
      }
   }

   @Override
   public void die(double damagingSpeed) {
      soulLink.setActivate(false, true);
      pathOfVenoms.setActivate(false, false);
      if (dive != null) {
         dive.clearTask();
      }
      super.die(damagingSpeed);
   }

   @Override
   protected boolean threatened() {
      if (!game.puck().dead()) {
         FirstSkill considering = game.puck().firstSkill();
         if (!considering.fakeSkill()) {
            Pointt display;
            Area skill, crep;

            if (considering.getRep() != null) {
               skill = new Area(considering.getRep());
               display = considering.displayPosition(game.focus());
               AffineTransform transform = AffineTransform.getTranslateInstance(display.getX(), display.getY());
               transform.rotate(considering.movingAngle());
               skill.transform(transform);


               crep = new Area(this.getRep());
               display = this.displayPosition(game.focus());
               transform = AffineTransform.getTranslateInstance(display.getX(), display.getY());
               crep.transform(transform);

               crep.intersect(skill);
               return !crep.isEmpty();
            }
         }
      }
      return false;
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
         if (dive == null || (dive.elapsedTime() > Dive.SKILL_BOUND)) {//Is not active
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
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      super.plotAttackUnits(a, transform, focus);
      if (soulLink.activate()) {
         soulLink.plot(a, transform, focus);
      }
   }

   @Override
   public Area getRep() {
      return new Area(Geometry.createEllipse(0, 0, DISPLAY_RADIUS, DISPLAY_RADIUS));


   }

   protected class VenomFire extends DragonFly.VenomFire {

      private int numberOfVenom;

      protected VenomFire(int numberOfVenom) {
         this.numberOfVenom = numberOfVenom;
      }

      @Override
      public void run() {
         for (int i = 0; i < numberOfVenom; i++) {
            double skewed = Math.random() * 45;
            Venom toAdd = new Venom(game, Eadirulatep.this, position(), -1,
                    Math.toRadians(skewed + i * 360 / numberOfVenom), VENOM_TYPE);
            toAdd.schedule();

            synchronized (projectiles) {
               projectiles.add(toAdd);
            }

            synchronized (venoms()) {
               venoms().add(toAdd);
            }
         }
         Audio.playSound(Audio.VENOM);
      }
   }

   protected class Attack extends LivingUnit.Attack {

      @Override
      public void run() {
         double shootingAngle = (splitFire / 2) * SPLIT_ANGLE_INCREMENT + DEFAULT_SHOOTING_ANGLE;
         if ((!target().dead())
                 && (Math.abs(movingAngle() - finalAngle()) <= shootingAngle)) {
            if (distance(target()) <= Eadirulatep.this.range()) {
               synchronized (Eadirulatep.this) {
                  synchronized (projectiles) {
                     projectiles.add(projectileGenerator().generateProjectile());
                     for (int i = 1; i < splitFire / 2 + 1; i++) {
                        projectiles.add(projectileGenerator().generateProjectile(movingAngle() + SPLIT_ANGLE_INCREMENT * i));
                        projectiles.add(projectileGenerator().generateProjectile(movingAngle() - SPLIT_ANGLE_INCREMENT * i));
                     }
                  }
               }
            }
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
   protected SoulDive dive() {
      return (SoulDive) dive;
   }

   @Override
   protected void removeDive() {
   }

   @Override
   protected boolean diveable() {
      return super.diveable() || dive.available();
   }

   protected void increaseSplitFire() {
      if (splitFire < MAX_SPLIT) {
         splitFire += 2; //Symmetrical
      }
   }

   protected SoulLink soulLink() {
      return soulLink;
   }

   protected PathOfVenoms pathOfVenoms() {
      return pathOfVenoms;
   }

   @Override
   public void setHealth(double health, int damageType) {
      if (!((pathOfVenoms != null && pathOfVenoms.activate()) && (health < health()) && (Math.random() < PathOfVenoms.EVASION))) {
         super.setHealth(health, damageType);
      }
   }
}