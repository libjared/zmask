/*
 * RGBRotate.java
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
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import org.zkt.zmask.Image;

/**
 * Rotate RGB of the selection
 *
 * @author zqad
 */
public class RGBRotate implements Mask {

	public String getDescription() {
		return "RGB rotate";
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
		BufferedImageOp bio = new LookupOp(new RotateTable(0, 1, Rotation.RGB), null);
		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		bio.filter(src, dst);
		return dst;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

	enum Rotation {
		RGB,
		RG,
		RB,
		GB,
	}

	private class RotateTable extends LookupTable {
		private Rotation rotation;

		public RotateTable(int offset, int numitems, Rotation rotation) {
			super(offset, numitems);
			this.rotation = rotation;
		}

		@Override
		public int[] lookupPixel(int[] src, int[] dst) {
			if (dst == null)
				dst = new int[src.length];

			int tmp;
			switch (rotation) {
				case RGB:
					tmp = dst[0];
					dst[0] = dst[1];
					dst[1] = dst[2];
					dst[2] = tmp;
					break;
				case RG:
					tmp = dst[0];
					dst[0] = dst[1];
					dst[1] = tmp;
					break;
				case RB:
					tmp = dst[0];
					dst[0] = dst[2];
					dst[2] = tmp;
					break;
				case GB:
					tmp = dst[1];
					dst[1] = dst[1];
					dst[2] = tmp;
					break;
			}

			for (int i = 3; i < src.length; i++)
				dst[i] = src[i];

			return dst;
		}

	}
}
