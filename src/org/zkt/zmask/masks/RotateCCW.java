/*
 * RotateCCW.java
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
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;

/**
 * Rotate image counter-clockwise
 *
 * @author zqad
 */
public class RotateCCW implements Mask {

	public void runMask(Image image) {
		RotateCW.rotate(image, 3);

		// Update image panel size
		image.getImagePanel().updateSize();
	}

	public String getDescription() {
		return "Rotate 90 degrees clockwise";
	}

	public boolean needClone() {
		return false;
	}

	public boolean needWhole() {
		return true;
	}

	public boolean runBare() {
		return true;
	}

	public BufferedImage runMask(BufferedImage image) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public PropertyDescription[] getProperties() {
		return null;
	}
}
