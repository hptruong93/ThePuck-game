package Main.Units.Living.Puck;

import Main.Game;
import Main.ProcessingUnit;
import Main.Units.RepInstance;
import Main.Units.Units;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.UniversalEffect;
import Main.Units.NonLiving.Projectile;
import Main.MainScreen;
import Features.Audio;
import Features.Clocks;
import Utilities.Geometry;
import Utilities.FileUtilities;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Puck extends LivingUnit {

   protected static RepInstance[] repInstances;
   protected static ArrayList<Color> standardColors;
   private boolean holdBlink;
   private boolean holdAttack;
   private boolean holdFirstSkill;
   private boolean holdUltimate;
   private boolean attackCommand; //Auto attack mode
   private long lastAttack;
   private long deadTime;
   private long respawnTime;
   private long blinkStart;
   private boolean blinked, usedFirstSkill, usedSecondSkill, usedThirdSkill, usedUltimate; //Tutorial only
   private int range;
   private byte bonusType;
   private FirstSkill firstSkill;
   private SecondSkill secondSkill;
   private ThirdSkill thirdSkill;
   private Ultimate ultimate;
   private static final String NAME_DEFAULT = "The Puck";
   private static final long BLINK_COOL_DOWN = 1500;//Milliseconds
   private static final double BLINK_DISTANCE = 1000 / Geometry.DISPLAY_REAL_RATIO;
   public static final byte SKILL_BONUS = 0;
   public static final byte HEALTH_BONUS = 1;
   public static final double DEFAULT_NATURAL_HEAL = 1;
   private static final Color DEFAULT_COLOR = Color.LIGHT_GRAY;
   public static final int DEFAULT_DAMAGE = 300;
   private static final int INITIAL_RANGE = 150;
   public static final int DEFAULT_MAX_HEALTH = 3000;
   public static final int DEFAULT_KILLS = 0;
   public static final int HEALTH_INCREMENT = 20;
   public static final double REGEN_INCREMENT = 0.0005;
   public static final int DEFAULT_BAT = 1000; //Millisecond
   private static final double DEFAULT_SPEED = 0.1;
   private static final double ANGULAR_SPEED = Math.toRadians(1); //Redefine ANGULAR_SPEED
   public static final double DISPLAY_RADIUS = 30;
   public static final int DEFAULT_RESPAWN_TIME = 2000; //Milliseconds
   private static final int RESPAWN_TIME_INCREMENT = 1000;
   private static final double DEAD_DEFAULT_SPEED = 0.1;
   private static final Pointt STARTING_POSITION = new Pointt(Game.map.REAL_SIZEX() / 2, Game.map.REAL_SIZEY() / 2);
   private static int initialRespawnTime = DEFAULT_RESPAWN_TIME;
   private static double initialDamage = DEFAULT_DAMAGE;
   private static double initialNaturalHeal = DEFAULT_NATURAL_HEAL;
   private static double initialMaxHealth = DEFAULT_MAX_HEALTH;
   private static double initialHealth = DEFAULT_MAX_HEALTH;
   private static double initialAttackSpeed = DEFAULT_BAT;
   private static int initialKills = DEFAULT_KILLS;

   public Puck(Game game, Pointt position, int numberOfKills) {
      super(position, ProcessingUnit.PLAYER_SIDE());
      this.game = game;
      increaseNumberOfKill(initialKills);
      setName(NAME_DEFAULT);
      setDestination(position);
      setSpeed(DEFAULT_SPEED);
      setAngularSpeed(ANGULAR_SPEED);
      range = INITIAL_RANGE;

      setRadius(Pointt.displayToReal(DISPLAY_RADIUS));
      partColors = standardColors;
      setColor(DEFAULT_COLOR);

      respawnTime = initialRespawnTime;
      setDamage(initialDamage);
      setHeal(initialNaturalHeal);
      setMaxHealth(initialMaxHealth);
      setHealth(initialHealth, Units.FORCE_CHANGE);
      setAttackSpeed(initialAttackSpeed);

      firstSkill = new FirstSkill();
      thirdSkill = new ThirdSkill(game, this);

      holdAttack = false;
      holdBlink = false;
      holdFirstSkill = false;
      attackCommand = false;

      lastAttack = 0;
      setTarget(null);
   }

   public void blink(Pointt destination) {
      if (blinkable()) {
         setBlinked(true);
         synchronized (game.visualEffects()) {
            game.visualEffects().add(new UniversalEffect(position(), Puck.repInstances[10], partColors,
                    20, 0, movingAngle(), UniversalEffect.STAND_STILL, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
         }
         blinkStart = Clocks.masterClock.currentTime();
         if (thirdSkill != null) {
            thirdSkill.setActivate(false);
         }

         Pointt dest;
         if (BLINK_DISTANCE < this.position().distance(destination)) {//Out of blink range
            double angle = this.position().angle(destination);
            dest = new Pointt(this.position().getX() + BLINK_DISTANCE * Math.cos(angle), this.position().getY() + BLINK_DISTANCE * Math.sin(angle));
         } else {//In range
            dest = destination;
         }
         this.setPosition(dest);
         this.setDestination(dest);
      }
   }

   @Override
   synchronized public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      if (this.health() < 0 && !this.dead()) {
         this.die(DEAD_DEFAULT_SPEED);
      } else if (!this.dead()) {
         currentRepIndex = (currentRepIndex + 1) % (repInstances.length - 1);
         super.move(time, testUnits, thisCanBeAnyInt);
         this.setHealth(health() + heal(), Units.HEAL);
         if (!this.destination().samePlace(this.position())) {//Is moving so cancel third skill
            if (thirdSkill != null) {
               thirdSkill.setActivate(false);
            }
         } else {// Reached destination
            attackCommand = false;
         }

         if (attackCommand) {// Find the nearest current to attack
            if (this.target() == null || this.target().health() <= 0) {// Have nothing to aim for so search for nearest target
               this.setTarget(this.nearestEnemy(game.enemies()));
            }
            if (this.target() == null) {// Nothing to attack, moveFragments
               this.moveWithCollision(time, testUnits);
            } else {//Exists a current nearby, attack it if in range
               if (this.target().position().distance(this.position()) <= range) {//In range
                  this.puckAttack();
               } else {//Out of range, ignore and moveFragments
                  this.setTarget(null);
                  this.moveWithCollision(time, testUnits);
               }
            }
         } else if (this.target() != null) {//Have sth in mind to attack
            if (this.target().health() > 0) {
               if (!this.puckAttack()) {//Could not attack, so moveFragments TOWARDS the target
                  Pointt tam = this.destination().clone();
                  this.updateMovement(this.target().position());
                  this.moveWithCollision(time, testUnits);
                  this.updateMovement(tam);
               }
            } else {
               this.setTarget(null);
               this.updateMovement(this.position());
            }
         } else {//Move normally
            this.moveWithCollision(time, testUnits);
         }
      } else if (Clocks.masterClock.currentTime() - deadTime > respawnTime) {//Respawn
         respawnTime += RESPAWN_TIME_INCREMENT;
         respawn();
      }
   }

   @Override
   public void die(double damagingSpeed) {//Split into fragments
      if (ultimate.activate()) {//Turn off when dead
         ultimate.setActivate(false, null);//Force turn off
      }

      firstSkill.degrade();
      secondSkill.degrade();
      ultimate.degrade();
      this.increaseMaxHealth(-10 * HEALTH_INCREMENT);

      if (secondSkill.activate()) {
         secondSkill.closeOperation();
      }

      synchronized (curses()) {
         curses().clear();
      }

      synchronized (this) {
         deadTime = Clocks.masterClock.currentTime();
         super.die(damagingSpeed);

         synchronized (game.visualEffects()) {
            ArrayList<Area> part;
            for (int i = 0; i < repInstances[10].parts().size(); i++) {
               part = new ArrayList<>();
               part.add(new Area(repInstances[1].parts().get(i)));
               game.visualEffects().add(new UniversalEffect(position().clone(),
                       new RepInstance(part), partColors, UniversalEffect.DEFAULT_COUNT_DOWN,
                       damagingSpeed, Math.random() * Math.PI * 2,
                       UniversalEffect.DECELERATING, UniversalEffect.FADING, UniversalEffect.FIX_SCALE));
            }
         }
      }
   }

   private void respawn() {
      this.setDead(false);
      this.setPosition(STARTING_POSITION.clone());
      this.setTarget(null);
      this.setAttackCommand(false);
      this.setHealable(true);
      this.setSpeed(DEFAULT_SPEED);
      this.setAngularSpeed(ANGULAR_SPEED);
      this.setDestination(this.position().clone());
      this.setHealth(this.maxHealth(), Units.FORCE_CHANGE);
      this.setSkillEnable(true);
      this.setMoveable(true);
   }

   public void clearHold() {
      holdAttack = false;
      holdBlink = false;
      holdFirstSkill = false;
      holdUltimate = false;
   }

   @Override
   public void setSkillEnable(boolean isActive) {
      super.setSkillEnable(isActive);
      if (firstSkill != null) {
         firstSkill.setDisable(!isActive);
      }
      secondSkill.setDisable(!isActive);
      thirdSkill.setDisable(!isActive);
      ultimate.setDisable(!isActive);
   }

   public LivingUnit nearestEnemy(HashSet<LivingUnit> enemies) {//Return nearest current
      LivingUnit output = null;
      double nearestDistance = Double.MAX_VALUE;

      synchronized (enemies) {
         for (LivingUnit current : enemies) {
            if (!current.dead()) {
               if (this.position().distance(current.position()) < nearestDistance) {
                  nearestDistance = this.position().distance(current.position());
                  output = current;
               }
            }
         }
      }
      return output;
   }

   public LivingUnit targetedCreep(Pointt clicked, HashSet<LivingUnit> enemies) {//Return the current that is clicked on
      if (clicked == null) {
         return null;
      }
      Pointt tam;
      synchronized (enemies) {
         for (LivingUnit current : enemies) {
            if (!current.dead()) {
               if (clicked != null) {
                  tam = clicked.clone();
                  tam = tam.getRotated(current.position(), -current.movingAngle());
                  Area creepRep = new Area(current.getRep());
                  creepRep.transform(AffineTransform.getTranslateInstance((int) current.position().getX(), (int) current.position().getY()));
                  Pointt compare = tam.clone();
                  if (current.position().distance(compare) < current.radius()) {
                     return current;
                  }
               }
            }
         }
      }
      return null;
   }

   /**
    * WARNING: Only call this method when Puck is not dead
    * @Return whether puck can attack the current target.
    */
   protected boolean puckAttack() {
      if (this.position().distance(this.target().position()) <= range) { //In range
         long currentTime = Clocks.masterClock.currentTime(); //Check masterClock
         if (currentTime - this.lastAttack() > this.attackSpeed()) { //Attack
            synchronized (projectiles) {
               projectiles.add(new PuckProjectile(game, this.position().clone(), this, this.target()));
            }
            this.setLastAttack(currentTime);
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void processProjectile() {
      synchronized (projectiles()) {
         for (Iterator<Projectile> it = projectiles().iterator(); it.hasNext();) {
            Projectile current = it.next();
            if (current.removing()) {
               it.remove();
            } else {
               current.moveNoCollision(Game.map.PROCESSING_RATE());
            }
         }
      }
   }

   public void processSkills() {
      //First Skill is also processed separatedly
      if (firstSkill != null) {
         if (firstSkill.factor() > FirstSkill.FACTORS.length * 2) {
            firstSkill.clearTask();
            firstSkill = new FirstSkill(); //Create fake first skill to notify that this has finished
         }
      }
      //Second Skill
      //Third Skill
      if (thirdSkill != null) {
         if (Clocks.masterClock.currentTime() - thirdSkill.startTime() > ThirdSkill.SKILL_TIME) {
            thirdSkill.setActivate(false);
         }
      }

      //Ultimate
      if (ultimate != null) {
         ultimate.moveNoCollision(Game.map.PROCESSING_RATE());
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (this.dead()) {
         return;
      }

      super.plot(a, transform, focus);

      if (ultimate != null) {
         ultimate.plot(a, transform, focus);
      }

      a.setTransform(transform);
      a.setPaint(Color.BLACK);
      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.rotate(this.movingAngle());


      if (secondSkill != null && secondSkill.activate()) {//Second skill mode
         repInstances[repInstances.length - 1].plot(a, partColors);
         a.translate(-20, 0);
         for (int i = 0; i < 20; i++) {
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) (i / 20.0)));
            a.setColor(SecondSkill.colors(i));
            a.translate(-10, 0);
            repInstances[repInstances.length - 1].plot(a, partColors);
         }
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      } else {
         int tam;
         boolean thirdSkillActivated = (thirdSkill != null) && thirdSkill.activate();

         if (!thirdSkillActivated) {
            for (int i = 0; i < ThirdSkill.blur().length; i++) {
               a.setComposite(ThirdSkill.blur()[i]);
               tam = (currentRepIndex - i) % (repInstances.length - 1);
               if (tam < 0) {
                  tam += (repInstances.length - 1);
               }
               repInstances[tam].plot(a, partColors);
            }
         }
      }

      plotProjectile(a, transform);
      plotAttackUnits(a, transform, focus);

      //Plot health bar
      a.setTransform(transform);
      plotHealthBar(a, display);
   }

   @Override
   protected void plotAttackUnits(Graphics2D a, AffineTransform transform, Pointt focus) {
      if (firstSkill() != null) {
         firstSkill().plot(a, transform, focus);
      }

      if (secondSkill() != null) {
         secondSkill().plot(a, transform, focus);
      }

      if (thirdSkill() != null) {
      }

      if (ultimate() != null) {
         ultimate().plot(a, transform, focus);
      }
   }

   public static void initialize() {
      standardColors = FileUtilities.getGradient("PuckGradient.txt");
      repInstances = new RepInstance[12];
      int oisi = 0, m, l;
      l = 4;
      for (int i = 0; i < repInstances.length - 1; i++) {// The last element is the second skill rep
         m = oisi + 2;
         oisi = (oisi + 1) % l;
         ArrayList<Area> part = generateRepInstance(40 * (1 - (m % l) / 6.0), 30 * (1 - (m % l) / 6.0), 25 + 10 * (m % l));
         repInstances[i] = new RepInstance(part);
      }

      //Second skill rep
      ArrayList<Area> part = generateRepInstance(40, 30, -50);
      for (int j = 0; j < part.size(); j++) {
         standardColors.add(Color.BLACK);
      }
      repInstances[repInstances.length - 1] = new RepInstance(part);

      ThirdSkill.initialize();
   }

   private static ArrayList<Area> generateRepInstance(double topWingWidth, double botWingWidth, double angle) {
      ArrayList<Area> output = new ArrayList<>();
      AffineTransform normalize = AffineTransform.getScaleInstance(0.5, 0.5);
      normalize.rotate(Math.PI / 2);

      Area body = new Area(new Ellipse2D.Double(-10, -40, 20, 80));

      body.transform(normalize);
      output.add(body);

      //Top wing
      double u = topWingWidth;
      double v = 20;
      Area topWing = new Area(new Ellipse2D.Double(-u, -v, 2 * u, 2 * v));
      topWing.transform(AffineTransform.getRotateInstance(Math.toRadians(-angle)));
      topWing.transform(AffineTransform.getTranslateInstance(25, -17));

      Area mirror = new Area(topWing);
      mirror.transform(AffineTransform.getScaleInstance(-1, 1));
      topWing.transform(normalize);
      mirror.transform(normalize);
      output.add(topWing);
      output.add(mirror);

      //Bot wing
      u = botWingWidth;
      v = 15;
      Area botWing = new Area(new Ellipse2D.Double(-u, -v, 2 * u, 2 * v));
      botWing.transform(AffineTransform.getRotateInstance(Math.toRadians(Math.abs(angle))));
      botWing.transform(AffineTransform.getTranslateInstance(15, 12));

      mirror = new Area(botWing);
      mirror.transform(AffineTransform.getScaleInstance(-1, 1));
      botWing.transform(normalize);
      mirror.transform(normalize);
      output.add(botWing);
      output.add(mirror);

      //Add 2 antena
      GeneralPath hair = new GeneralPath();
      hair.moveTo(0, -20);
      hair.quadTo(0, -75, 40, -50);
      hair.quadTo(0, -73, 0, -20);
      hair.closePath();

      Area antena = new Area(hair);
      mirror = new Area(antena);
      mirror.transform(AffineTransform.getScaleInstance(-1, 1));
      antena.transform(normalize);
      mirror.transform(normalize);
      output.add(antena);
      output.add(mirror);

      return output;
   }

   @Override
   public Area getRep() {
      return new Area(Geometry.createEllipse(0, 0, this.radius() * Geometry.DISPLAY_REAL_RATIO, this.radius() * Geometry.DISPLAY_REAL_RATIO));
   }

   public void resetBonus() {
      bonusType = SKILL_BONUS;
      firstSkill.setBonusType(FirstSkill.DEFAULT_BONUS_TYPE);
      secondSkill.setBonusType(SecondSkill.DEFAULT_BONUS_TYPE);
      thirdSkill.setBonusType(ThirdSkill.DEFAULT_BONUS_TYPE);
      ultimate.setBonusType(Ultimate.DEFAULT_BONUS_TYPE);
   }

   //Setter & Getter Auto-Generated Code

   private boolean blinkable() {
      return moveable() && (Clocks.masterClock.currentTime() - blinkStart >= BLINK_COOL_DOWN);
   }

   public FirstSkill firstSkill() {
      return firstSkill;
   }

   public void setFirstSkill(FirstSkill firstSkill) {
      if (this.firstSkill != null && (!this.firstSkill.available())) {
         return;
      }
      this.firstSkill = firstSkill;
      usedFirstSkill = true;
      firstSkill.schedule();
      Audio.playSound(Audio.FIRST_SKILL);
   }

   public SecondSkill secondSkill() {
      return secondSkill;
   }

   public ThirdSkill thirdSkill() {
      return thirdSkill;
   }

   public void setSecondSkill(SecondSkill secondSkill) {
      if ((this.secondSkill == null) || (this.secondSkill.available())) {
         thirdSkill.setActivate(false);
         this.secondSkill = secondSkill;
      }
   }

   public void setUltimate(Ultimate ultimate) {
      if (this.ultimate == null) {
         this.ultimate = ultimate;
      }
   }

   public void setThirdSkill() {
      if (this.thirdSkill.available()) {
         this.setDestination(this.position());
         this.setTarget(null);
         this.setInvulnerable(true);
         thirdSkill.setActivate(true);
      }
   }

   public Ultimate ultimate() {
      return ultimate;
   }

   @Override
   public void setMoveable(boolean moveable) {
      super.setMoveable(moveable);
   }

   protected void setLastAttack(long time) {
      this.lastAttack = time;
   }

   protected long lastAttack() {
      return lastAttack;
   }

   public void setTarget(LivingUnit targetedCreep, Pointt destination) {
      this.setTarget(targetedCreep);
      this.updateMovement(destination);
   }

   public boolean holdBlink() {
      return holdBlink;
   }

   public boolean holdAttack() {
      return holdAttack;
   }

   public boolean holdFirstSkill() {
      return holdFirstSkill;
   }

   public boolean setHoldBlink(boolean holdBlink) {//Return if can set holdBlink
      if (holdBlink || blinkable()) {
         this.holdBlink = holdBlink;
         return true;
      }
      return false;
   }

   public boolean setHoldAttack(boolean holdAttack) {
      this.holdAttack = holdAttack;
      return true;
   }

   public boolean setHoldFirstSkill(boolean holdFirstSkill) {
      if (((firstSkill != null) && (firstSkill.available())) || !holdFirstSkill) {
         this.holdFirstSkill = holdFirstSkill;
         return true;
      }
      return false;
   }

   public boolean setHoldUltimate(boolean holdUltimate) {
      if (((ultimate != null) && (ultimate.available())) || !holdUltimate) {
         this.holdUltimate = holdUltimate;
         return true;
      }
      return false;
   }

   public boolean attackCommand() {
      return attackCommand;
   }

   public void setAttackCommand(boolean attackCommand) {
      this.attackCommand = attackCommand;
   }

   public long deadTime() {
      return deadTime;
   }

   public byte bonusType() {
      return bonusType;
   }

   public void setBonusType(int bonusType) {
      this.bonusType = (byte) bonusType;
   }

   public boolean holdUltimate() {
      return holdUltimate;
   }

   public static void setInitialRespawnTime(int initialRespawnTime) {
      Puck.initialRespawnTime = initialRespawnTime;
   }

   public static void setInitialDamage(double initialDamage) {
      Puck.initialDamage = initialDamage;
   }

   public static void setInitialNaturalHeal(double initialNaturalHeal) {
      Puck.initialNaturalHeal = initialNaturalHeal;
   }

   public static void setInitialMaxHealth(double initialMaxHealth) {
      Puck.initialMaxHealth = initialMaxHealth;
   }

   public static void setInitialHealth(double initialHealth) {
      Puck.initialHealth = initialHealth;
   }

   public static void setInitialAttackSpeed(double initialAttackSpeed) {
      Puck.initialAttackSpeed = initialAttackSpeed;
   }

   public static void setInitialKill(int kills) {
      Puck.initialKills = kills;
   }

   public long respawnTime() {
      return respawnTime;
   }

   public boolean blinked() {
      return blinked;
   }

   public boolean usedFirstSkill() {
      return usedFirstSkill;
   }

   public boolean usedSecondSkill() {
      return usedSecondSkill;
   }

   public boolean usedThirdSkill() {
      return usedThirdSkill;
   }

   public boolean usedUltimate() {
      return usedUltimate;
   }

   public void setBlinked(boolean blinked) {
      this.blinked = blinked;
   }

   public void setUsedFirstSkill(boolean usedFirstSkill) {
      this.usedFirstSkill = usedFirstSkill;
   }

   public void setUsedSecondSkill(boolean usedSecondSkill) {
      this.usedSecondSkill = usedSecondSkill;
   }

   public void setUsedThirdSkill(boolean usedThirdSkill) {
      this.usedThirdSkill = usedThirdSkill;
   }

   public void setUsedUltimate(boolean usedUltimate) {
      this.usedUltimate = usedUltimate;
   }
}