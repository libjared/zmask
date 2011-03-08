/*
 * ImagePanelAction.java
 * Copyright (C) 2010-2011  Jonas Eriksson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.zkt.zmask;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JViewport;

/**
 * Handle actions on image panels
 *
 * @author zqad
 */
public class ImagePanelAction implements MouseListener, MouseMotionListener,
	MouseWheelListener, KeyListener {
	ImagePanel ip;
	boolean buttonPressed = false;
	int startX = 0;
	int startY = 0;
	int width = 0;
	int height = 0;

	public ImagePanelAction(ImagePanel ip) {
		this.ip = ip;
	}

	public void mousePressed(MouseEvent me) {
		buttonPressed = true;
		startX = normalizeX(me.getX());
		startY = normalizeY(me.getY());
		State.setCursorPositionPressed(ip, startX, startY, startX, startY);
		ip.updateTool(startX, startY, startX, startY, me.getButton(),
			me.getModifiers());
	}

	public void mouseReleased(MouseEvent me) {
		buttonPressed = false;
		State.setCursorPositionHover(ip, normalizeX(me.getX()),
			normalizeY(me.getY()));
		ip.commitTool(me.getModifiers(), me.getButton());
	}

	public void mouseMoved(MouseEvent me) {
		int x = normalizeX(me.getX());
		int y = normalizeY(me.getY());
		ip.updateStatusbar(x, y);
	}

	public void mouseExited(MouseEvent me) {
		ip.updateStatusbar(-1, -1);
	}

	public void mouseEntered(MouseEvent me) {
		if (buttonPressed) {
			if (me.getButton() == MouseEvent.BUTTON1) {
				mouseDragged(me);
				return;
			}
			else {
				// Button released while cursor outside of panel
				buttonPressed = false;
				ip.commitTool(me.getModifiers(), me.getButton());
			}
		}
		State.setCursorPositionHover(ip, normalizeX(me.getX()),
			normalizeY(me.getY()));
	}

	public void mouseDragged(MouseEvent me) {
		int currentX = normalizeX(me.getX());
		int currentY = normalizeY(me.getY());
		ip.updateTool(startX, startY, currentX, currentY, me.getButton(),
			me.getModifiers());
		ip.updateStatusbar(currentX, currentY);

		// Should we scrollIncremental?
		JViewport vp = ip.getScrollPane().getViewport();
		Rectangle vpRectangle = vp.getViewRect();
		int mouseX = me.getX();
		int mouseY = me.getY();
		int vpLeftX = (int)vpRectangle.getX();
		int vpUpY = (int)vpRectangle.getY();
		int vpRightX = vpLeftX + (int)vpRectangle.getWidth();
		int vpDownY = vpUpY + (int)vpRectangle.getHeight();
		if (State.getCurrentTool() != State.Tools.HAND_TOOL) { // TODO!
			if (mouseX < vpLeftX) {
				ip.scrollIncremental(ImagePanel.Direction.DIRECTION_LEFT, vpLeftX - mouseX, 1);
			}
else if (mouseX > vpRightX) {
	ip.scrollIncremental(ImagePanel.Direction.DIRECTION_RIGHT, mouseX - vpRightX, 1);
}
			if (mouseY < vpUpY) {
				ip.scrollIncremental(ImagePanel.Direction.DIRECTION_UP, vpUpY - mouseY, 1);
			}
else if (mouseY > vpDownY) {
	ip.scrollIncremental(ImagePanel.Direction.DIRECTION_DOWN, mouseY - vpDownY, 1);
}
		}
	}

	public void mouseClicked(MouseEvent me) {
		// Ignored
	}

	public void mouseWheelMoved(MouseWheelEvent mwe) {
		int rotation = mwe.getWheelRotation();

		if ((mwe.getModifiers() & MouseWheelEvent.CTRL_MASK) != 0) {
			// Zoom
			if (rotation > 0) {
				ip.zoomOut(Math.abs(rotation), ip.getMousePosition(), ip.getScrollPane().getMousePosition());
			}
			else if (rotation < 0) {
				ip.zoomIn(Math.abs(rotation), ip.getMousePosition(), ip.getScrollPane().getMousePosition());
			}
		}
		else {
			/* Default java scrollIncremental is *painfully* slow. I suspect
			 * that this is due to a platform constant that dictates
			 * how many lines that should be scrolled on one "click",
			 * and that java uses the same value for pixels as
			 * lines of text. This fixes this irritating problem,
			 * and sets one unit to 10% of the width/height of the
			 * viewport respectively for th scrollIncremental direction.
			 */

			// Get values and calculate new viewport position
			if ((mwe.getModifiers() & MouseWheelEvent.SHIFT_MASK) == 0) {
				// Shift down-up
				if (rotation < 0) {
					ip.scrollIncremental(ImagePanel.Direction.DIRECTION_UP, 0, Math.abs(rotation));
				}
				else if (rotation > 0) {
					ip.scrollIncremental(ImagePanel.Direction.DIRECTION_DOWN, 0, Math.abs(rotation));
				}
			}
			else {
				// Shift left-right
				if (rotation < 0) {
					ip.scrollIncremental(ImagePanel.Direction.DIRECTION_LEFT, 0, Math.abs(rotation));
				}
				else if (rotation > 0) {
					ip.scrollIncremental(ImagePanel.Direction.DIRECTION_RIGHT, 0, Math.abs(rotation));
				}
			}

		}
	}




	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.ip.abortTool();
		}
	}

	public void keyReleased(KeyEvent ke) {
		// Ignored
	}

	public void keyTyped(KeyEvent ke) {
		// Ignored
	}

	public void setWidth(int maxWidth) {
		this.width = maxWidth;
	}

	public void setHeight(int maxHeight) {
		this.height = maxHeight;
	}

	/**
	 * Normalize x to image dimensions
	 *
	 * @param x x coordinate
	 * @return normalized value of x
	 */
	public int normalizeX(int x) {
		// Recalculate to account for displacement
		Point disp = ip.getDisplacement();
		x -= disp.x;

		/* Normalize x variable to be within 0 and height
		 * When the cursor exits the window, the values of the X and
		 * Y coordinates needs to be normalized for them to not go
		 * outside the image bonds. */
		x = x < 0 ? 0 : Math.min(x, ip.getUIImageWidth() - 1);

		return x;
	}

	/**
	 * Normalize y to image dimensions
	 *
	 * @param y y coordinate
	 * @return normalized value of y
	 */
	public int normalizeY(int y) {
		// Recalculate to account for displacement
		Point disp = ip.getDisplacement();
		y -= disp.y;

		// See normalizeX
		y = y < 0 ? 0 : Math.min(y, ip.getUIImageHeight() - 1);

		return y;
	}

}
