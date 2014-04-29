package Main.Units.Living.Boss.Ryskor;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.Living.Boss.AdvancedBossSkill;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.NonLiving.Projectile;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.MainScreen;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.Maths;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Ryskor extends LivingUnit {

    private static ArrayList<Area> standardProjectile;
    protected static RepInstance[] repInstances;
    private static ArrayList<Color> standardColors;
    private AdvancedBossSkill[] skills; //Moon blade, Blaze & stealth strike
    private static final byte MOON_BLADE_INDEX = 0;
    private static final byte BLAZE_INDEX = 1;
    private static final byte MOON_WALK_INDEX = 2;
    private static final double ANGULAR_SPEED = Math.toRadians(36);
    private static final double SPEED_INITIAL = 0.09;
    private static final double DISPLAY_RADIUS = 30;
    private static final double INITIAL_HEALTH = 30000;
    private static final double ATTACK_SPEED = 0.5; //Seconds
    private static final double PROJECTILE_SPEED = 0.1;
    private static final Color COLOR_DEFAULT = Color.MAGENTA;
    private static final double RANGE_INITIAL = 175;
    private static final double DAMAGE_INITIAL = 1000;
    private static final String NAME_DEFAULT = "Ryskor";

    public static void initialize() {//Call only once
        new Ryskor();
        MoonBlade.initialize();
        BlazeElement.initialize();
        MoonWalk.initialize();
        initProjectile();
    }

    private Ryskor() {
        RepGenerator generator = new RepGenerator();
        generator.generateInstances();
    }

    public Ryskor(Game game, Pointt position) {
        super(game, position, INITIAL_HEALTH, ProcessingUnit.AI_SIDE());
        this.setName(NAME_DEFAULT);
        this.setAttackSpeed(ATTACK_SPEED);
        this.setSpeed(SPEED_INITIAL);
        this.setAngularSpeed(ANGULAR_SPEED);
        this.setRadius(Pointt.displayToReal(DISPLAY_RADIUS));
        this.setRange(RANGE_INITIAL);
        this.setDamage(DAMAGE_INITIAL);

        partColors = standardColors;
        currentRepIndex = 0;
        setColor(COLOR_DEFAULT);

        skills = new AdvancedBossSkill[3]; //Moon blade, Blaze & stealth strike
        skills[MOON_BLADE_INDEX] = new MoonBlade(game, this, game.puck());
        skills[BLAZE_INDEX] = new Blaze(game, this, game.puck());
        skills[MOON_WALK_INDEX] = new MoonWalk(game, this, game.puck());

        setProjectileGenerator(new ProjectileGenerator());
        attack = new Attack();
        attack.schedule();
    }

    @Override
    public void die(double damagingSpeed) {
        clearTasks();

        //Turn skills off
        skills[MOON_BLADE_INDEX].setActivate(false, true);
        skills[MOON_WALK_INDEX].setActivate(false, true);

        super.die(damagingSpeed);
        synchronized (game.visualEffects()) {
            ArrayList<Area> part;
            for (int i = 0; i < repInstances[1].parts().size(); i++) {
                part = new ArrayList<>();
                part.add(new Area(repInstances[1].parts().get(i)));
                game.visualEffects().add(new UniversalEffect(position(),
                        new RepInstance(part), partColors.get(i), DEFAULT_FRAGMENT_FADE_TIME / 100, damagingSpeed,
                        Maths.randomAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
            }
        }
    }

    @Override
    public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
        currentRepIndex = (currentRepIndex + 1) % repInstances.length;
        if (!skills[MOON_WALK_INDEX].activate()) {
            super.move(time, testUnits, 1);
        }
        if (!dead()) {
            AIcastSkills();
        }
    }

    private void AIcastSkills() {
        boolean moonWalk = !skills[MOON_WALK_INDEX].activate() && skills[MOON_WALK_INDEX].available() && !target().dead();
        if (moonWalk) {
            skills[MOON_WALK_INDEX].setActivate(true, false);
            skills[MOON_BLADE_INDEX].setActivate(true, false);
            skills[BLAZE_INDEX].setActivate(true, false);
        } else if (this.distance(target()) < MoonBlade.RANGE) {
            skills[MOON_BLADE_INDEX].setActivate(true, false);
        }
    }

    @Override
    synchronized public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
        super.plot(a, transform, focus);

        if (!dead()) {
            a.setTransform(transform);
            a.setPaint(Color.BLACK);

            Pointt display = this.displayPosition(focus);
            a.translate(display.getX(), display.getY());
            a.rotate(this.movingAngle());
            repInstances[currentRepIndex].plot(a, partColors);

            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            plotProjectile(a, transform);
            plotAttackUnits(a, transform, focus);

            //Plot healthbar
            a.setTransform(transform);
            this.plotHealthBar(a, display);
        }
    }

    @Override
    public Area getRep() {
        return new Area(Geometry.createEllipse(0, 0, DISPLAY_RADIUS, DISPLAY_RADIUS));
    }

    @Override
    protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
        skills[MOON_BLADE_INDEX].plot(a, transform, focus);
        skills[MOON_WALK_INDEX].plot(a, transform, focus);
    }

    private class Attack extends LivingUnit.Attack {

        @Override
        public void run() {
            if (!target().dead() && (target().distance(Ryskor.this) < range())) {
                if (Math.abs(Ryskor.this.movingAngle() - finalAngle()) <= DEFAULT_SHOOTING_ANGLE) {
                    synchronized (Ryskor.this.projectiles()) {
                        projectiles().add(projectileGenerator().generateProjectile());
                    }
                }
            }
        }
    }

    private class RepGenerator implements Units.RepGenerator {

        static final double radius = 50;
        static final double width = 1.25 * radius, height = radius / 14;
        static final double startPoint = -radius + height;
        static final double widthDecrement = radius / 12;
        static final int numberOfWings = 26;
        static final int MAX = numberOfWings / 2 - 1;

        RepGenerator() {
            long start;
            start = Clocks.masterClock.currentTime();
        }

        @Override
        public void generateInstances() {
            final Pointt positionLeft = new Pointt(-2 * height, -2 * height);
            final Pointt positionRight = new Pointt(2 * height, -2 * height);
            final Pointt positionBotLeft = new Pointt(-2 * height, 2 * height);
            final Pointt positionBotRight = new Pointt(2 * height, 2 * height);

            Area leftRotateEye, rightRotateEye;


            leftRotateEye = shuriken(radius / 7);
            Area leftBotRotateEye = new Area(leftRotateEye);
            rightRotateEye = shuriken(radius / 7);
            Area rightBotRotateEye = new Area(rightRotateEye);
            leftRotateEye.transform(AffineTransform.getTranslateInstance(positionLeft.getX(), positionLeft.getY()));
            rightRotateEye.transform(AffineTransform.getTranslateInstance(positionRight.getX(), positionRight.getY()));

            leftBotRotateEye.transform(AffineTransform.getTranslateInstance(positionBotLeft.getX(), positionBotLeft.getY()));
            rightBotRotateEye.transform(AffineTransform.getTranslateInstance(positionBotRight.getX(), positionBotRight.getY()));

            leftRotateEye.add(leftBotRotateEye);
            rightRotateEye.add(rightBotRotateEye);


            Area[] wings = new Area[numberOfWings];
            Counter[] dec = new Counter[numberOfWings];
            for (int i = 0; i < numberOfWings / 2; i++) {
                dec[i] = new Counter(i, true);
            }
            for (int i = numberOfWings / 2; i < numberOfWings; i++) {
                dec[i] = new Counter(numberOfWings - 1 - i, false);
            }



            for (int i = 0; i < numberOfWings; i++) {
                wings[i] = roundRec(0, startPoint + i * height, width - dec[i].number * widthDecrement, height);
            }

            ArrayList<Area> parts;
            repInstances = new RepInstance[2 * (MAX + 1)];
            double rotatingAngle = Math.toRadians(-360.0 / (MAX + 1));
            for (int i = 0; i < 2 * (MAX + 1); i++) {
                parts = new ArrayList<>();
                parts.addAll(Arrays.asList(wings));
                parts.add(new Area(leftRotateEye));
                parts.add(new Area(rightRotateEye));

                Area left = shuriken(radius / 5);
                Area right = shuriken(radius / 5);
                left.transform(AffineTransform.getRotateInstance(i * rotatingAngle));
                right.transform(AffineTransform.getRotateInstance(-i * rotatingAngle));

                left.transform(AffineTransform.getTranslateInstance(2 * height, -radius + 2 * height));
                right.transform(AffineTransform.getTranslateInstance(-2 * height, -radius + 2 * height));

                parts.add(left);
                parts.add(right);

                //Fix biased angle
                for (int j = 0; j < parts.size(); j++) {
                    parts.get(j).transform(AffineTransform.getRotateInstance(Math.PI / 2));
                }

                repInstances[i] = new RepInstance(parts);

                for (int j = 0; j < numberOfWings; j++) {
                    dec[j].next();
                    wings[j] = roundRec(0, startPoint + j * height, width - dec[j].number * widthDecrement, height);
                }

                leftRotateEye.transform(AffineTransform.getRotateInstance(rotatingAngle));
                rightRotateEye.transform(AffineTransform.getRotateInstance(-rotatingAngle));
            }


            standardColors = new ArrayList<>();
            for (int i = 0; i < wings.length; i++) {//Wings color
                standardColors.add(Color.BLACK);
            }
            for (int i = 0; i < 4; i++) {//Eyes color
                standardColors.add(Color.RED);
            }

        }

        private Area roundRec(double cenX, double cenY, double width, double height) {
            return new Area(new RoundRectangle2D.Double(cenX - width, cenY - height, 2 * width, 2 * height, 15, 15));
        }

        private class Counter {

            int number;
            boolean increasing;

            Counter(int start, boolean increasing) {
                number = start;
                this.increasing = increasing;
            }

            private void next() {
                if (increasing) {
                    if (number == MAX) {
                        increasing = false;
                        next();
                    }
                    number++;
                } else {
                    if (number == 0) {
                        increasing = true;
                        next();
                    }
                    number--;
                }
            }
        }
    }

    private static void initProjectile() {
        standardProjectile = new ArrayList<>();

        double radius = ProjectileGenerator.RADIUS;
        Area defaultShape = shuriken(radius);
        standardProjectile.add(defaultShape);
    }

    private static Area shuriken(double radius) {
        Area whole = new Area(Geometry.createEllipse(0, 0, radius, radius));
        Pointt p1 = new Pointt(0, -radius);
        Pointt p2 = new Pointt(-radius * Math.sqrt(3) / 2, radius / 2);
        Pointt p3 = new Pointt(radius * Math.sqrt(3) / 2, radius / 2);
        Pointt rightCenter = p1.getRotated(p3, Math.PI / 3);
        Pointt leftCenter = p1.getRotated(p2, -Math.PI / 3);
        Pointt botCenter = p2.getRotated(p3, -Math.PI / 3);

        whole.subtract(new Area(Geometry.createEllipse(rightCenter.getX(), rightCenter.getY(), radius * Math.sqrt(3), radius * Math.sqrt(3))));
        whole.subtract(new Area(Geometry.createEllipse(leftCenter.getX(), leftCenter.getY(), radius * Math.sqrt(3), radius * Math.sqrt(3))));
        whole.subtract(new Area(Geometry.createEllipse(botCenter.getX(), botCenter.getY(), radius * Math.sqrt(3), radius * Math.sqrt(3))));

        return whole;
    }

    private class ProjectileGenerator implements LivingUnit.ProjectileGenerator {

        private ArrayList<Area> parts;
        private ArrayList<Color> colors;
        private static final int RADIUS = 15;

        @Override
        public Projectile generateProjectile() {
            final Color PROJECTILE_COLOR = Color.RED;
            colors = new ArrayList<>();
            colors.add(PROJECTILE_COLOR);
            parts = new ArrayList<>();

            Area toBeCreated = new Area();
            toBeCreated.add(standardProjectile.get(0));
            toBeCreated.transform(AffineTransform.getRotateInstance(Math.PI * 2 * Math.random()));

//            for (int i = 0; i < standardColors.size(); i++) {
            parts.add(toBeCreated);
//            }

            return new Projectile(Ryskor.this.position().clone(), Ryskor.this.movingAngle(),
                    PROJECTILE_SPEED, Ryskor.this.damage(), colors, parts, RADIUS);
        }

        @Override
        public Projectile generateProjectile(Color color) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

      @Override
      public Projectile generateProjectile(double movingAngle) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
    }
}
