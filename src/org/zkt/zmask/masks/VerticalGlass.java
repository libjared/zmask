/*
 * VerticalGlass.java
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

package org.zkt.zmask.masks;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.zkt.zmask.Image;
import org.zkt.zmask.GeneralProperties;
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;

/**
 * The vertical glass mask
 *
 * @author zqad
 */
public class VerticalGlass implements Mask {

	public String getDescription() {
		return "Vertical Glass";
	}

	public boolean needClone() {
		return false;
	}

	public boolean needWhole() {
		return false;
	}

	public boolean runBare() {
		return false;
	}

	public BufferedImage runMask(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage bi = new BufferedImage(width, height,
			image.getType());
		Graphics2D g = (Graphics2D)bi.getGraphics();
		g.scale(-1.0, 1.0);
		int bs = GeneralProperties.getInstance().getBlockSize().width;
		for (int i = 0; i <= width - bs; i += bs)
			g.drawImage(image.getSubimage(i, 0, bs, height), -1 * i - bs, 0, null);

		int remainder = width % bs;
		if (remainder != 0)
			g.drawImage(image.getSubimage(width - remainder, 0, remainder, height),
				width - remainder, 0, null);

		return bi;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

	public PropertyDescription[] getProperties() {
		return null;
	}
}
