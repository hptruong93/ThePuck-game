package main.userInterface;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import main.engineInterface.GameControl;
import utilities.geometry.Point;

public class MouseResponse extends MouseAdapter {

	private final JComponent board;
	private final GameControl gameControl;
	
	public MouseResponse(JComponent board) {
		this.board = board;
		this.gameControl = new GameControl();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point clicked = new Point(e.getPoint()).displayToReal(gameControl.focus());
		gameControl.mouseClicked(clicked, e.getModifiers());
	}
}
