package Main.Units;

import com.sun.media.sound.AiffFileReader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

public class RepInstance {

    private ArrayList<Area> parts;

    public RepInstance(ArrayList<Area> parts) {
        this.parts = parts;
    }

    public RepInstance(ArrayList<Area> parts, double scale) {
       Area temp;
       this.parts = new ArrayList<>();
       for (int i = 0; i < parts.size(); i++) {
          temp = new Area(parts.get(i));
          temp.transform(AffineTransform.getScaleInstance(scale, scale));
          this.parts.add(temp);
       }
    }

    /**
     *
     * @warning: Have to rotate and translate the current Graphics properly before using this
     */
    public void plot(Graphics2D a, ArrayList<Color> partColors) {
        for (int i = 0; i < parts.size(); i++) {
            a.setPaint(partColors.get(i));
            a.fill(parts.get(i));
        }
    }

    public void plot(Graphics2D a, Color color) {//Uniform color
        a.setPaint(color);
        for (int i = 0; i < parts.size(); i++) {
            a.fill(parts.get(i));
        }
    }

    public void plot(Graphics2D a, ArrayList<Color> partColors, double scale) {
       a.scale(scale, scale);
       plot(a, partColors);
    }

    public void plot(Graphics2D a) {//Already set up color
        for (int i = 0; i < parts.size(); i++) {
            a.fill(parts.get(i));
        }
    }

    public ArrayList<Area> parts() {
        return parts;
    }

    public int size() {
        return parts.size();
    }
}