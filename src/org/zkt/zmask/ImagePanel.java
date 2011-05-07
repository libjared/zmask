/*
 * ImagePanel.java
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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.zkt.zmask.tools.Hand;
import org.zkt.zmask.tools.Select;
import org.zkt.zmask.tools.Tool;
import org.zkt.zmask.tools.Zoom;

/**
 * Panel used to display an image
 *
 * @author zqad
 */
public class ImagePanel extends JPanel {
	public static final long serialVersionUID = 1;

	private final double ZOOM_MAX = Math.pow(1.25, 7);
	private final double ZOOM_MIN = Math.pow(0.8, 7);
	private Image image;
	private ImageWindow parentImageWindow;
	private Statusbar statusbar;
	private ImagePanelAction listener;
	private double zoom = 1.0;
	private Tool currentTool;

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		// Limit zoom range
		if (zoom < ZOOM_MIN) {
			zoom = ZOOM_MIN;
		}
		else if (zoom > ZOOM_MAX) {
			zoom = ZOOM_MAX;
		}

		this.zoom = zoom;

		// Update with zoomed size
		Dimension d = new Dimension((int)(image.getImageWidth() * zoom),
			(int)(image.getImageHeight() * zoom));
		setSize(d);
		setPreferredSize(d);

		// Refresh grahpical elements
		repaint();
		parentImageWindow.getScrollPane().revalidate();
		statusbar.update();
	}

	public ImagePanel(BufferedImage bi, ImageWindow parentImageWindow) {
		super();

		// Create and save an Image object
		this.image = new Image(bi, this);

		// Save parentImageWindow
		this.parentImageWindow = parentImageWindow;

		// Get and set the current tool
		setTool(State.getCurrentTool());

		// Get statusbar
		statusbar = State.getStatusbar();

		// Register mouse listener
		listener = new ImagePanelAction(this);
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addKeyListener(listener);
		addMouseWheelListener(listener);

		// Update size from image data
		updateSize();
	}

	public void updateSize() {
		// Set panel size = bi size
		int width = image.getImageWidth();
		int height = image.getImageHeight();
		this.setPreferredSize(new Dimension((int)(width * zoom), (int)(height * zoom)));
		this.setSize(new Dimension((int)(width * zoom), (int)(height * zoom)));
		listener.setWidth(width);
		listener.setHeight(height);

		// Update scrollpane
		JScrollPane sp = parentImageWindow.getScrollPane();
		if (sp != null) {
			sp.revalidate();
		}
	}

	public Image getImage() {
		return image;
	}

	public JScrollPane getScrollPane() {
		return parentImageWindow.getScrollPane();
	}

	public boolean isChanged() {
		return image.isChanged();
	}

	public int getImageWidth() {
		return image.getImageWidth();
	}

	public int getImageHeight() {
		return image.getImageHeight();
	}

	public Point getDisplacement() {
		// Calculate X and Y displacement as result of a too large window
		Dimension winDimension = getSize();
		int borderWidth = (int)(winDimension.getWidth() - image.getImageWidth() * zoom);
		int borderHeight = (int)(winDimension.getHeight() - image.getImageHeight() * zoom);

		int dispX = 0;
		int dispY = 0;
		if (borderWidth > 0) {
			dispX = borderWidth / 2;
		}
		if (borderHeight > 0) {
			dispY = borderHeight / 2;
		}

		return new Point(dispX, dispY);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		// Paint background layer
		g2.fillRect(0, 0, getWidth(), getHeight());

		// Get displacement
		Point disp = getDisplacement();
		// Note: it was either multiply by zoom everywhere else or
		// divide only here. This will render the least amount of flops.
		disp.setLocation(disp.x / zoom, disp.y / zoom);

		// Save transform, set zoom
		AffineTransform at = g2.getTransform();
		g2.scale(zoom, zoom);

		// Paint the image
		image.drawImage(g2, disp.x, disp.y);

		// Paint the selection
		Rectangle selection = image.getSelection();
		if (selection != null && !(currentTool instanceof Select)) {
			g2.setStroke(Select.STROKE);
			g2.drawRect((int)selection.getX() + disp.x, (int)selection.getY() + disp.y,
				(int)selection.getWidth(), (int)selection.getHeight());
		}

		// Paint the tool layer
		if (currentTool != null)
			currentTool.draw(g2, disp);

		// Reset transform
		g2.setTransform(at);
	}

	public void updateTool(int srcX, int srcY, int dstX, int dstY,
			int mouseButton, int modifiers) {
		// Recalculate to account for zooming
		srcX /= zoom;
		srcY /= zoom;
		dstX /= zoom;
		dstY /= zoom;

		// Create tool object if nonexistent
		if (currentTool == null) {
			switch (State.getCurrentTool()) {
				case SELECT_TOOL:
					currentTool = new Select(srcX, srcY,
						image, mouseButton, modifiers);
					break;
				case ZOOM_TOOL:
					currentTool = new Zoom(srcX, srcY,
						image, mouseButton, modifiers);
					break;
				case HAND_TOOL:
					currentTool = new Hand(srcX, srcY,
						image, mouseButton, modifiers);
					break;
			}
		}

		// Update with destination coordinates and current modifiers
		currentTool.update(dstX, dstY, modifiers);

		// Update cursor position
		State.setCursorPositionPressed(this, srcX, srcY,
			dstX, dstY);

		this.repaint();
	}

	public void commitTool(int modifiers, int button) {
		currentTool.commit(modifiers);
		if (currentTool.isUndoable()) {
			image.addTool(currentTool, null);
		}
		currentTool = null;
	}

	public void updateStatusbar(int x, int y) {
		if (x < 0 || y < 0) {
			statusbar.update(-1, -1);
		}
		else {
			x /= zoom;
			y /= zoom;
			statusbar.update(x, y);
		}
	}

	public void zoom(int n, Point imagePoint, Point windowPoint) {
		if (n < 0) {
			zoomOut(Math.abs(n), imagePoint, windowPoint);
		}
		else if (n > 0) {
			zoomIn(n, imagePoint, windowPoint);
		}
	}

	public void zoomIn(int n, Point imagePoint, Point windowPoint) {
		factorZoom(1.25, n, imagePoint, windowPoint);
	}

	public void zoomOut(int n, Point imagePoint, Point windowPoint) {
		factorZoom(0.8, n, imagePoint, windowPoint);
	}

	private void factorZoom(double factor, int times, Point imagePoint, Point windowPoint) {
		// We must calculate what zoom we got, since setZoom enforces
		// the zooming limits
		double prevZoom = zoom;
		setZoom(zoom * Math.pow(factor, times));
		double postZoom = zoom / prevZoom;

		if (imagePoint != null) {
			// We need to add one before flooring, if not the image
			// scrolls while moving. 0.5 is not enough for whatever
			// reason.
			imagePoint.setLocation(Math.floor(imagePoint.x * postZoom + 1),
				Math.floor(imagePoint.y * postZoom + 1));
			scrollImagePointToWindowPoint(imagePoint, windowPoint);
		}
	}

	public void setTool(State.Tools tool) {
		switch (tool) {
			case SELECT_TOOL:
				this.setCursor(Select.CURSOR);
			break;
			case ZOOM_TOOL:
				this.setCursor(Zoom.CURSOR);
			break;
			case HAND_TOOL:
				this.setCursor(Hand.CURSOR);
			break;
			default:
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
		}

	}

	public void abortTool() {
		repaint();
	}

	public enum Direction {
		DIRECTION_UP,
		DIRECTION_DOWN,
		DIRECTION_LEFT,
		DIRECTION_RIGHT
	}

	@SuppressWarnings("fallthrough")
	public void scrollIncremental(Direction d, int atLeastPixels, int atLeastJumps) {
		JViewport vp = getScrollPane().getViewport();
		Rectangle vpRectangle = vp.getVisibleRect();
		Point p = vp.getViewPosition();

		int x = (int)p.getX();
		int y = (int)p.getY();
		int vpWidth = (int)vpRectangle.getWidth();
		int vpHeight = (int)vpRectangle.getHeight();
		double steps = 0.1; // Percent of viewport as default unit

		int jumps = 1;
		switch (d) {
			case DIRECTION_UP:
				steps *= -1;
			case DIRECTION_DOWN:
				int maxHeight = getHeight() - vpHeight;
				steps *= vpHeight;
				jumps = (int)steps * atLeastJumps;
				while (Math.abs(jumps) < atLeastPixels) {
					jumps += (int)steps;
				}
				y += jumps;
				if (y >= maxHeight) {
					y = maxHeight;
				}
				else if (y < 0) {
					y = 0;
				}
				break;
			case DIRECTION_LEFT:
				steps *= -1;
			case DIRECTION_RIGHT:
				int maxWidth = getWidth() - vpWidth;
				steps *= vpWidth;
				jumps = (int)steps * atLeastJumps;
				while (Math.abs(jumps) < atLeastPixels) {
					jumps += (int)steps;
				}
				x += jumps;
				if (x >= maxWidth) {
					x = maxWidth;
				}
				else if (x < 0) {
					x = 0;
				}
			break;
		}

		// Set new viewport position
		vp.setViewPosition(new Point(x, y));
	}

	public void scrollImagePointToWindowPoint(Point imagePoint, Point windowPoint) {
		int panelWidth = getWidth();
		int panelHeight = getHeight();

		if (windowPoint == null) {
			windowPoint = new Point(panelWidth / 2, panelHeight / 2);
		}

		// Get current viewport
		JViewport vp = getScrollPane().getViewport();

		int x = imagePoint.x;
		int y = imagePoint.y;

		// Set x and y to point to the top-left corner of the viewport
		x -= windowPoint.x;
		if (x < 0)
			x = 0;
		if (x + vp.getWidth() >= panelWidth)
			x = panelWidth - vp.getWidth();
		y -= windowPoint.y;
		if (y < 0)
			y = 0;
		if (y + vp.getHeight() >= panelHeight)
			y = panelHeight - vp.getHeight();

		// Set new viewport position
		vp.setViewPosition(new Point(x, y));
	}

	public int getUIImageWidth() {
		return (int)(image.getImageWidth() * zoom);
	}

	public int getUIImageHeight() {
		return (int)(image.getImageHeight() * zoom);
	}

	public void updateParentWindowTitle() {
		parentImageWindow.updateTitle();
	}

	public void updateParentWindowTitle(String filename) {
		parentImageWindow.setFilename(filename);
		parentImageWindow.updateTitle();
	}
}
