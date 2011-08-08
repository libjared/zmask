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
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;
import org.zkt.zmask.utils.RadioGroupModel;

/**
 * Rotate RGB of the selection
 *
 * @author zqad
 */
public class RGBRotate implements Mask {

	private RGBRotatePropertyHandler propertyHandler;
	private PropertyDescription[] propertyArray;
	private Rotation rotation;

	public RGBRotate() {
		rotation = Rotation.RGB;
		propertyHandler = new RGBRotatePropertyHandler(this);

		/* Cannot assign this directly for whatever reason */
		PropertyDescription[] propertyArray = {
			new PropertyDescription("rotation", PropertyDescription.TYPE_RADIOS, "Affected channels", propertyHandler),
		};
		this.propertyArray = propertyArray;
	}

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
		BufferedImageOp bio = new LookupOp(new RotateTable(0, 1, rotation), null);
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
					dst[1] = dst[2];
					dst[2] = tmp;
					break;
			}

			for (int i = 3; i < src.length; i++)
				dst[i] = src[i];

			return dst;
		}

	}

	public PropertyDescription[] getProperties() {
		return propertyArray;
	}

	private static class RGBRotatePropertyHandler implements PropertyHandler {
		RGBRotate rgbrotate;
		RadioGroupModel rotationModel;

		protected RGBRotatePropertyHandler(RGBRotate rgbrotate) {
			this.rgbrotate = rgbrotate;

			rotationModel = new RadioGroupModel();
			rotationModel.addRadio("RGB", "R -> G -> B -> R", true);
			rotationModel.addRadio("RG", "R -> G -> R", false);
			rotationModel.addRadio("RB", "R -> B -> R", false);
			rotationModel.addRadio("GB", "G -> B -> G", false);
		}

		private String rotationToString(Rotation rotation) {
			switch (rotation) {
				case RGB:
					return "RGB";
				case RG:
					return "RG";
				case RB:
					return "RB";
				case GB:
					return "GB";
			}

			return null;
		}

		private Rotation stringToRotation(String r) {
			if (r.equals("RGB"))
				return Rotation.RGB;
			else if (r.equals("RG"))
				return Rotation.RG;
			else if (r.equals("RB"))
				return Rotation.RB;
			else if (r.equals("GB"))
				return Rotation.GB;
			else
				return null;
		}

		public void setProperty(String key, Object value) throws PropertyException {
			if (key.equals("rotation")) {
				rgbrotate.rotation = stringToRotation((String)value);
			}
			else {
				throw new PropertyException(key);
			}
		}

		public Object getProperty(String key) throws PropertyException {
			if (key.equals("rotation"))
				return rotationToString(rgbrotate.rotation);

			throw new PropertyException(key);
		}

		public Object getModel(String key) throws PropertyException {
			if (key.equals("rotation"))
				return rotationModel;

			throw new PropertyException(key);
		}
	}
}
