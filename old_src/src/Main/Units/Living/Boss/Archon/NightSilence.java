package Main.Units.Living.Boss.Archon;

import Buffs.Curse;
import Main.Game;
import Main.Units.Living.Boss.BossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.RepInstance;
import Main.Units.Units;
import Buffs.Silent;
import Buffs.Slow;
import Main.MainScreen;
import Features.Audio;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NightSilence extends BossSkill {

    private static RadialGradientPaint standardPaint;
    private static RepInstance instance;
    private static ArrayList<Color> standardColors;
    private double rotatingAngle;
    private boolean caught;
    private static final double SPEED = 0.09; //Distance moved per timeFrame
    private static final double ROTATING_SPEED = Math.toRadians(5);
    private static final double SLOW_FACTOR = 5;
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final double RADIUS = 150;
    private static final double SKILL_TIME = 3500; //Milliseconds
    private static final int UNIT_TIME = 100; //Milliseconds
    private static final double ANGULAR_SPEED = Math.toRadians(360);
    public static final double RANGE = 200;

    public static void initialize() {
        new NightSilence();
    }

    private NightSilence() {// This is used to initialize
        NightSilence.RepGenerator generator = new NightSilence.RepGenerator();
        generator.generateInstances();
        float[] color = {0.2f, 0.9f};
        Color[] colors = {Color.BLACK, Color.GRAY};
        standardPaint = new RadialGradientPaint(0, 0, (float) RADIUS, color, colors);
    }

    public NightSilence(Game gameMap, LivingUnit target, Pointt castPosition, Archon owner) {
        super(gameMap);
        this.setOwner(owner);
        partColors = standardColors;

        initializeUnderEffect();
        this.setSpeed(SPEED);
        this.setAngularSpeed(ANGULAR_SPEED);
        this.setPosition(calculatePosition(castPosition, target.position()));
        rotatingAngle = 0;
        this.setRadius(RADIUS);
        this.setStartTime(Clocks.masterClock.currentTime());
        this.createEffectsContainer();
        effect().add(new Silent(Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
        effect().add(new Slow(SLOW_FACTOR, Curse.NO_STACK, Curse.INFINITE_DURATION, Curse.DEFAULT_START_TIME));
        Audio.playSound(Audio.NIGHT_SILENCE);
    }

    private static Pointt calculatePosition(Pointt castPosition, Pointt targetPosition) {
        if (castPosition.distance(targetPosition) <= RANGE) {
            return targetPosition.clone();
        } else {
            double angle = Geometry.arcTan(targetPosition.getY() - castPosition.getY(), targetPosition.getX() - castPosition.getX(), targetPosition.getX() - castPosition.getX());
            return new Pointt(castPosition.getX() + RANGE * Math.cos(angle), castPosition.getY() + RANGE * Math.sin(angle));
        }
    }

    @Override
    public void schedule() {
        setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
    }

    @Override
    public void moveNoCollision(double time) {
        rotatingAngle += Math.toRadians(ROTATING_SPEED);
        if (elapsedTime() > SKILL_TIME) {
            clearTask();

            synchronized (underEffect()) {
                Set<LivingUnit> encircleds = underEffect().keySet();
                for (Iterator<LivingUnit> ir = encircleds.iterator(); ir.hasNext();) {
                    LivingUnit removingUnit = ir.next();
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

            caught = false;
            owner().setSilent(null);
        }
    }

    @Override
    public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
        a.setTransform(transform);
        Pointt display = this.displayPosition(focus);
        a.translate(display.getX(), display.getY());

        a.rotate(rotatingAngle);
        rotatingAngle += ROTATING_SPEED;
        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        a.setPaint(standardPaint);
        instance.plot(a);
        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    @Override
    protected void applyEffect(LivingUnit affectedUnit, boolean touched) {//Skill effect
        synchronized (affectedUnit.curses()) {
            if (elapsedTime() > SKILL_TIME) {
                return;
            } else if (affectedUnit.dead()) {
                removeFromAllContainer();
            }

            if (touched) {
                caught = true;
                if (!underEffect().containsKey(affectedUnit)) {
                    underEffect().put(affectedUnit, new HashSet<Curse>());
                    HashSet<Curse> silentCurse = underEffect().get(affectedUnit);
                    for (int i = 0; i < effect().size(); i++) {
                        Curse current = effect().get(i).clone();
                        affectedUnit.curses().add(current);
                        silentCurse.add(current);
                    }
                }
            } else {
                if (underEffect().containsKey(affectedUnit)) {
                    caught = false;
                    HashSet<Curse> silentCurse = underEffect().get(affectedUnit);
                    for (Curse current : silentCurse) {
                        current.removeFrom(affectedUnit, null);
                    }
                    underEffect().remove(affectedUnit);
                }
            }
        }
    }

    @Override
    public Shape getRep() {
        return Geometry.createEllipse(0, 0, RADIUS, RADIUS);
    }

    private class RepGenerator implements Units.RepGenerator {

        @Override
        public void generateInstances() {
            standardColors = new ArrayList<>();
            ArrayList part = new ArrayList<>();

            Area tam = new Area(Geometry.createEllipse(0, 0, RADIUS, RADIUS));

            part.add(tam);
            standardColors.add(DEFAULT_COLOR);

            instance = new RepInstance(part);
        }
    }

    @Override
    protected long elapsedTime() {
        return Clocks.masterClock.currentTime() - this.startTime();
    }

    protected boolean caught() {
        return caught;
    }

    @Override
    protected Archon owner() {
        return (Archon) super.owner();
    }

    @Override
    protected final void setOwner(LivingUnit owner) {
        if (owner.getClass().equals(Archon.class)) {
            super.setOwner(owner);
        }
    }
}