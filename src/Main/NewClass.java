package Main;

import Main.Units.RepInstance;
import Utilities.Geometry;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class NewClass extends JFrame {

   public static void main(String[] args) {
      new NewClass();
   }
   static ArrayList<Color> colors;
   static int time = -50;

   public NewClass() {

      long start;
      setSize(950, 1000);
      setLocation(0, 0);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      Board board = new Board();
      this.add(board);

      Scanner scanner = new Scanner(System.in);
      instances = new RepInstance[20];

      start = System.currentTimeMillis();
      generateInstances();
      board.setVisible(true);
      this.setVisible(true);

      for (int i = 0; i < 500; i++) {
         try {
            index = (index + 1) % instances.length;
            board.repaint();
            Thread.sleep(50);
         } catch (InterruptedException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      System.out.println(System.currentTimeMillis() - start);
      System.out.println();

   }

   private void generateInstances() {
      double radius = 25;
      double f = radius / 6;
      double a = 6 * f;
      double b = 3.75 * f;
      double y = 2 * f;
      double x = a * Math.sqrt(1 - (y / b) * (y / b));

      Ellipse2D e1 = new Ellipse2D.Double(-a - x, -b - y, 2 * a, 2 * b);

      double a1 = 3 * f;
      double b1 = 6 * f;
      double y1 = 2 * f;
      double x1 = a1 * Math.sqrt(1 - (y1 / b1) * (y1 / b1));

      Ellipse2D e2 = new Ellipse2D.Double(-a1 - x1, -b1 - y1, 2 * a1, 2 * b1);

      Area ar = new Area(e2);
      ar.subtract(new Area(e1));
      ar.subtract(new Area(new Rectangle2D.Double(-1000, -1000, 5000, 1000)));

      ArrayList<Area> parts = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
         ar.transform(AffineTransform.getRotateInstance(Math.PI/2));
         parts.add(new Area(ar));
      }

      for (int i = 0; i < 20; i++) {
         ArrayList current = new ArrayList<>();
         for (int j = 0; j < parts.size(); j++) {
            Area tam = new Area(parts.get(j));
            tam.transform(AffineTransform.getRotateInstance(-2 * i * Math.PI/20));
            current.add(tam);
         }
         instances[i] = new RepInstance(current);
      }
   }
   static RepInstance[] instances;
   static int index = -1;

   private class Board extends JComponent {

      @Override
      public void paint(Graphics g) {
         Graphics2D a = (Graphics2D) g;
         a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         AffineTransform af = new AffineTransform();
         af.translate(500, 300);
         int scale = 5;
         af.scale(scale, scale);
         a.setTransform(af);
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
         double radius = 40;
         Area whole = new Area();

         instances[index].plot(a, Color.BLACK);

         a.setPaint(Color.BLACK);
         a.fill(whole);
         a.setTransform(af);
         a.setPaint(Color.RED);
//         a.fill(con(0, 0, 5, 5));
         a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
         a.fill(con(0, 0, radius, radius));
      }
   }

   private static Area roundRec(double cenX, double cenY, double width, double height) {
      return new Area(new RoundRectangle2D.Double(cenX - width, cenY - height, 2 * width, 2 * height, 15, 15));
   }

   private static Ellipse2D con(double cenX, double cenY, double width, double height) {
      return new Ellipse2D.Double(cenX - width, cenY - height, 2 * width, 2 * height);
   }

   private static Area rec(double cenX, double cenY, double width, double height) {
      Area tam = new Area(new Rectangle2D.Double(cenX - width, cenY - height, 2 * width, 2 * height));
      return tam;
   }
}