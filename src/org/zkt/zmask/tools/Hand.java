/*
 * Hand.java
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

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import org.zkt.zmask.Image;
import org.zkt.zmask.ImagePanel;

/**
 * The hand tool
 *
 * @author zqad
 */
public class Hand implements Tool {
	public final static Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	private int x;
	private int y;
	private double zoom;
	private Image image;

	public Hand(int x, int y, Image image, int mouseButton, int modifiers) {
		zoom = image.getImagePanel().getZoom();
		this.x = (int)Math.round(x * zoom);
		this.y = (int)Math.round(y * zoom);
		this.image = image;
	}

	public void commit(int modifiers) {
		// No operation
		return;
	}

	public void recommit() {
		throw new UnsupportedOperationException("Not supported.");
	}

	public void draw(Graphics2D graphics, Point disp) {
		// No operation
		return;
	}

	public String getName() {
		return "Hand";
	}

	public void update(int dstX, int dstY, int modifiers) {
		if (x != dstX || y != dstY) {
			dstX *= zoom;
			dstY *= zoom;
			ImagePanel ip = image.getImagePanel();
			ip.scrollImagePointToWindowPoint(new Point(x, y),
				ip.getScrollPane().getMousePosition());
		}
	}

	public boolean isUndoable() {
		return false;
	}
}
