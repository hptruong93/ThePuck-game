package Main.Units.NonLiving;

import Main.Game;
import Main.ProcessingUnit;
import Utilities.Pointt;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class MessageText extends OnScreenText {

   public static final Font MESSAGE_FONT = new Font("VNI-Algerian", 3, 12);
   public static final int DEFAULT_APPEAR_TIME = 100;
   public static final int DEFAULT_EXIST_TIME = 500;
   private int appearTime, existTime, initialExistTime;

   public MessageText(String content, Font font, Color color, int existTime, int appearTime, Pointt position, double speed) {
      super(content, OnScreenText.BOTTOM_POSITION, font, color, existTime, position, speed, Math.PI / 2, UniversalEffect.STAND_STILL, UniversalEffect.APPEARING);
      setCurrentCountDown(appearTime);
      this.appearTime = appearTime;
      this.existTime = existTime;
      initialExistTime = existTime;
      centerPosition();
   }

   @Override
   public void moveNoCollision(double time) {
      super.moveNoCollision(time);
      if (existTime > 0) {
         existTime--;
      }
   }

   @Override
   public void plot(Graphics2D a, AffineTransform transform) {
      if (existTime > 0) {
         super.plot(a, transform);
      }
   }

   @Override
   protected void blurFunction() {
      setBlur(1 - ((float) currentCountDown() / initialCountDown()));
   }

   private void centerPosition() {
      if (type() != CUSTOM_POSITION) {
         position().setX(ProcessingUnit.SCREEN().getWidth() / 2 - 0.5 * font().getSize() * content().length());
         if (type() == MIDDLE_POSITION) {
            position().setY(0.5 * (ProcessingUnit.SCREEN().getHeight() - Game.map.MINI_SIZEY()) - font().getSize());
         } else if (type() == BOTTOM_POSITION) {
            position().setY(ProcessingUnit.SCREEN().getHeight() - Game.map.MINI_SIZEY() - font().getSize());
         }
      }
   }

   @Override
   public void setContent(String content) {
      super.setContent(content);
      centerPosition();
      setCurrentCountDown(appearTime);
      existTime = initialExistTime;
   }
}