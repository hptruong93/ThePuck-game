package main.userInterface;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;

import main.engineInterface.GameGraphics;

public class GameScreen extends JFrame {
	
	private Board board;
	
	public GameScreen() {
		initComponents();
	}
	
	private void initComponents() {
		setSize(1000, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		board = new Board();
		this.add(board);
		
		board.addMouseListener(new MouseResponse(board));
	}

	public void repaintBoard() {
		board.repaint();
	}
	
	private static class Board extends JComponent {
		
		@Override
		public void paint(Graphics g) {
			GameGraphics.plotAll(g);
		}
	}
}
