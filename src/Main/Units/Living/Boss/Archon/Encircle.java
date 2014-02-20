package Main.Units.Living.Boss.Archon;

import Buffs.AngularSpeedBuff;
import Buffs.Curse;
import Buffs.DamageEnhance;
import Buffs.LifeDrain;
import Buffs.Poison;
import Main.Game;
import Main.Units.Living.Boss.BossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.RepInstance;
import Main.Units.Units;
import Buffs.Slow;
import Main.ProcessingUnit;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Encircle extends BossSkill {

//    private static RadialGradientPaint standardPaint;
   private static RepInstance instance;
   private static ArrayList<Color> standardColors;
   private double rotatingAngle;
   private boolean caught;
   private static final int ACTIVATE_TIME = 750;
   private static final byte SLOW_EFFECT_INDEX = 0;
   private static final byte NO_HEAL_EFFECT_INDEX = 1;
   private static final byte DAMAGE_BUFF_EFFECT_INDEX = 2;
   private static final byte SPEED_BUFF_EFFECT_INDEX = 3;
   private static final byte ANGULAR_SPEED_BUFF_EFFECT_INDEX = 4;
   private static final byte LIFE_DRAIN_EFFECT_INDEX = 5;
   private static final double LIFE_DRAIN_FACTOR = 0.03;//Percent max health
   private static final double ROTATING_SPEED = Math.toRadians(15);
   private static final double SLOW_FACTOR = 1.5;
   private static final double RADIUS = 700;
   private static final double SKILL_TIME = 10000; //Milliseconds
   private static final int UNIT_TIME = 100; //Milliseconds
   private static final double DAMAGE_BUFF_FATOR = 1.5;
   private static final double SPEED_BUFF_FACTOR = 2;
   private static final double ANGULAR_SPEED_BUFF = Units.MAX_ANGULAR_SPEED;

   public static void initialize() {
      new Encircle();
   }

   private Encircle() {// This is used to initialize
      RepGenerator generator = new RepGenerator();
      generator.generateInstances();
   }

   public Encircle(Game gameMap, LivingUnit target, Pointt castPosition) {
      super(gameMap);
      partColors = standardColors;
      this.setPosition(castPosition.clone());
      this.setSpeed(0);
      this.setAngularSpeed(0.01);
      rotatingAngle = 0;
      caught = false;
      this.setRadius(RADIUS);
      this.setStartTime(Clocks.masterClock.currentTime());
      initializeUnderEffect();

      this.createEffectsContainer();
      effect().add(new Slow(SLOW_FACTOR, Curse.NO_STACK, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
      effect().add(new Poison(0, Curse.NO_STACK, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
      effect().add(new DamageEnhance(DAMAGE_BUFF_FATOR, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
      effect().add(new Slow(1 / SPEED_BUFF_FACTOR, Curse.NO_STACK, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
      effect().add(new AngularSpeedBuff(ANGULAR_SPEED_BUFF, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
   }

   @Override
   protected void schedule() {
      setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
   }

   @Override
   public void run() {
      ArrayList<LivingUnit> tam = new ArrayList<>();
      tam.add(game().puck());
      synchronized (game().enemies()) {
         tam.addAll(game().enemies());
      }
      if (elapsedTime() > ACTIVATE_TIME) {
         skillEffects(tam, game().focus());
      }
   }

   @Override
   protected void skillEffects(ArrayList<LivingUnit> affectedUnits, Pointt focus) {
      for (int i = 0; i < affectedUnits.size(); i++) {
         LivingUnit current = affectedUnits.get(i);
         synchronized (underEffect()) {
            if (this.position().distance(current.position()) < Pointt.displayToReal(RADIUS)) {
               applyEffect(current, true);
            } else if (underEffect().containsKey(current)) {
               applyEffect(current, false);
            }
         }
      }

      if (elapsedTime() > SKILL_TIME) {
         synchronized (underEffect()) {
            Set<LivingUnit> encircleds = underEffect().keySet();
            for (Iterator<LivingUnit> ir = encircleds.iterator(); ir.hasNext();) {
               LivingUnit removingUnit = ir.next();
               if (removingUnit.dead()) {
                  continue;
               }
               synchronized (removingUnit.curses()) {
                  Iterator<Curse> it;
                  for (it = underEffect().get(removingUnit).iterator(); it.hasNext();) {
                     Curse nextCurse = it.next();
                     nextCurse.removeFrom(removingUnit, null);
                  }
               }
            }
            underEffect().clear();
         }
         clearTask();
         Archon.resetEncircle();
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      a.setTransform(transform);
      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
              (float) Math.min(0.7 * elapsedTime() / ACTIVATE_TIME, 0.7)));
      rotatingAngle += ROTATING_SPEED;
      a.rotate(rotatingAngle);
      instance.plot(a, partColors);
      plotLink(a, transform, focus, display);
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
   }

   private void plotLink(Graphics2D a, AffineTransform transform, Pointt focus, Pointt display) {
      a.setTransform(transform);
      synchronized (underEffect()) {
         Set<LivingUnit> encircled = underEffect().keySet();
         for (LivingUnit current : encircled) {
            if (!current.dead()) {
               Pointt currentDisplay = current.displayPosition(focus);
               GeneralPath path = new GeneralPath();
               path.moveTo(display.getX(), display.getY());
               path.quadTo(0.5 * (currentDisplay.getX() + display.getX()) + 50 * Math.random(),
                       0.5 * (currentDisplay.getY() + display.getY()) + 50 * Math.random(),
                       currentDisplay.getX(), currentDisplay.getY());
               path.closePath();
               a.fill(path);
            }
         }
      }
   }

   private double calculateDrain(double affectedUnitMaxHealth) {
      return affectedUnitMaxHealth * LIFE_DRAIN_FACTOR;
   }

   @Override
   protected void applyEffect(LivingUnit affectedUnit, boolean touched) {//Skill toApply
      if (affectedUnit.dead()) {
         synchronized (underEffect()) {
            if (underEffect().containsKey(affectedUnit)) {
               underEffect().remove(affectedUnit);
            }
         }
         return;
      }

      synchronized (effect()) {
         synchronized (affectedUnit.curses()) {
            if (affectedUnit.side() == ProcessingUnit.PLAYER_SIDE()) {
               if (!caught) {
                  if (touched) {
                     caught = true;
                     effect().add(new LifeDrain(calculateDrain(affectedUnit.maxHealth()), Curse.INFINITE_DURATION, Clocks.masterClock.currentTime()));
                     affectedUnit.curses().add(effect().get(LIFE_DRAIN_EFFECT_INDEX));
                     effect().get(LIFE_DRAIN_EFFECT_INDEX).applyEffect(affectedUnit);
                     affectedUnit.curses().remove(effect().get(LIFE_DRAIN_EFFECT_INDEX));
                     effect().remove(LIFE_DRAIN_EFFECT_INDEX);

                     if (!underEffect().containsKey(affectedUnit)) {
                        underEffect().put(affectedUnit, new HashSet<Curse>());
                        Curse slow = effect().get(SLOW_EFFECT_INDEX).clone();
                        affectedUnit.curses().add(slow);
                        underEffect().get(affectedUnit).add(slow);

                        Curse noHeal = effect().get(NO_HEAL_EFFECT_INDEX).clone();
                        affectedUnit.curses().add(noHeal);
                        underEffect().get(affectedUnit).add(noHeal);
                     }
                  }
               } else {//Already inside the skill
                  if (touched) {
                     effect().add(new LifeDrain(calculateDrain(affectedUnit.maxHealth()), Curse.INFINITE_DURATION, Clocks.masterClock.currentTime()));
                     affectedUnit.curses().add(effect().get(LIFE_DRAIN_EFFECT_INDEX));
                     effect().get(LIFE_DRAIN_EFFECT_INDEX).applyEffect(affectedUnit);
                     affectedUnit.curses().remove(effect().get(LIFE_DRAIN_EFFECT_INDEX));
                     effect().remove(LIFE_DRAIN_EFFECT_INDEX);
                  } else {//Break spell, removeFrom all curses
                     caught = false;
                     synchronized (underEffect()) {
                        HashSet<Curse> curses = underEffect().get(affectedUnit);
                        for (Curse current : curses) {
                           current.removeFrom(affectedUnit, null);
                        }
                        underEffect().remove(affectedUnit);
                     }
                     affectedUnit.setHealth(affectedUnit.health() - (affectedUnit.maxHealth() - affectedUnit.health()), Units.MAGICAL_DAMAGE);
                  }
               }
            } else if (affectedUnit.side() == ProcessingUnit.AI_SIDE()) {
               if (touched) {
                  if (!underEffect().containsKey(affectedUnit)) {
                     underEffect().put(affectedUnit, new HashSet<Curse>());

                     Curse damage = effect().get(DAMAGE_BUFF_EFFECT_INDEX).clone();
                     affectedUnit.curses().add(damage);
                     underEffect().get(affectedUnit).add(damage);

                     Curse speed = effect().get(SPEED_BUFF_EFFECT_INDEX).clone();
                     affectedUnit.curses().add(speed);
                     underEffect().get(affectedUnit).add(speed);

                     Curse angular = effect().get(ANGULAR_SPEED_BUFF_EFFECT_INDEX).clone();
                     affectedUnit.curses().add(angular);
                     underEffect().get(affectedUnit).add(angular);
                  }
               } else {//Remove everything
                  synchronized (underEffect()) {
                     HashSet<Curse> curses = underEffect().get(affectedUnit);
                     for (Curse current : curses) {
                        current.removeFrom(affectedUnit, null);
                     }
                     underEffect().remove(affectedUnit);
                  }
               }
            }
         }
      }
   }

   @Override
   protected long elapsedTime() {
      return Clocks.masterClock.currentTime() - startTime();
   }

   private class RepGenerator implements Units.RepGenerator {

      private static final int THICKNESS = 15;

      @Override
      public void generateInstances() {
         ArrayList<Area> parts = new ArrayList<>();
         Area outer = new Area(Geometry.createEllipse(0, 0, RADIUS, RADIUS));
         Area inner = new Area(Geometry.createEllipse(0, 0, RADIUS - THICKNESS, RADIUS - THICKNESS));
         outer.subtract(inner);
         parts.add(outer);

         Area leftCircle = new Area(Geometry.createEllipse(-10, -20, 20, 20));
         Area rightCircle = new Area(Geometry.createEllipse(10, -20, 20, 20));
         rightCircle.subtract(leftCircle);
         parts.add(new Area(rightCircle));
         rightCircle.transform(AffineTransform.getRotateInstance(-Math.PI / 2));
         parts.add(new Area(rightCircle));
         rightCircle.transform(AffineTransform.getRotateInstance(-Math.PI / 2));
         parts.add(new Area(rightCircle));
         rightCircle.transform(AffineTransform.getRotateInstance(-Math.PI / 2));
         parts.add(new Area(rightCircle));

         instance = new RepInstance(parts);
         standardColors = new ArrayList<>();
         standardColors.add(Color.RED);
         standardColors.add(new Color(0x24, 0x34, 0xFF));
         standardColors.add(new Color(0xFF, 0x24, 0xFA));
         standardColors.add(new Color(0xFF, 0x24, 0x27));
         standardColors.add(new Color(0xFF, 0xF2, 0x24));
      }
   }
}