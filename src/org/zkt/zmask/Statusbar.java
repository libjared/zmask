/*
 * Statusbar.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.beans.*;
import java.io.Serializable;
import java.text.AttributedString;
import javax.swing.JPanel;

/**
 * Display a status bar
 *
 * @author zqad
 */
public class Statusbar extends JPanel implements Serializable {
	public static final long serialVersionUID = 1;

	private PropertyChangeSupport propertySupport;
	private Rectangle cursorBox;
	private Rectangle zoomBox;
	private Rectangle selectionBox;
	private int cx, cy;

	public Statusbar() {
		propertySupport = new PropertyChangeSupport(this);
	}

	@Override
	public void setPreferredSize(Dimension size) {
		super.setPreferredSize(size);
		cursorBox = new Rectangle(1, 2, 200, size.height - 4);
		zoomBox = new Rectangle(205, 2, 100, size.height - 4);
		selectionBox = new Rectangle(310, 2, 100, size.height - 4);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public void update(int cx, int cy) {
		this.cx = cx;
		this.cy = cy;

		repaint();
	}

	public void update() {
		repaint();
	}

	private void frame(Graphics2D g, int x, int y, int w, int h) {
		g.fill3DRect(x, y, w, h, true);
		g.fill3DRect(x + 2, y + 2, w - 4, h - 4, false);
		g.fillRect(x + 3, y + 3, w - 6, h - 6);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		// Draw static content
		g2.setPaint(getBackground());
		frame(g2, cursorBox.x, cursorBox.y, cursorBox.width, cursorBox.height);
		frame(g2, zoomBox.x, zoomBox.y, zoomBox.width, zoomBox.height);
		frame(g2, selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);

		Image image = State.getCurrentImage();
		if (image == null || cx == -1 || cy == -1)
			return;
		g2.setPaint(Color.BLACK);

		g2.drawString(getCursorAttributedString(image).getIterator(),
			textOffsetX(cursorBox), textOffsetY(cursorBox));

		g2.drawString(Math.round(image.getImagePanel().getZoom() * 100) + "%",
			textOffsetX(zoomBox), textOffsetY(zoomBox));

	}

	private int textOffsetX(Rectangle box) {
		return box.x + 5;
	}

	private int textOffsetY(Rectangle box) {
		return box.y + 5 + box.height / 2;
	}

	public AttributedString getCursorAttributedString(Image image) {
		// Calculate position in blocks
		Dimension blockSize = GeneralProperties.getInstance().getBlockSize();
		int blockX = cx / blockSize.width;
		int blockY = cy / blockSize.height;
		int remX = cx % blockSize.width;
		int remY = cy % blockSize.height;

		// Generate attributed string
		// <b>BX</b>+[PX % BW]<b>,BY</b>+[PC % BH]</b> / BW,BH / PX,PY
		// B = Block, P = Pixel
		// X = x, Y = y, W = Width, H = Height (BW/BH in Pixels)
		AttributedString as = new AttributedString(blockX + "+" + remX
			+ "," + blockY + "+" + remY + " / " + blockSize.width
			+ "," + blockSize.height + " / " + cx + "," + cy);
		int start = 0;
		int end = Integer.toString(blockX).length();
		as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, start, end);
		start = end + 2 + Integer.toString(remX).length();
		end = start + Integer.toString(blockY).length();
		as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, start, end);

		return as;

	}


}
