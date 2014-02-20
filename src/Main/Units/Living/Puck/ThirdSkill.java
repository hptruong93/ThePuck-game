package Main.Units.Living.Puck;

import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.MainScreen;
import Features.Audio;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;

public class ThirdSkill extends PuckSkill {

   public static final int DEFAULT_BONUS_TYPE = 0;
   public static final long SKILL_TIME = 2000; // in milliseconds
   public static final int COOL_DOWN = 1900; //Milliseconds
   private static final byte SHRINK_TIME = 5; //Actual shrink masterClock = SHRINK_TIME / (1000 / Game.map.processingTime())
   private static int initialCoolDown = COOL_DOWN;
   private static AlphaComposite[] blur;
   private boolean activate;

   protected static void initialize() {
      blur = new AlphaComposite[3];
      for (int i = 0; i < 3; i++) {
         blur[i] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) (0.2 * i));
      }
   }

   public ThirdSkill(Game game, Puck owner) {
      super(game, initialCoolDown);
      activate = false;
      this.setOwner(owner);
   }

   @Override
   public void setActivate(boolean activate) {
      if (!activate) {
         owner().setInvulnerable(false);
         owner().setTransparent(false);
         this.activate = false;
      } else {
         if (Clocks.masterClock.currentTime() - this.startTime() > this.coolDown()) {
            owner().setUsedThirdSkill(true);
            this.setStartTime(Clocks.masterClock.currentTime());
            owner().setInvulnerable(true);
            owner().setTransparent(true);
            this.activate = true;

            synchronized (game().visualEffects()) {
               game().visualEffects().add(new UniversalEffect(owner().position(), Puck.repInstances[10], Puck.standardColors,
                       SHRINK_TIME, 0, owner().movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FIX_BLUR, UniversalEffect.SHRINKING_SCALE));
            }
            Audio.playSound(Audio.THIRD_SKILL);
         }
      }
   }

   @Override
   protected void applyKillBonus(LivingUnit killedUnit) {
      throw new RuntimeException("Not suppose to call this");
   }

   @Override
   public void degrade() {
      throw new RuntimeException("Not suppose to call this");
   }

   @Override
   public double checkKill(HashSet<LivingUnit> creeps, Pointt focus) {
      throw new RuntimeException("This method is not supposed to be called");
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      throw new RuntimeException("This method is not supposed to be called");
   }

   public boolean activate() {
      return activate;
   }

   @Override
   protected long elapsedTime() {
      throw new RuntimeException("Not suppose to call this!");
   }

   @Override
   public void schedule() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public static void setInitialCoolDown(int initialCoolDown) {
      ThirdSkill.initialCoolDown = initialCoolDown;
   }

   protected static AlphaComposite[] blur() {
      return blur;
   }
}
