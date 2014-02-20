package Main.Units.NonLiving;

import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class UniversalEffect extends Units {

   private static final double DEFAULT_ANGULAR_SPEED = Math.toRadians(360);
   public static final int DEFAULT_COUNT_DOWN = 25; //2.5 seconds
   public static final byte FIX_SCALE = 0;
   public static final byte GROWING_SCALE = 1;
   public static final byte SHRINKING_SCALE = 2;
   public static final byte FIX_BLUR = 0;
   public static final byte FADING = 1;
   public static final byte APPEARING = 2;
   public static final byte STAND_STILL = 0;
   public static final byte ACCELERATING = 1;
   public static final byte DECELERATING = 2;
   public static final byte ANGULARLY_STILL = 0;
   public static final byte ROTATING = 1;
   private RepInstance instances;
   private int initialCountDown, currentCountDown;
   private double scale, deceleration;
   private float blur;
   private byte accelerationType, blurType, scaleType;
   private boolean finish;

   public UniversalEffect(Pointt position, RepInstance instance, ArrayList<Color> partColors, int fadeCountDown,
           double speed, double movingAngle, byte accelerationType, byte blurType, byte scaleType) {

      this.instances = instance;
      this.setSpeed(Math.abs(speed));
      this.deceleration = speed() / fadeCountDown;
      this.setMovingAngle(movingAngle);
      this.setFinalAngle(movingAngle);
      this.setPosition(position.clone());

      this.initialCountDown = fadeCountDown;
      currentCountDown = initialCountDown;
      this.partColors = partColors;

      scale = 1;
      blur = 1;

      this.accelerationType = accelerationType;
      this.blurType = blurType;
      this.scaleType = scaleType;
   }

   public UniversalEffect(Pointt position, RepInstance instance, Color color, int fadeCountDown,
           double speed, double movingAngle, byte accelerationType, byte blurType, byte scaleType) {
      this.instances = instance;
      this.setSpeed(Math.abs(speed));
      this.deceleration = speed() / fadeCountDown;
      this.setMovingAngle(movingAngle);
      this.setFinalAngle(movingAngle);
      this.setPosition(position.clone());

      this.initialCountDown = fadeCountDown;

      currentCountDown = initialCountDown;

      if (instance != null) {
         partColors = new ArrayList<>();
         for (int i = 0; i < instance.size(); i++) {
            partColors.add(color);
         }
      }

      scale = 1;
      blur = 1;

      this.accelerationType = accelerationType;
      this.blurType = blurType;
      this.scaleType = scaleType;
   }

   @Override
   public void moveNoCollision(double time) {
      if (currentCountDown > 0) {
         positionFunction(time);
         blurFunction();
         scaleFunction();
         currentCountDown--;
      } else {
         finish = true;
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      a.setTransform(transform);

      Pointt display = this.displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.rotate(movingAngle());

      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blur));
      instances.plot(a, partColors, scale);
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
   }

   protected void blurFunction() {
      if (blurType == APPEARING) {
         blur = 1 - ((float) currentCountDown / initialCountDown);
      } else if (blurType == FADING) {
         blur = (float) currentCountDown / initialCountDown;
      } else if (blurType == FIX_BLUR) {
         blur = 1;
      }
   }

   protected void positionFunction(double time) {
      if (accelerationType == DECELERATING) {
         setSpeed(speed() - deceleration);
      } else if (accelerationType == ACCELERATING) {
         setSpeed(speed() + deceleration);
      } else if (accelerationType == STAND_STILL) {
         //Do nothing
      }

      super.moveNoCollision(time);
   }

   protected void rotateFunction() {
      setMovingAngle(movingAngle() + angularSpeed());
   }

   protected void scaleFunction() {
      if (scaleType == GROWING_SCALE) {
         scale = 1 - (double) currentCountDown / initialCountDown;
      } else if (scaleType == SHRINKING_SCALE) {
         scale = (double) currentCountDown / initialCountDown;
      } else if (scaleType == FIX_SCALE) {
         scale = 1;
      }
   }

   public boolean finish() {
      return finish;
   }

   protected float blur() {
      return blur;
   }

   protected void setBlur(float blur) {
      this.blur = blur;
   }

   protected int currentCountDown() {
      return currentCountDown;
   }

   protected int initialCountDown() {
      return initialCountDown;
   }

   protected void setCurrentCountDown(int currentCountDown) {
      this.currentCountDown = currentCountDown;
   }
}