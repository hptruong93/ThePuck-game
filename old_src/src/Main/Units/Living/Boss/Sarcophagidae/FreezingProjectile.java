package Main.Units.Living.Boss.Sarcophagidae;

import Buffs.Curse;
import Buffs.Slow;
import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.Living.MainBuilding;
import Main.Units.NonLiving.ChasingProjectile;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashMap;

public class FreezingProjectile extends ChasingProjectile {

   private Pointt previousPosition;
   private static final double SPEED = 0.15;
   private static final double SLOW_FACTOR = 2; //No stack
   private static final int SLOW_DURATION = 3000;
   private static final int FADE_TIME = 5;
   private static final Color DEFAULT_COLOR = new Color(41, 69, 235);
   private static final HashMap<LivingUnit, Curse> slows = new HashMap<>();
   private static RepInstance instance;
   private static ArrayList<Color> standardColor;

   protected static void initialize() {
      ArrayList<Area> temp = new ArrayList<>();
      double radius = 20, width = 2.5, cutting = 2;
      Area whole = new Area();

      Area right = new Area(Geometry.createEllipse(0, 0, radius / width, radius));
      Area cut = new Area(Geometry.createRectangle(0, 0, radius, radius));
      cut.transform(AffineTransform.getTranslateInstance(0, radius / cutting));
      right.subtract(cut);

      GeneralPath bot = new GeneralPath();
      bot.moveTo(-Math.sqrt(1 - 1 / Math.pow(cutting, 2)) * radius / width, -radius / cutting);
      bot.quadTo(-radius / (0.8 * width), radius - radius / cutting, 0, radius);
      bot.lineTo(Math.sqrt(1 - 1 / Math.pow(cutting, 2)) * radius / width, -radius / cutting);
      bot.closePath();

      whole.add(right);
      whole.add(new Area(bot));

      bot.transform(AffineTransform.getScaleInstance(-1, 1));
      whole.add(new Area(bot));
      whole.transform(AffineTransform.getRotateInstance(Math.PI/2));

      temp.add(whole);

      instance = new RepInstance(temp);
      standardColor = new ArrayList<>();
      standardColor.add(DEFAULT_COLOR);
   }

   public FreezingProjectile(Game game, Pointt position, LivingUnit owner, LivingUnit chase, double damage) {
      super(game, position, SPEED, owner, chase);
      setDamage(damage);

      previousPosition = position.clone();

      createEffectsContainer();
      effects().add(new Slow(SLOW_FACTOR, Curse.NO_STACK, SLOW_DURATION, Curse.DEFAULT_START_TIME));
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      //Visual Effect already plot this quite precisely
   }

   @Override
   protected void addVisualEffects() {
      synchronized (game().visualEffects()) {
         game().visualEffects().add(new UniversalEffect(position(), instance, standardColor, FADE_TIME,
                 0, movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.SHRINKING_SCALE));
      }

      previousPosition.concur(position());
   }

   @Override
   protected void applyEffect(LivingUnit enemy, boolean touched) {
      if (touched) {
         if (enemy.getClass() != MainBuilding.class) {
            enemy.setHealth(enemy.health() - this.damage(), Units.PHYSICAL_DAMAGE);
            if (slows.containsKey(enemy) && enemy.curses().contains(slows.get(enemy))) {//Extend masterClock
               slows.get(enemy).setStartTime(Clocks.masterClock.currentTime());
            } else {//Start monitoring
               Curse toBeAdd = effects().get(0).clone();

               synchronized (slows) {
                  slows.put(enemy, toBeAdd);
               }

               synchronized (enemy.curses()) {
                  enemy.curses().add(toBeAdd);
               }
            }
         }
      }
   }

   @Override
   public Area getRep() {
      return instance.parts().get(0);
   }
}
