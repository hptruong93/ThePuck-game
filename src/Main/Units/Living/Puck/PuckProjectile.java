package Main.Units.Living.Puck;

import Main.Game;
import Main.Units.Living.LivingUnit;
import Main.Units.NonLiving.ChasingProjectile;
import Main.Units.Units;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class PuckProjectile extends ChasingProjectile {

   private static final double SPEED = 0.1;
   private static final Area REP = new Area(new Ellipse2D.Double(-5, -5, 10, 10));

   public PuckProjectile(Game game, Pointt position, Puck owner, LivingUnit chase) {
      super(game, position, SPEED, owner, chase);
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform, Pointt focus) {
      Pointt display;

      a.setTransform(transform);
      display = displayPosition(focus);
      a.translate(display.getX(), display.getY());
      a.setPaint(Color.MAGENTA);
      a.fill(getRep());
   }

   @Override
   public Area getRep() {
      return REP;
   }

   @Override
   protected void addVisualEffects() {
      //No visual effect
   }
   //Getter Auto-generated code

   @Override
   public Puck owner() {
      return (Puck) super.owner();
   }
}
