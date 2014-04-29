package Main.Units.NonLiving;

import Utilities.Pointt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public abstract class OnScreenText extends UniversalEffect {

   public static final byte MIDDLE_POSITION = 0;
   public static final byte BOTTOM_POSITION = 1;
   public static final byte CUSTOM_POSITION = 2;
   private byte type;
   private Font font;
   private Color color;
   private String content;

   public OnScreenText(String content, byte type, Font font, Color color, int countDown, Pointt position, double speed, double movingAngle, byte accelerating, byte blurType) {
      super(position, null, color, countDown, speed, movingAngle, accelerating, blurType, UniversalEffect.FIX_SCALE);
      this.font = font;
      this.color = color;
      setPosition(position);
      this.type = type;
      this.content = content;
   }

   public void plot(Graphics2D a, AffineTransform transform) {
      a.setTransform(transform);
      a.setFont(font);
      a.setColor(color);
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blur()));
      a.translate(position().getX(), position().getY());
      a.drawString(content, 0, 0);
   }

   public void setContent(String content) {
      this.content = content;
   }

   protected byte type() {
      return type;
   }

   protected Font font() {
      return font;
   }

   protected String content() {
      return content;
   }
}