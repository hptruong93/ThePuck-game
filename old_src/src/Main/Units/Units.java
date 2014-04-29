package Main.Units;

import Main.Game;
import Main.Units.Living.Creep;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Units {

   private Pointt position;
   private Pointt destination;
   private Pointt collidePosition;
   private double speed;
   private double xVelocity; //Per second
   private double yVelocity;
   private double movingAngle;
   private double finalAngle;
   private double angularSpeed;
   private boolean transparent; //Transparent unit does not block other units
   private boolean moveable;
   private double radius; // This is the real radius
   private double maxHealth;
   private double health;
   private double damage;
   protected ArrayList<Color> partColors;
   protected int currentRepIndex;
   public static double MAX_ANGULAR_SPEED = Math.toRadians(360);
   public static double MAX_MOVEMENT_SPEED = 0.15;
   private static double REPEL_SPEED = 0.08;
   public static final int MAGICAL_DAMAGE = 1;
   public static final int PHYSICAL_DAMAGE = 2;
   public static final int PURE_DAMAGE = 3;
   public static final int FORCE_CHANGE = 4;
   public static final int HEAL = 5;

   protected Units() {
      moveable = true;
   }

   protected Units(Pointt position) {
      moveable = true;
      this.position = position;
      partColors = new ArrayList<>();
   }

   protected Units(Pointt position, double movingAngle, double health) {
      moveable = true;
      this.position = position.clone();
      this.xVelocity = 10;
      this.yVelocity = 10;
      this.movingAngle = movingAngle;
      this.finalAngle = movingAngle;
      this.health = health;
      this.maxHealth = health;
      partColors = new ArrayList<>();
   }

   //Move given a set of static units
   public void moveWithCollision(double time, ArrayList<Units> testUnits) {
      //Scale speed and angular speed

      //Normal moveNoCollision with collision check
      if (!moveable) {
         return;
      } else if (testUnits == null || testUnits.isEmpty()) {
         this.moveNoCollision(time);
         return;
      }

      ArrayList<Units> collide, proposedMoveCollide;
      double suggestedAngle;
      Units clone = this.clone();
      clone.speed = Math.min(clone.speed, MAX_MOVEMENT_SPEED);
      clone.angularSpeed = Math.min(clone.angularSpeed, MAX_ANGULAR_SPEED);

      //Static stuck check
      collide = clone.testOfCollision(this, testUnits, STATIC_COLLISION);
      if (!collide.isEmpty()) {
         suggestedAngle = collide.get(0).position.angle(position);
         if (suggestedAngle == Math.PI / 2) {
            suggestedAngle = Math.random() * 2 * Math.PI;
         }

         clone.movingAngle = suggestedAngle;
         clone.finalAngle = suggestedAngle;
         clone.destination = null;
         clone.speed = REPEL_SPEED;

         clone.moveNoCollision(time);
         this.position = clone.position;
         updateMovement(destination);
         return;
      }

      //Moving stuck check
      clone.moveNoCollision(time);
      collide = clone.testOfCollision(this, testUnits, MOVING_COLLISION); //Test if collision occurs

      if (collide.isEmpty()) {//Good to go
         this.position = clone.position;
         this.movingAngle = clone.movingAngle;
         collidePosition = null;
      } else {//If collide, choose one collided object and moveNoCollision parrallel to that object's tangent

         if (collidePosition == null) {
            collidePosition = this.position.clone();
         }

         for (int i = 0; i < collide.size(); i++) {
            Units current = collide.get(i);
            suggestedAngle = tangentialAngle(current);
            boolean collideTest2 = false;
            do {
               clone = this.clone();
               clone.speed = Math.min(clone.speed, MAX_MOVEMENT_SPEED);
               clone.angularSpeed = Math.min(clone.angularSpeed, MAX_ANGULAR_SPEED);
               clone.movingAngle = suggestedAngle;
               clone.finalAngle = suggestedAngle;
               clone.moveNoCollision(time);
               //If suggested movement causes collision, moveNoCollision in the other tangential direction
               proposedMoveCollide = clone.testOfCollision(this, testUnits, MOVING_COLLISION);
               if (!proposedMoveCollide.isEmpty()) { //Collide. Move in other tangential direction
                  for (int k = 0; k < proposedMoveCollide.size(); k++) {
                     if (!collide.contains(proposedMoveCollide.get(k))) {
                        collide.add(proposedMoveCollide.get(k));
                     }
                  }

                  suggestedAngle += Math.PI;
                  //If still colide in other tangential direction, cannot moveNoCollision
                  if (collideTest2) {
                     break;
                  }
                  collideTest2 = true;
               } else {//Not collide then check if moving away from collide position
                  if (collidePosition != null) {
                     if (clone.position.distance(collidePosition) > this.position.distance(collidePosition)) {//Found a possible movement. i.e. away from collide position
                        position = clone.position;
                        return; //Job done. End method
                     } else {
                        suggestedAngle += Math.PI;
                        if (collideTest2) {
                           break;
                        }
                        collideTest2 = true;
                     }
                  } else {
                     position = clone.position;
                     return;
                  }
               }
            } while (true);
         }
         //Reaching this point means no movement found. Reset the collidePosition and try again
         collidePosition = this.position.clone();
      }
   }

   private static final int STATIC_COLLISION = 0;
   private static final int MOVING_COLLISION = 1;

   // Given list of objects, test for collision (excluding "this" object)
   private ArrayList<Units> testOfCollision(Units original, ArrayList<Units> testUnits, int testType) {
      ArrayList<Units> output = new ArrayList<>();
      for (int i = 0; i < testUnits.size(); i++) {
         Units current = testUnits.get(i);
         if (current != original && !current.transparent) {
            if (current.position.distance(this.position) < (current.radius + original.radius)) {//Collide so add to the collision list
               output.add(current);
               if (testType == STATIC_COLLISION) {
                  return output;
               }
            }
         }
      }
      return output;
   }

   private double tangentialAngle(Units testUnit) {
      double output;

      double side = (destination.getY() - position.getY()) * (position.getX() - testUnit.position.getX()) - (position.getY() - testUnit.position.getY()) * (destination.getX() - position.getX());
      //Change angle to the tangential angle
      output = Geometry.arcTan(position.getY() - testUnit.position.getY(), position.getX() - testUnit.position.getX(), position.getX() - testUnit.position.getX()) - Math.PI / 2;

      if (side >= 0) {
         output += Math.PI;
      }

      return output;
   }

   /**
    * Movement given no collision
    */
   synchronized public void moveNoCollision(double time) {
      if (time <= 0) {
         throw new InvalidParameterException("Cannot move the unit, time <= 0!");
      }

      if (Double.isNaN(time)) {
         throw new RuntimeException("time is NaN. speed is " + speed + "  moving angle is " + movingAngle);
      }

      // Check if the unit is facing the required direction
      if (movingAngle == finalAngle) {// Already facing that direction
         if (this.destination != null) {
            if (speed * time >= position.distance(destination)) {
               xVelocity = 0;
               yVelocity = 0;
               position = destination;
               return;
            }
         }

         xVelocity = speed * Math.cos(movingAngle);
         yVelocity = speed * Math.sin(movingAngle);

         position.setX(Geometry.fixX(position.getX() + xVelocity * time));
         position.setY(Geometry.fixY(position.getY() + yVelocity * time));

      } else { //Need to turn to that direction
         double distance = finalAngle - movingAngle;
         double alternative = -(2 * Math.PI - finalAngle + movingAngle);

         if (distance < 0) {
            while (alternative <= -Math.PI) {
               alternative += 2 * Math.PI;
            }
         } else {
            while (alternative >= Math.PI) {
               alternative -= 2 * Math.PI;
            }
         }

         double angularDisplacement;
         if (Math.abs(distance) < Math.abs(alternative)) {
            angularDisplacement = distance;
         } else {
            angularDisplacement = alternative;
         }

         if (angularSpeed * time < Math.abs(angularDisplacement)) { //Does not finish turning
            movingAngle += ((angularDisplacement) / Math.abs(angularDisplacement)) * angularSpeed * time;
            xVelocity = 0;
            yVelocity = 0;
         } else { // Finish turning with extra or no time left
            movingAngle = finalAngle;

            if (Double.isNaN(angularSpeed)) {
               throw new RuntimeException("Angular speed is NaN. angulardisplacement is " + angularDisplacement);
            }

            if (Double.isInfinite(angularSpeed)) {
               throw new RuntimeException("Angular speed is isInfinite. angulardisplacement is " + angularDisplacement);
            }

            if (Double.isNaN(angularDisplacement)) {
               throw new RuntimeException("angularDisplacement is NaN. angularSpeed is " + angularSpeed);
            }

            if (Double.isNaN(time - Math.abs(angularDisplacement) / angularSpeed)) {
               throw new RuntimeException("Next input is NaN. angularSpeed is " + angularSpeed + " angularDisplacement is " + angularDisplacement
                       + "time is " + time);
            }

            moveNoCollision(time - Math.abs(angularDisplacement) / angularSpeed);
         }
      }
   }

   //Update heading direction
   public void updateMovement(Pointt destination) {
      this.destination = destination;
      finalAngle = Geometry.arcTan(destination.getY() - position.getY(), destination.getX() - position.getX(), destination.getX() - position.getX());
   }

   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Can not plot because of ambiguous Units! No representation found. Identify the Units!");
   }

   public Shape getRep() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Ambiguous Units! No representation found. Identify the Units! " + this);
   }

   protected Shape healthBar() {
      final int UPPER_DISTANCE = 10;
      final int HEIGHT = 10;
      return new Rectangle2D.Double(-Pointt.realToDisplay(radius), -Pointt.realToDisplay(radius) - UPPER_DISTANCE - HEIGHT, 2 * Pointt.realToDisplay(radius), HEIGHT);
   }

   protected Shape realHealthBar() {
      final int UPPER_DISTANCE = 10;
      final int HEIGHT = 10;
      try {
         return new Rectangle2D.Double(-Pointt.realToDisplay(radius), -Pointt.realToDisplay(radius) - UPPER_DISTANCE - HEIGHT, 2 * Pointt.realToDisplay(radius) * ((double) health / maxHealth), HEIGHT);
      } catch (ArithmeticException ex) {
         return null;
      }
   }

   protected double realRadius() {
      throw new RuntimeException("Unspecified units. Cannot determine real Radius");
   }

   public double radius() {// Return the real radius
      return radius;
   }

   @Override
   protected Units clone() {
      Units output = new Units(this.position.clone(), this.movingAngle, this.health);
      output.destination = this.destination.clone();
      output.speed = this.speed;
      output.xVelocity = this.xVelocity;
      output.yVelocity = this.yVelocity;
      output.finalAngle = this.finalAngle;
      output.angularSpeed = this.angularSpeed;

      return output;
   }

   public interface RepGenerator {

      public void generateInstances();
   }

   //Getter & Setter Auto-Generated Code
   protected void setRadius(double radius) {
      this.radius = radius;
   }

   public double movingAngle() {
      return movingAngle;
   }

   public void setMovingAngle(double movingAngle) {
      this.movingAngle = movingAngle;
   }

   public void setPosition(Pointt position) {
      this.position = position;
   }

   public void setXVelocity(double xVelocity) {
      this.xVelocity = xVelocity;
   }

   public void setYVelocity(double yVelocity) {
      this.yVelocity = yVelocity;
   }

   public void setHealth(double health, int notApplicable) {
      this.health = health;
   }

   public Pointt position() {
      return position;
   }

   public double distance(Units otherUnit) {
      return position.distance(otherUnit.position);
   }

   public Pointt displayPosition(Pointt focus) {
      return position.realToDisplay(focus);
   }

   public Pointt miniPosition() {
      return position.realToMini();
   }

   public double health() {
      return health;
   }

   public void setSpeed(double speed) {
      this.speed = speed;
   }

   public Pointt destination() {
      return destination;
   }

   public void setDestination(Pointt destination) {
      this.destination = destination;
   }

   public double finalAngle() {
      return finalAngle;
   }

   public void setFinalAngle(double finalAngle) {
      this.finalAngle = finalAngle;
   }

   public double angularSpeed() {
      return angularSpeed;
   }

   public void setAngularSpeed(double angularSpeed) {
      this.angularSpeed = angularSpeed;
   }

   public double speed() {
      return speed;
   }

   public double maxHealth() {
      return maxHealth;
   }

   public void setMaxHealth(double maxHealth) {
      this.maxHealth = maxHealth;
   }

   public double damage() {
      return damage;
   }

   public void setDamage(double damage) {
      this.damage = damage;
   }

   public boolean moveable() {
      return moveable;
   }

   public void setMoveable(boolean moveable) {
      this.moveable = moveable;
   }

   public ArrayList<Color> partColors() {
      return partColors;
   }

   public boolean transparent() {
      return transparent;
   }

   public void setTransparent(boolean transparent) {
      this.transparent = transparent;
   }
}