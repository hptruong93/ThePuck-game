package Main.Units.Living.Boss.Sarcophagidae;

import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExponentialGrowth extends AdvancedBossSkill {

   private double ownerPreviousHealth;
   protected static RepInstance instance;
   private static final int UNIT_TIME = 100;
   protected static final int APPEAR_TIME = 1500;
   protected static final double APPEAR_SPEED = 0.01;
   private static final int SKILL_TIME = 5000;
   private static final int COOL_DOWN = 7500;
   private static final double REP_SCALE = 0.25;
   private static final double HEALTH_SCALE = 0.5;
   private static final double DAMAGE_SCALE = 0.75;

   public static void initialize() {
      instance = new RepInstance(Sarcophagidae.repInstances[0].parts(), REP_SCALE);
   }

   public ExponentialGrowth(Game game, Sarcophagidae owner) {
      super(game, null);
      setCoolDown(COOL_DOWN);
      setOwner(owner);
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      skillEffects(null, null);
   }

   @Override
   public void skillEffects(ArrayList<LivingUnit> mustBeNull, Pointt mustBeNullToo) {
      if ((mustBeNull != null) || (mustBeNullToo != null)) {
         throw new UnsupportedOperationException("Invalid call. " + mustBeNull + " " + mustBeNullToo);
      } else if (elapsedTime() > SKILL_TIME) {
         setActivate(false, true);
      }
      if (owner().health() < ownerPreviousHealth) {
         applyEffect(null, true);
         ownerPreviousHealth = owner().health();
      }
   }

   @Override
   public void setActivate(boolean activate, boolean forceAdjust) {
      super.setActivate(activate, forceAdjust);
      if (activate) {
         if (available() || forceAdjust) {
            ownerPreviousHealth = owner().health();
            setStartTime(Clocks.masterClock.currentTime());
            schedule();
            Audio.playSound(Audio.EXPONENTIAL_GROWTH);
         }
      } else {
         clearTask();
      }
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      Pointt appearing = owner().position().randomRange(20);
      owner().addIllusion(new SarcophagidaeShadow(owner(), appearing, owner().movingAngle(), HEALTH_SCALE, DAMAGE_SCALE), APPEAR_TIME);

      synchronized (game().visualEffects()) {
         game().visualEffects().add(new UniversalEffect(appearing, Sarcophagidae.repInstances[0], Sarcophagidae.standardColors, APPEAR_TIME / 100,
                 0, owner().movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.APPEARING, UniversalEffect.FIX_SCALE));
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (activate() && Math.random() < 0.5) {
         synchronized (game().visualEffects()) {
            game().visualEffects().add(new UniversalEffect(owner().position().randomRange(25),
                    instance, Sarcophagidae.standardColors, APPEAR_TIME / 100,
                    APPEAR_SPEED, owner().movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
      }
   }

   @Override
   protected long elapsedTime() {
      if (activate()) {
         return Clocks.masterClock.currentTime() - startTime();
      } else {
         return 0;
      }
   }

   @Override
   protected Sarcophagidae owner() {
      return (Sarcophagidae) super.owner();
   }

   protected RepInstance instance() {
      return instance;
   }
}