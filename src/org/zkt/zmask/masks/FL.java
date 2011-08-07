/*
 * FL.java
 * Copyright (C) 2010  Jonas Eriksson
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

/*
 * This class is *heavily* based on gimpmask.c by Hirotsuna Mizuno, (c) 1998,
 * with additions by Michael J Hammel 2002. gimpmask.c is licensed under GPLv2.
 * URL: http://registry.gimp.org/node/24496
 */

package org.zkt.zmask.masks;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import org.zkt.zmask.Image;
import org.zkt.zmask.GeneralProperties;
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;

/**
 * The FL mask
 *
 * @author zqad
 */
public class FL implements Mask {

	private boolean useInvert;
	private PropertyDescription[] propertyArray;
	private PropertyHandler propertyHandler;

	public FL() {
		useInvert = true;

		propertyHandler = new FLPropertyHandler(this);
		PropertyDescription[] propertyArray = {
			new PropertyDescription("useInvert", PropertyDescription.TYPE_BOOLEAN, "Use invert", propertyHandler),
		};
		this.propertyArray = propertyArray;
	}

	public String getDescription() {
		return "FL mask";
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
		// Get parameters
		int bsWidth = GeneralProperties.getInstance().getBlockSize().width;
		int bsHeight = GeneralProperties.getInstance().getBlockSize().height;
		int width = image.getWidth();
		int height = image.getHeight();
		int cellWidth = width / bsWidth;
		int cellHeight = height / bsHeight;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D)bi.getGraphics();
		FLTable table = getTable(cellWidth, cellHeight);
		BufferedImageOp bio = new LookupOp(new Invert.InvertTable(0, 1), null);

		for (int y = 0; y < cellHeight; y++) {
			for (int x = 0; x < cellWidth; x++) {
				FLTransformResult tr = table.transform(x, y);
				BufferedImage subImage = image.getSubimage(x * bsWidth, y * bsHeight, bsWidth, bsHeight);
				if (tr.inv && useInvert)
					subImage = bio.filter(subImage, null);
				g.drawImage(subImage, tr.x * bsWidth, tr.y * bsHeight, null);
			}
		}

		// Copy the parts not covered because of the blocksize
		int remainder = height % bsHeight;
		if (remainder != 0)
			g.drawImage(image.getSubimage(0, height - remainder, width, remainder),
				0, height - remainder, null);

		remainder = width % bsWidth;
		if (remainder != 0)
			g.drawImage(image.getSubimage(width - remainder, 0, remainder, height),
				width - remainder, 0, null);
		return bi;
	}

	public void runMask(Image image) {
		throw new UnsupportedOperationException("Not supported.");
	}

	private FLTable getTable(int width, int height) {
		// TODO, cache
		return buildTable(width, height);
	}

	private final int[] dx = { 1, 0, -1, 0 };
	private final int[] dy = { 0, -1, 0, 1 };

	private FLTable buildTable(int width, int height) {
		FLCell[][] table = new FLCell[height][width];
		int[] xMap = new int[width * height];
		int[] yMap = new int[width * height];

		int x, y, d, i;
		for( x = d = i = 0, y = height - 1; i < width * height; i++ ){
			xMap[i] = x;
			yMap[i] = y;
			table[y][x] = new FLCell(i, width * height - i -1);
			x += dx[d];
			y += dy[d];
			if( x < 0 || width <= x || y < 0 || height <= y || (table[y][x] != null && 0 <= table[y][x].no) ){
				x -= dx[d];
				y -= dy[d];
				d = ( d + 1 ) % 4;
				x += dx[d];
				y += dy[d];
			}
		}

		return new FLTable(table, xMap, yMap);
	}

	private class FLTransformResult {
		public int x;
		public int y;
		public boolean inv;

		public FLTransformResult(int x, int y, boolean inv) {
			this.x = x;
			this.y = y;
			this.inv = inv;
		}
	}

	private class FLTable {
		FLCell[][] table;
		int[] xMap;
		int[] yMap;

		public FLTable(FLCell[][] table, int[] xMap, int[] yMap) {
			this.table = table;
			this.xMap = xMap;
			this.yMap = yMap;
		}

		public FLTransformResult transform(int x, int y) {
			int rx = xMap[table[y][x].pair];
			int ry = yMap[table[y][x].pair];
			return new FLTransformResult(rx, ry, table[y][x].no != table[y][x].pair);
		}

	}

	private class FLCell {
		public int no;
		public int pair;

		public FLCell(int no, int pair) {
			this.no = no;
			this.pair = pair;
		}
	}

	public PropertyDescription[] getProperties() {
		return propertyArray;
	}

	private static class FLPropertyHandler implements PropertyHandler {
		FL fl;

		protected FLPropertyHandler(FL fl) {
			this.fl = fl;
		}

		public void setProperty(String key, Object value) throws PropertyException {
			if (key.equals("useInvert"))
				fl.useInvert = ((Boolean)value).booleanValue();
			else
				throw new PropertyException(key);
		}

		public Object getProperty(String key) throws PropertyException {
			if (key.equals("useInvert"))
				return new Boolean(fl.useInvert);

			throw new PropertyException(key);
		}

		public Object getModel(String key) throws PropertyException {
			return null;
		}
	}
}
