package Main.Units.NonLiving;

import Main.Units.Units;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public abstract class Marker extends Units {

   private boolean removeable;

   public static void initialize() {
      DirectionMarker.initialize();
   }

   public Marker(Pointt position) {
      super(position);
      setMoveable(false);
      setHealth(10, Units.FORCE_CHANGE); //Any number greater than 0
   }

   public abstract void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt);

   @Override
   public abstract void plot(Graphics2D a, AffineTransform transform, Pointt focus);

   public boolean removeable() {
      return removeable;
   }

   public final void setRemoveable(boolean removeable) {
      this.removeable = removeable;
   }
}
