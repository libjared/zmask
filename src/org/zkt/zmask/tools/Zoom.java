/*
 * Zoom.java
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

package org.zkt.zmask.tools;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import org.zkt.zmask.Image;
import org.zkt.zmask.ImagePanel;
import org.zkt.zmask.ImageWindow;

/**
 * Zoom tool
 *
 * @author zqad
 */
public class Zoom implements Tool {
	public final static Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private final static float dash[] = {5.0f};
	public final static Stroke STROKE = new BasicStroke(3.0f,
		BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f);


	private int x;
	private int y;
	private int width;
	private int height;
	private int mouseButton;
	private int modifiers;
	private Image image;

	public Zoom(int x, int y, Image image, int mouseButton, int modifiers) {
		this.x = x;
		this.y = y;
		this.width = 0;
		this.height = 0;
		this.image = image;
		this.mouseButton = mouseButton;
	}

	public void commit(int modifiers) {
		ImagePanel ip = image.getImagePanel();
		this.modifiers = modifiers;

		if (width == 0 && height == 0) {
			// Button 1 = zoom in, other = zoom out
			int zoomDirection = mouseButton == MouseEvent.BUTTON1 ? 1 : -1;
			// Shift flips the direction
			if ((modifiers & KeyEvent.SHIFT_MASK) != 0)
				zoomDirection *= -1;
			ip.zoom(zoomDirection, ip.getMousePosition(),
				ip.getScrollPane().getMousePosition());
		}
		else {
			// TODO: window zoom
			ip.repaint();
		}
	}

	public void recommit() {
		throw new UnsupportedOperationException("Not supported.");
	}

	public void draw(Graphics2D graphics, Point disp) {
		graphics.setStroke(STROKE);
		graphics.drawRect(x + disp.x, y + disp.y, width, height);
	}

	public String getName() {
		return "Zoom";
	}

	public void update(int dstX, int dstY, int modifiers) {
		width = Math.abs(x - dstX);
		height = Math.abs(y - dstY);
		x = Math.min(x, dstX);
		y = Math.min(y, dstY);
	}

	public boolean isUndoable() {
		return false;
	}
}
