package Main.Units.Living.Boss.Sarcophagidae;

import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.Puck.Puck;
import Main.Units.NonLiving.UniversalEffect;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class ShadowsDance extends AdvancedBossSkill {

   public static final int ILLUSIONS_CIRLCE = 10;
   public static final int DISAPPEAR_TIME = 15;
   public static final double DISAPPEAR_SPEED = 0.1;
   public static final int RANGE = 300; //Real distance
   public static final int RADIUS = 100; //Real radius
   public static final int COOL_DOWN = 10000;

   public ShadowsDance(Game game, Sarcophagidae owner) {
      super(game, owner.target());
      setOwner(owner);
      setCoolDown(COOL_DOWN);
      setRadius(RADIUS);
   }

   @Override
   protected void schedule() {
      throw new UnsupportedOperationException("Invalid call.");
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      throw new UnsupportedOperationException("Invalid call.");
   }

   @Override
   public void setActivate(boolean activate, boolean forcedAdjust) {
      if (activate) {
         if (available() || forcedAdjust) {

            synchronized (owner().illusions()) {
               int extra = (owner().illusions().size() + 1) % ILLUSIONS_CIRLCE;
               int numberOfCircle = (owner().illusions().size() + 1) / ILLUSIONS_CIRLCE;
               if (extra > 0) {
                  numberOfCircle++;
               }

               int circleNumber = 1, currentCircle = 1;
               double startingAngle = 0;
               SarcophagidaeShadow exchange = null;
               for (SarcophagidaeShadow current : owner().illusions()) {

                  if (Math.random() < 0.25 && exchange != null) {
                     exchange = current;
                  }

                  synchronized (game().visualEffects()) {
                     game().visualEffects().add(new UniversalEffect(current.position(), Sarcophagidae.repInstances[0], Sarcophagidae.standardColors,
                             DISAPPEAR_TIME, DISAPPEAR_SPEED, Maths.randomAngle(), UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.SHRINKING_SCALE));
                  }

                  if (currentCircle == 1) {
                     startingAngle = Maths.randomAngle();
                  }
                  if (circleNumber != numberOfCircle) {//If not last circle
                     current.position().concur(target().position().getArcPointt(
                             radius() * circleNumber, startingAngle + currentCircle * 2 * Math.PI / ILLUSIONS_CIRLCE));
                  } else if (extra != 0) {//If last circle
                     current.position().concur(target().position().getArcPointt(
                             radius() * circleNumber, startingAngle + currentCircle * 2 * Math.PI / extra));
                  }

                  currentCircle++;
                  if (currentCircle > ILLUSIONS_CIRLCE) {
                     currentCircle = 1;
                     circleNumber++;
                  }
               }

               owner().position().concur(target().position().getArcPointt(
                       radius() * circleNumber, startingAngle + currentCircle * 2 * Math.PI / ILLUSIONS_CIRLCE));

               if (exchange != null) {
                  Pointt tam = owner().position().clone();
                  owner().position().concur(exchange.position());
                  exchange.position().concur(tam);
               }
            }
            setStartTime(Clocks.masterClock.currentTime());
            Audio.playSound(Audio.SHADOW_DANCE);
         }
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      throw new UnsupportedOperationException("Invalid call.");
   }

   @Override
   public boolean available() {
      return super.available() && owner().distance(target()) < RANGE && !owner().illusions().isEmpty();
   }

   @Override
   protected long elapsedTime() {
      throw new UnsupportedOperationException("Invalid call.");
   }

   @Override
   public Sarcophagidae owner() {
      return (Sarcophagidae) super.owner();
   }
}
