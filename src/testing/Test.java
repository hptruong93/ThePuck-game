package testing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import utilities.SpriteSheetReader;

public class Test extends JFrame {
	
	Board board;
	ArrayList<Image> images;
	int index;
	
	public Test() {
		initComponents();
		this.setVisible(true);
	}
	
	private void initComponents() {
		setSize(1000, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.board = new Board();
		this.add(board);
        images = SpriteSheetReader.readImage("data\\img\\BUTTERFLY01.png", 6,3, 500, 500);
        System.out.println(images.size());
	}
	
	public static void main(String[] args) {
		Test t = new Test();
		while (true) {
		t.board.repaint();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	private class Board extends JComponent {
		
		@Override
		public void paint(Graphics g) {
			Graphics2D a = (Graphics2D) g;
			a.setPaint(Color.BLUE);
			a.fill(new Rectangle2D.Double(-500, -500, 1000, 1000));
			
			int width = images.get(index).getWidth(null);
			int height = images.get(index).getHeight(null);
			
			a.drawImage(images.get(index), 100, 100, 100+width, 100+height, 0, 0, width, height, null);
			index = (index + 1) % images.size();
		}
	}
}