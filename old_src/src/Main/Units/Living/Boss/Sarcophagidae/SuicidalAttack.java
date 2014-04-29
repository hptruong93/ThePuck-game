package Main.Units.Living.Boss.Sarcophagidae;

import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SuicidalAttack extends AdvancedBossSkill {

   private static final int MAX_ACTIVATION_TIME = 4000; //Milliseconds
   private static final double RANGE = 200;
   private static final byte DAMAGE_SPLIT = 10;
   private static final double DAMAGING_SPEED = 0.2;
   private static final int COOL_DOWN = 3000;

   public SuicidalAttack(Game game, Sarcophagidae owner) {
      super(game, owner.target());
      setOwner(owner);
      setCoolDown(COOL_DOWN);
   }

   protected static double damageFunction(int numberOfIllusions) {
      return Math.pow(Math.E, numberOfIllusions);
   }

   @Override
   protected void schedule() {
      for (int i = 0; i < DAMAGE_SPLIT; i++) {
         Clocks.masterClock.scheduleOnce(this, Maths.RANDOM.nextInt(MAX_ACTIVATION_TIME));
      }
   }

   @Override
   public void run() {
      if (!target().invulnerable() && !target().repel()) {
         applyEffect(target(), true);
      }
   }

   @Override
   public void setActivate(boolean activate, boolean forceAdjust) {
      if (activate) {
         if (available() || forceAdjust) {
            setDamage(damageFunction(owner().illusions().size()));
            synchronized (game().visualEffects()) {
               synchronized (owner().illusions()) {
                  for (Iterator<SarcophagidaeShadow> it = owner().illusions().iterator(); it.hasNext();) {
                     SarcophagidaeShadow current = it.next();
                     current.setHealth(-1, Units.FORCE_CHANGE); //Synchronized way to declare dead
                  }

                  owner().illusions().clear();
               }
            }

            setStartTime(Clocks.masterClock.currentTime());
            schedule();
         }
      }
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      Audio.playSound(Audio.SUICIDAL_ATTACK);
      affectedUnit.setHealth(affectedUnit.health() - damage() / DAMAGE_SPLIT, Units.MAGICAL_DAMAGE);
      plot(null, null, null);
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {//Does not use any of the parameter
      int random;
      ArrayList<Area> temp;

      synchronized (game().visualEffects()) {
         for (int i = 0; i < 10; i++) {
            temp = new ArrayList<>();
            random = Maths.RANDOM.nextInt(Sarcophagidae.repInstances[0].parts().size());
            temp.add(Sarcophagidae.repInstances[0].parts().get(random));
            game().visualEffects().add(new UniversalEffect(target().position(), new RepInstance(temp),
                    Sarcophagidae.standardColors.get(random),
                    UniversalEffect.DEFAULT_COUNT_DOWN, DAMAGING_SPEED, Maths.randomAngle(),
                    UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }


   }

   @Override
   public boolean available() {
      return super.available() && (owner().distance(owner().target()) < RANGE);
   }

   @Override
   protected long elapsedTime() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   protected Sarcophagidae owner() {
      return (Sarcophagidae) super.owner();
   }
}
