package Main.Items;

import Main.Units.Living.LivingUnit;
import Main.Units.Living.Puck.PuckSkill;
import Utilities.Pointt;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Blink extends Item {

   private static final String DEFAULT_NAME = "Blink";

   public Blink() {
      super(DEFAULT_NAME);
   }

   @Override
   protected void schedule() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void run() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   protected void applyEffects(LivingUnit target) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
