/*
 * Invert.java
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
 * Invert colors of selection
 *
 * @author zqad
 */
public class Invert implements Mask {

	protected static boolean r = true;
	protected static boolean g = true;
	protected static boolean b = true;
	private PropertyDescription[] propertyArray;
	private InvertPropertyHandler propertyHandler;

	public Invert() {
		propertyHandler = new InvertPropertyHandler(this);

		/* Cannot assign this directly for whatever reason */
		PropertyDescription[] propertyArray = {
			new PropertyDescription("r", PropertyDescription.TYPE_BOOLEAN, "Affect red channel", propertyHandler),
			new PropertyDescription("g", PropertyDescription.TYPE_BOOLEAN, "Affect green channel", propertyHandler),
			new PropertyDescription("b", PropertyDescription.TYPE_BOOLEAN, "Affect blue channel", propertyHandler),
		};
		this.propertyArray = propertyArray;
	}

	public String getDescription() {
		return "Invert";
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
		BufferedImageOp bio = new LookupOp(new InvertTable(0, 1), null);
		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		bio.filter(src, dst);
		return dst;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

	public static class InvertTable extends LookupTable {

		public InvertTable(int offset, int numitems) {
			super(offset, numitems);
		}

		@Override
		public int[] lookupPixel(int[] src, int[] dst) {
			if (dst == null)
				dst = new int[src.length];

			dst[0] = r ? 255 - src[0] : src[0];
			dst[1] = g ? 255 - src[1] : src[1];
			dst[2] = b ? 255 - src[2] : src[2];

			for (int i = 3; i < src.length; i++)
				dst[i] = src[i];

			return dst;
		}

	}

	public PropertyDescription[] getProperties() {
		return propertyArray;
	}

	private static class InvertPropertyHandler implements PropertyHandler {
		Invert invert;

		protected InvertPropertyHandler(Invert invert) {
			this.invert = invert;
		}

		public void setProperty(String key, Object value) throws PropertyException {
			if (key.equals("r"))
				invert.r = ((Boolean)value).booleanValue();
			else if (key.equals("g"))
				invert.g = ((Boolean)value).booleanValue();
			else if (key.equals("b"))
				invert.b = ((Boolean)value).booleanValue();
			else
				throw new PropertyException(key);
		}

		public Object getProperty(String key) throws PropertyException {
			if (key.equals("r"))
				return new Boolean(invert.r);
			else if (key.equals("g"))
				return new Boolean(invert.g);
			else if (key.equals("b"))
				return new Boolean(invert.b);

			throw new PropertyException(key);
		}

		public Object getModel(String key) throws PropertyException {
			return null;
		}
	}
}
