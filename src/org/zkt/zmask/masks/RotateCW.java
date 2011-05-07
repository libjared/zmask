/*
 * RotateCW.java
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import org.zkt.zmask.Image;
import org.zkt.zmask.tools.Select;
import org.zkt.zmask.utils.Property;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;

/**
 * Rotate image clockwise
 *
 * @author zqad
 */
public class RotateCW implements Mask {

	public void runMask(Image image) {
		rotate(image, 1);

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

	public static void rotate(Image image, int quadrant) {
		BufferedImage bi = new BufferedImage(image.getImageWidth(), image.getImageHeight(), image.getImageType());
		image.drawImage((Graphics2D)bi.getGraphics(), 0, 0);
		AffineTransform rotationTransform = AffineTransform.getQuadrantRotateInstance(
			quadrant, bi.getWidth() / 2.0, bi.getHeight() / 2.0);

		// Use rotation tranform to figure out translation coordinates
		Point2D p2dinUpperRight[] = {
			null,
			new Point2D.Double(0.0, 0.0),
			new Point2D.Double(bi.getWidth(), bi.getHeight()),
			new Point2D.Double(bi.getWidth(), bi.getHeight()),
		};

		Point2D p2dinUpperLeft[] = {
			null,
			new Point2D.Double(0, bi.getHeight()),
			new Point2D.Double(0, bi.getHeight()),
			new Point2D.Double(bi.getWidth(), 0),
		};
		Point2D p2dout;

		p2dout = rotationTransform.transform(p2dinUpperRight[quadrant], null);
		double ytrans = p2dout.getY();

		p2dout = rotationTransform.transform(p2dinUpperLeft[quadrant], null);
		double xtrans = p2dout.getX();

		// Contcatenate translation transform to rotation transform
		AffineTransform translateTransform = new AffineTransform();
		translateTransform.translate(-xtrans, -ytrans);
		rotationTransform.preConcatenate(translateTransform);

		// Create tranform operation
		AffineTransformOp ato = new AffineTransformOp(rotationTransform,
			AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		// Rotate selection
		Rectangle selection = image.getSelection();
		Select select = null;
		if (selection != null) {
			Point loc = selection.getLocation();
			Point2D inLoc2d = new Point2D.Double(loc.getX(), loc.getY());
			Point2D outLoc2d = rotationTransform.transform(inLoc2d, null);

			// Pick out coordinates and dimensions for adjustment
			Dimension dim = selection.getSize();
			int sX = (int)outLoc2d.getX();
			int sY = (int)outLoc2d.getY();
			int sWidth = (int)dim.getWidth();
			int sHeight = (int)dim.getHeight();

			// Switch places of width and height if the rotation
			// was 90 degrees CW or CCW
			if (quadrant == 1 || quadrant == 3) {
				int tmp = sWidth;
				sWidth = sHeight;
				sHeight = tmp;
			}

			// Do compensation of the point, since it is now
			// rotated to another corner of the selection
			if (quadrant == 1 || quadrant == 2)
				sX -= sWidth;

			if (quadrant == 3 || quadrant == 2)
				sY -= sHeight;

			// Reenter the selection data
			outLoc2d.setLocation(sX, sY);
			dim.setSize(sWidth, sHeight);
			selection.setSize(dim);
			loc.setLocation(outLoc2d);
			selection.setLocation(loc);
			select = new Select(image, null);
		}

		// Rotate image and add the result
		image.addImage(ato.filter(bi, null), null, "Rotate 90 degrees clockwise", true, select);
	}

	public Property[] getProperties() {
		return null;
	}

	public PropertyHandler getPropertyHandler() {
		return null;
	}

}
