package Main.Units.Living.Boss.DragonFly;

import Main.Game;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.Boss.BossSkill;
import Main.Units.Living.LivingUnit;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public abstract class Dive extends BossSkill {

    protected double relativeTime;
    protected Pointt start, end, origin;
    protected double angle;
    protected static final int UNIT_TIME = 60;
    protected static final long SKILL_BOUND = 51;//Milliseconds
    protected static final double DELTA = 0.00001;
    //RunningTime = (SKILL_BOUND/INCREMENT) * UNIT_TIME;

    public Dive(Game game, DragonFly owner) {
        super(game);
        setOwner(owner);
    }

    public abstract void calculateEndPoint(Pointt targetPosition);

    @Override
    protected abstract void schedule();

    @Override
    public void run() {
        moveNoCollision(UNIT_TIME);
    }

    @Override
    public abstract void moveNoCollision(double time);

    @Override
    public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
        throw new RuntimeException("Not suppose to call this");
    }

    protected Pointt position(double time) {
        double fix = (2 * Math.PI) / SKILL_BOUND;

        double x1 = start.getX();
        double x2 = end.getX();

        double x = -0.5 * (x2 - x1) * Math.cos(fix * (time - sign(x1 - x2) * Math.PI));
        double y = 0.25 * (x2 - x1) * Math.sin(fix * time);

        Pointt out = new Pointt(x, y);
        out = out.getRotated(new Pointt(0, 0), angle);
        out.translate(origin);

        out.setX(Geometry.fixX(out.getX()));
        out.setY(Geometry.fixY(out.getY()));
        return out;
    }

    private static int sign(double a) {
        return (a < 0) ? 0 : 1;
    }

    @Override
    protected long elapsedTime() {
        throw new UnsupportedOperationException("Not suppose to call this.");
    }

    @Override
    protected DragonFly owner() {
        return (DragonFly) super.owner();
    }

    @Override
    protected final void setOwner(LivingUnit owner) {
        super.setOwner(owner);
    }
}