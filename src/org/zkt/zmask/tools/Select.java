/*
 * Select.java
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import org.zkt.zmask.Image;
import org.zkt.zmask.GeneralProperties;

/**
 * Select tool
 *
 * @author zqad
 */
public class Select implements Tool {
	public final static Stroke STROKE = new BasicStroke(1.0f);
	public final static Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

	private int startX;
	private int startY;
	private int x;
	private int y;
	private int width;
	private int height;
	private Image image;

	public Select(int x, int y, Image image, int mouseButton, int modifiers) {
		// Fit to block size
		Dimension bs = GeneralProperties.getInstance().getBlockSize();
		x = (x + bs.width / 2);
		x -= x % bs.width;
		y = (y + bs.height / 2);
		y -= y % bs.height;

		this.startX = x;
		this.startY = y;
		this.width = 0;
		this.height = 0;
		this.image = image;
	}

	public Select(Image image, Rectangle selection) {
		if (selection == null)
			selection = image.getSelection();
		if (selection == null)
			throw new IllegalArgumentException("Unable to get selection");

		this.image = image;
		x = selection.x;
		y = selection.y;
		width = selection.width;
		height = selection.height;
	}

	public void commit(int modifiers) {
		// Set selection on image
		if (width == 0 || height == 0)
			image.setSelection(null);
		else
			image.setSelection(new Rectangle(x, y, width, height));
	}

	public void recommit() {
		commit(0);
	}

	public void draw(Graphics2D graphics, Point disp) {
		graphics.setStroke(STROKE);
		graphics.drawRect(x + disp.x, y + disp.y, width, height);
	}

	public String getName() {
		return "Select";
	}

	public void update(int dstX, int dstY, int modifiers) {
		// Fit to block size
		Dimension bs = GeneralProperties.getInstance().getBlockSize();
		dstX = (dstX + bs.width / 2);
		dstX -= dstX % bs.width;
		dstY = (dstY + bs.height / 2);
		dstY -= dstY % bs.height;

		int maxWidth = image.getImageWidth();
		maxWidth -= maxWidth % bs.width;
		if (dstX >= maxWidth)
			dstX = maxWidth - 1;
		int maxHeight = image.getImageHeight();
		maxHeight -= maxHeight % bs.height;
		if (dstY >= maxHeight)
			dstY = maxHeight - 1;

		x = Math.min(startX, dstX);
		y = Math.min(startY, dstY);
		width = Math.max(startX, dstX) - x;
		height = Math.max(startY, dstY) -y;
	}

	public boolean isUndoable() {
		return true;
	}

	public static void shift(Image image, int x, int y) {
		Rectangle currentSelection = image.getSelection();
		Rectangle newSelection = new Rectangle(currentSelection);
		newSelection.setLocation(currentSelection.x + x, currentSelection.y + y);
		Select tool = new Select(image, newSelection);
		tool.recommit();
		image.addTool(tool, "Shift selection");
	}
}
