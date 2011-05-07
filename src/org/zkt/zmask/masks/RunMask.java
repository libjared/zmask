/*
 * RunMask.java
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
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import org.zkt.zmask.Image;
import org.zkt.zmask.utils.Property;

/**
 * Runs a mask on image/selection
 *
 * @author zqad
 */
public class RunMask {
	private static Map<String, Mask> masks;

	public static void init() {
		masks = new HashMap<String, Mask>();
		masks.put("RotateCW", new RotateCW());
		masks.put("RotateCCW", new RotateCCW());

		masks.put("RGBRotate", new RGBRotate());
		masks.put("XOR", new XOR());
		masks.put("Invert", new Invert());

		masks.put("FlipVertical", new FlipVertical());
		masks.put("FlipHorizontal", new FlipHorizontal());
		masks.put("VerticalGlass", new VerticalGlass());
		masks.put("HorizontalGlass", new HorizontalGlass());
		masks.put("Win", new Win());
		masks.put("MekoPlus", new MekoPlus());
		masks.put("MekoMinus", new MekoMinus());
		masks.put("FL", new FL());
		masks.put("Q0", new Q0());
	}

	public static void run(Image image, String maskName) {
		if (image == null)
			throw new IllegalArgumentException("image is null");

		Mask mask = masks.get(maskName);
		if (mask == null)
			throw new IllegalArgumentException("No such mask: "
				+ maskName);

		BufferedImage bi;
		if (mask.runBare()) {
			mask.runMask(image);
		}
		else if (mask.needWhole()) {
			bi = new BufferedImage(image.getImageWidth(),
				image.getImageHeight(), image.getImageType());
			image.drawImage((Graphics2D)bi.getGraphics(), 0, 0);
			mask.runMask(bi);
			image.addImage(bi, null, mask.getDescription(), true, null);
		}
		else {
			bi = image.currentSelectionImage(mask.needClone());
			bi = mask.runMask(bi);
			image.addImage(bi, null, mask.getDescription(), false, null);
		}

	}

	public static List<MaskProperties> getAllMaskProperties() {
		List<MaskProperties> mp = new LinkedList<MaskProperties>();
		for (String key : masks.keySet()) {
			Mask mask =  masks.get(key);
			Property[] pa = mask.getProperties();
			if (pa != null)
				mp.add(new MaskProperties(key, mask.getDescription(), pa));
		}
		return mp;
	}
}
