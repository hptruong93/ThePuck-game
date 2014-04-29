package Main.Units.Living.Puck;

import Main.ProcessingUnit;
import Main.MainScreen;
import Features.Clocks;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class SkillCoolDown extends JComponent{
    private PuckSkill skill;
    private Arc2D rep;
    private Image icon;
    private Color displaying;
    private static final int SHIFTING_CONSTANT = 0;

    public SkillCoolDown(String iconPath, Color displaying, ProcessingUnit map) {
        try {
            Rectangle2D bound = new Rectangle2D.Double(-20, -20, map.MINI_SIZEY()/3+50, map.MINI_SIZEY()/3+50);
            rep = new Arc2D.Double(bound, 90, 360, Arc2D.PIE);
            this.displaying = displaying;
            icon = ImageIO.read(new File(iconPath)).getScaledInstance((int)map.MINI_SIZEY()/3-SHIFTING_CONSTANT, (int)map.MINI_SIZEY()/3-SHIFTING_CONSTANT, Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            Logger.getLogger(SkillCoolDown.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Cannot load Skill CoolDown icon");
        }
    }

    public void update() {
        if (skill == null) {
            rep.setAngleExtent(0);
            return;
        }
        rep.setAngleExtent(360 - 360 * ((double)(Clocks.masterClock.currentTime() - skill.startTime())/skill.coolDown()));
        if (rep.getAngleExtent() <= 0) rep.setAngleExtent(0);
    }

    @Override
    public void paint(Graphics g) {
        this.update();
        Graphics2D a = (Graphics2D) g;
        a.translate(SHIFTING_CONSTANT/2,SHIFTING_CONSTANT/2);
        a.drawImage(icon, null, this);
        a.setPaint(displaying);
        a.translate(-SHIFTING_CONSTANT/2,-SHIFTING_CONSTANT/2);
        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        a.fill(rep);
    }

    public void setSkill(PuckSkill skill) {
        this.skill = skill;
    }
}