/*
 * MekoMinus.java
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
 * The meko minus mask
 *
 * @author zqad
 */
public class MekoMinus implements Mask {
	private MekoCommon common;

	public MekoMinus() {
		common = MekoCommon.getInstance();
	}

	public String getDescription() {
		return "Meko-";
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
		return common.runMeko(image, false);
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}


}
