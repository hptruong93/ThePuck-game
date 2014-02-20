package Main.Units.Living.Boss.DragonFly;

import Buffs.AngularSpeedBuff;
import Buffs.Curse;
import Buffs.Slow;
import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.concurrent.TimeUnit;

public class PathOfVenoms extends AdvancedBossSkill {

   private int numberOfVenomsLeft;
   protected static final byte VENOM_TYPE = Venom.CIRCLE_TYPE;
   protected static final double EVASION = 0.8; //80%
   private static final int UNIT_TIME = 250;//Milliseconds
   private static final int NUMBER_OF_VENOMS = 40;
   private static final double SPEED_BUFF = 2;
   private static final byte SPEED_BUFF_INDEX = 0;
   private static final double ANGULAR_SPEED_BUFF = Math.toRadians(0.2);
   private static final byte ANGULAR_SPEED_BUFF_INDEX = 1;
   private static final int COOL_DOWN = UNIT_TIME * NUMBER_OF_VENOMS + 8000;
   //Skill masterClock = UNIT_TIME * NUMBER_OF_VENOMS

   PathOfVenoms(Game game, Eadirulatep owner) {
      super(game, null);
      numberOfVenomsLeft = NUMBER_OF_VENOMS;
      setOwner(owner);
      setStartTime(0);
      setCoolDown(COOL_DOWN);
      createEffectsContainer();
      effect().add(new Slow(1 / SPEED_BUFF, Curse.NO_STACK, UNIT_TIME * NUMBER_OF_VENOMS, Curse.DEFAULT_START_TIME));
      effect().add(new AngularSpeedBuff(ANGULAR_SPEED_BUFF, UNIT_TIME * NUMBER_OF_VENOMS, Curse.DEFAULT_START_TIME));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      numberOfVenomsLeft--;
      if (numberOfVenomsLeft <= 0) {
         clearTask();
         super.setActivate(false, false);
      }
      applyEffect(null, true);
      Audio.attemptReplay(Audio.PATH_OF_VENOMS);
   }

   @Override
   synchronized public void setActivate(boolean activate, boolean forceAdjust) {
      if (activate) {
         if (available() || forceAdjust) {
            numberOfVenomsLeft = NUMBER_OF_VENOMS; //Reset count
            setStartTime(Clocks.masterClock.currentTime());

            synchronized (owner().curses()) {
               Curse tam = effect().remove(SPEED_BUFF_INDEX).clone();
               owner().curses().add(tam);
               effect().add(SPEED_BUFF_INDEX, tam);

               tam = effect().remove(ANGULAR_SPEED_BUFF_INDEX).clone();
               owner().curses().add(tam);
               effect().add(ANGULAR_SPEED_BUFF_INDEX, tam);
            }

            schedule();
            super.setActivate(true, forceAdjust);
            Audio.playSound(Audio.PATH_OF_VENOMS);
         }
      } else {
         super.setActivate(false, forceAdjust);
      }
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
      if (affectedUnit != null) {
         throw new UnsupportedOperationException("Invalid call");
      } else {
         Venom toBeAdd = new Venom(game(), owner(), owner().position(), 0, 0, VENOM_TYPE);
         synchronized (owner().projectiles()) {
            owner().projectiles().add(toBeAdd);
         }

         synchronized (owner().venoms()) {
            owner().venoms().add(toBeAdd);
         }

         toBeAdd.schedule();
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean available() {
      return super.available() && !activate();
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
   protected Eadirulatep owner() {
      return (Eadirulatep) super.owner();
   }
}