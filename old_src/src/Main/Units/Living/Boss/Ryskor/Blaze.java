package Main.Units.Living.Boss.Ryskor;

import Features.Audio;
import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashSet;

public class Blaze extends AdvancedBossSkill {

    private static final int COOL_DOWN = 3500; //Milliseconds
    private static final double RANGE = 200; //DISPLAY range
    private HashSet<BlazeElement> elements;

    public Blaze(Game game, Ryskor owner, LivingUnit target) {
        super(game, target);
        this.setCoolDown(COOL_DOWN);
        this.setOwner(owner);
        elements = new HashSet<>();
    }

    @Override
    protected void schedule() {
        throw new UnsupportedOperationException("Not suppose to have this");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not suppose to have this");
    }

    @Override
    public void moveNoCollision(double time) {
        throw new UnsupportedOperationException("Not suppose to have this");
    }

    @Override
    public void setActivate(boolean activate, boolean forcedAdjust) {// Cast a BlazeElement
        if (activate && !owner().dead()) {
            if (available() && !target().dead()) {
                if (owner().distance(target()) <= RANGE) {
                    setStartTime(Clocks.masterClock.currentTime());
                    BlazeElement toBeAdd = new BlazeElement(game(), target(), this);
                    toBeAdd.schedule();

                    synchronized (owner().projectiles()) {
                        owner().projectiles().add(toBeAdd);
                    }

                    synchronized (elements) {
                        elements.add(toBeAdd);
                    }
                    Audio.playSound(Audio.BLAZE);
                }
            }
        }
    }

    @Override
    public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
        throw new UnsupportedOperationException("Not suppose to have this");
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
    protected void applyEffect(LivingUnit affectedUnit, boolean touched) {
        throw new UnsupportedOperationException("Not suppose to have this");
    }

    protected void removeElement(BlazeElement element) {
        synchronized (elements) {
            elements.remove(element);
        }
    }

    @Override
    public void removeFromAllContainer() {
        clearTask();
    }

    @Override
    protected Ryskor owner() {
        return (Ryskor) super.owner();
    }
}
