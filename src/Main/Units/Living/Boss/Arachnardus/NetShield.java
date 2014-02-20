package Main.Units.Living.Boss.Arachnardus;

import Main.Game;
import Main.Units.Living.Boss.BossSkill;
import Main.Units.Living.LivingUnit;
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
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class NetShield extends BossSkill {

    private static RepGenerator generator;
    private static RepInstance instance;
    private static ArrayList<Color> standardColors;
    private HashSet<LivingUnit> buffedUnits;
    private static final Color DEFAULT_COLOR = Color.YELLOW;
    private static final double RADIUS = 50;
    private static final double SKILL_TIME = 5000; //Milliseconds
    private static final int UNIT_TIME = 20; //Milliseconds
    public static final double RANGE = 350;

    public static void initialize() {
        new NetShield();
    }

    private NetShield() {// This is used to initialize
        generator = new RepGenerator();
        generator.generateInstances();
    }

    public NetShield(Game game, Arachnardus owner) {
        super(game);
        this.setRadius(RADIUS);
        buffedUnits = new HashSet<>();

        synchronized (game.enemies()) {
            for (LivingUnit current : game.enemies()) {
                if (current.position().distance(owner.position()) <= RADIUS) {
                    buffedUnits.add(current);
                    current.setInvulnerable(true);
                    current.setDamageReturn(true);
                }
            }
        }

        this.setStartTime(Clocks.masterClock.currentTime());
        this.setOwner(owner);
        this.createEffectsContainer();
        Audio.playSound(Audio.NET_SHIELD);
    }

    @Override
    public void schedule() {
        setTimerID(Clocks.masterClock.scheduleFixedRate(this, UNIT_TIME, TimeUnit.MILLISECONDS));
    }

    @Override
    public void moveNoCollision(double time) {
        if (elapsedTime() > SKILL_TIME) {
            synchronized (buffedUnits) {
                for (LivingUnit current : buffedUnits) {
                    current.setInvulnerable(false);
                    current.setDamageReturn(false);
                }
            }
            owner().resetShield();
            clearTask();
        } else {
            synchronized (buffedUnits) {
                for (LivingUnit current : buffedUnits) {
                    current.setInvulnerable(true);
                }
            }
        }
    }

    @Override
    public void run() {
        moveNoCollision(Game.map.PROCESSING_RATE());
    }

    @Override
    public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
        Pointt display;
        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        synchronized (buffedUnits) {
            try {
                for (LivingUnit current : buffedUnits) {
                    if (!current.dead()) {
                        a.setTransform(transform);
                        display = current.displayPosition(focus);
                        a.translate(display.getX(), display.getY());
                        generator.generateInstanceWithRadius(current.radius() * Geometry.DISPLAY_REAL_RATIO);
                        instance.plot(a, standardColors);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    @Override
    protected long elapsedTime() {
        return Clocks.masterClock.currentTime() - this.startTime();
    }

    @Override
    public Area getRep() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private class RepGenerator implements Units.RepGenerator {

        public void generateInstanceWithRadius(double radius) {
            generateInstances();
            ArrayList part = new ArrayList<>();

            Area big = new Area(Geometry.createEllipse(0, 0, radius, radius));
            Area small = new Area(Geometry.createEllipse(0, 0, radius - 10, radius - 10));
            big.subtract(small);

            part.add(big);
            instance = new RepInstance(part);
        }

        @Override
        public void generateInstances() {
            standardColors = new ArrayList<>();
            standardColors.add(DEFAULT_COLOR);
        }
    }

    @Override
    protected Arachnardus owner() {
        return (Arachnardus) super.owner();
    }

    @Override
    protected final void setOwner(LivingUnit owner) {
        if (owner.getClass().equals(Arachnardus.class)) {
            super.setOwner(owner);
        }
    }
}