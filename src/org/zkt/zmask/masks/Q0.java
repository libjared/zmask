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
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;

/**
 * The Q0 mask, basically horizontal+vertical glass and invert with optional
 * flip horizontal/vertical and XOR 0x80.
 *
 * @author zqad
 */
public class Q0 implements Mask {
	private boolean runHorizontalGlass, runVerticalGlass, runInvert;
	private boolean runFlipHorizontal, runFlipVertical, runXOR;
	private Mask horizontalGlass, verticalGlass, invert;
	private Mask flipHorizontal, flipVertical, xor;
	private Q0PropertyHandler propertyHandler;
	private PropertyDescription[] propertyArray;

	public Q0() {
		horizontalGlass = new HorizontalGlass();
		verticalGlass = new VerticalGlass();
		invert = new Invert();
		flipHorizontal = new FlipHorizontal();
		flipVertical = new FlipVertical();
		xor = new XOR();

		runHorizontalGlass = true;
		runVerticalGlass = true;
		runInvert = true;
		runFlipHorizontal = false;
		runFlipVertical = false;
		runXOR = false;

		propertyHandler = new Q0PropertyHandler(this);

		/* Cannot assign this directly for whatever reason */
		PropertyDescription[] propertyArray = {
			new PropertyDescription("runVerticalGlass", PropertyDescription.TYPE_BOOLEAN, "Run vertical glass", propertyHandler),
			new PropertyDescription("runHorizontalGlass", PropertyDescription.TYPE_BOOLEAN, "Run horizontal glass", propertyHandler),
			new PropertyDescription("runInvert", PropertyDescription.TYPE_BOOLEAN, "Run invert", propertyHandler),
			new PropertyDescription("runFlipVertical", PropertyDescription.TYPE_BOOLEAN, "Run vertical flip", propertyHandler),
			new PropertyDescription("runFlipHorizontal", PropertyDescription.TYPE_BOOLEAN, "Run horizontal flip", propertyHandler),
			new PropertyDescription("runXOR", PropertyDescription.TYPE_BOOLEAN, "Run XOR 0x80", propertyHandler),
		};
		this.propertyArray = propertyArray;
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

		if (runFlipHorizontal)
			image = flipHorizontal.runMask(image);

		if (runFlipVertical)
			image = flipVertical.runMask(image);

		if (runXOR)
			image = xor.runMask(image);

		return image;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

	public PropertyDescription[] getProperties() {
		return propertyArray;
	}

	private static class Q0PropertyHandler implements PropertyHandler {
		Q0 q0;

		protected Q0PropertyHandler(Q0 q0) {
			this.q0 = q0;
		}

		public void setProperty(String key, Object value) throws PropertyException {
			if (key.equals("runVerticalGlass"))
				q0.runVerticalGlass = ((Boolean)value).booleanValue();
			else if (key.equals("runHorizontalGlass"))
				q0.runHorizontalGlass = ((Boolean)value).booleanValue();
			else if (key.equals("runInvert"))
				q0.runInvert = ((Boolean)value).booleanValue();
			else if (key.equals("runFlipHorizontal"))
				q0.runFlipHorizontal = ((Boolean)value).booleanValue();
			else if (key.equals("runFlipVertical"))
				q0.runFlipVertical = ((Boolean)value).booleanValue();
			else if (key.equals("runXOR"))
				q0.runXOR = ((Boolean)value).booleanValue();
			else
				throw new PropertyException(key);
		}

		public Object getProperty(String key) throws PropertyException {
			if (key.equals("runVerticalGlass"))
				return new Boolean(q0.runVerticalGlass);
			else if (key.equals("runHorizontalGlass"))
				return new Boolean(q0.runHorizontalGlass);
			else if (key.equals("runInvert"))
				return new Boolean(q0.runInvert);
			else if (key.equals("runFlipHorizontal"))
				return new Boolean(q0.runFlipHorizontal);
			else if (key.equals("runFlipVertical"))
				return new Boolean(q0.runFlipVertical);
			else if (key.equals("runXOR"))
				return new Boolean(q0.runXOR);

			throw new PropertyException(key);
		}

		public Object getModel(String key) throws PropertyException {
			return null;
		}
	}
}
