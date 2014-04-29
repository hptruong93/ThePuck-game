package Main.Units.NonLiving;

import Main.Units.RepInstance;
import Main.Units.Units;
import Utilities.Geometry;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

public class DirectionMarker extends Marker {

   private static RepInstance[] instances;
   private static ArrayList<Color> standardColors;
   private static final int RADIUS = 50;

   public static void initialize() {
      instances = new RepInstance[1];
      ArrayList<Area> parts = new ArrayList<>();
      parts.add(new Area(Geometry.createEllipse(0, 0, RADIUS * Geometry.DISPLAY_REAL_RATIO, RADIUS * Geometry.DISPLAY_REAL_RATIO)));
      parts.get(0).subtract(new Area(Geometry.createEllipse(0, 0, RADIUS * Geometry.DISPLAY_REAL_RATIO - 10, RADIUS * Geometry.DISPLAY_REAL_RATIO - 10)));
      instances[0] = new RepInstance(parts);

      standardColors = new ArrayList<>();
      standardColors.add(Color.BLUE);
   }

   public DirectionMarker(Pointt position) {
      super(position);
      setRadius(RADIUS);
   }

   @Override
   public void move(double time, ArrayList<Units> testUnits, int thisCanBeAnyInt) {
      //Do nothing
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) throws UnsupportedOperationException {
      a.setTransform(transform);
      Pointt display = displayPosition(focus);
      a.translate(display.getX(), display.getY());
      currentRepIndex = (currentRepIndex + 1) % instances.length;
      instances[currentRepIndex].plot(a, standardColors);
   }
}
