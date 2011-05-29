/*
 * XOR.java
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
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import org.zkt.zmask.Image;
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;

/**
 * XOR colors in selection with 0x80
 *
 * @author zqad
 */
public class XOR implements Mask {

	public XOR() {
	}

	public String getDescription() {
		return "XOR 0x80";
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

	public BufferedImage runMask(BufferedImage src) {
		BufferedImageOp bio = new LookupOp(new XORTable(0, 1), null);
		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		bio.filter(src, dst);
		return dst;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

	private class XORTable extends LookupTable {

		public XORTable(int offset, int numitems) {
			super(offset, numitems);
		}

		@Override
		public int[] lookupPixel(int[] src, int[] dst) {
			if (dst == null)
				dst = new int[src.length];

			dst[0] = src[0] ^ 0x80;
			dst[1] = src[1] ^ 0x80;
			dst[2] = src[2] ^ 0x80;

			for (int i = 3; i < src.length; i++)
				dst[i] = src[i];

			return dst;
		}

	}

	public PropertyDescription[] getProperties() {
		return null;
	}
}
