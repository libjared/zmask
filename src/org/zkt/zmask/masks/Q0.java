/*
 * Q0.java
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

import java.awt.image.BufferedImage;
import org.zkt.zmask.Image;

/**
 * The Q0 mask, basically horizontal+vertical glass and invert
 *
 * @author zqad
 */
public class Q0 implements Mask {
	boolean runHorizontalGlass, runVerticalGlass, runInvert;
	Mask horizontalGlass, verticalGlass, invert;

	public Q0() {
		horizontalGlass = new HorizontalGlass();
		verticalGlass = new VerticalGlass();
		invert = new Invert();

		runHorizontalGlass = true;
		runVerticalGlass = true;
		runInvert = true;
	}

	public String getDescription() {
		return "Q0";
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
		if (runHorizontalGlass)
			image = horizontalGlass.runMask(image);

		if (runVerticalGlass)
			image = verticalGlass.runMask(image);

		if (runInvert)
			image = invert.runMask(image);

		return image;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

}
