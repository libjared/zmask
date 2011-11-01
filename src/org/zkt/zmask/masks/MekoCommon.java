/*
 * MekoCommon.java
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
 * Last known URL: http://registry.gimp.org/node/24496
 */

package org.zkt.zmask.masks;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.zkt.zmask.GeneralProperties;

/**
 * Common logic for the Meko+/- masks
 *
 * @author zqad
 */
public class MekoCommon {
	private Map<Dimension, Table> cache = null;
	private int mekoKey[] = null;
	private static MekoCommon instance = null;

	private MekoCommon() {
		/* We actually need to read this from the Meko.key resource,
		 * since java does not allow for the class to be bigger than
		 * a certain size, a problem that appeared when the data in
		 * the key file was entered into this class as final static.
		 */
		LinkedList<Integer> tmpkey = new LinkedList<Integer>();

		byte[] buffer = new byte[1024];
		try {
			InputStream is = MekoCommon.class.getResourceAsStream("/org/zkt/zmask/masks/resources/Meko.key");
			int n;
			short save = 0;
			while ((n = is.read(buffer)) != -1) {
				for (int i = 0; i < n - 1; i +=2) {
					/* All this joggling cruft is to ensure that
					 * we have network byte order when we read,
					 * and that java does not try to do anything
					 * signed. Because of this, the 0xFF of a byte
					 * is actually needed. And we need the int
					 * instead of short array.
					 */
					tmpkey.add(new Integer((((0xFF & buffer[i]) << 8) + (0xFF & buffer[i + 1]) + save)));
					save = 0;
				}
				if (n % 2 != 0)
					save = (short)(0x100 * buffer[n - 1]);
			}
		}
		catch (IOException ioe) {
			System.out.println("Caught an exception when accessing an internal file. This is not supposed to happen!");
			ioe.printStackTrace();
			System.exit(1);
		}
		// Transfer to int array...
		mekoKey = new int[tmpkey.size()];
		int i = 0;
		for (Integer key : tmpkey) {
			mekoKey[i] = key.intValue();
			i++;
		}

		cache = new HashMap<Dimension, Table>();
	}

	public static synchronized MekoCommon getInstance() {
		if (instance == null)
			instance = new MekoCommon();
		return instance;
	}

	public synchronized Table getTable(Dimension dim) {
		Table table = null;
		if (cache != null)
			table = cache.get(dim);

		if (table == null) {
			table = buildTable(dim);
			cache.put(dim, table);
		}

		return table;
	}

	private Table buildTable(Dimension dim) {
		return new Table(dim, mekoKey);
	}

	public BufferedImage runMeko(BufferedImage image, boolean doPlus) {
		// Get parameters
		int bsWidth = GeneralProperties.getInstance().getBlockSize().width * 2;
		int bsHeight = GeneralProperties.getInstance().getBlockSize().height * 2;
		int width = image.getWidth();
		int height = image.getHeight();
		int cellWidth = width / bsWidth;
		int cellHeight = height / bsHeight;
		BufferedImage bi = new BufferedImage(width, height, image.getType());
		Graphics2D g = (Graphics2D)bi.getGraphics();
		MekoCommon.Table table = getTable(new Dimension(cellWidth, cellHeight));
		BufferedImageOp bio = new LookupOp(new Invert.InvertTable(0, 1), null);

		for (int y = 0; y < cellHeight; y++) {
			for (int x = 0; x < cellWidth; x++) {
				Point p = table.transform(x, y);
				int srcX, srcY, dstX, dstY;
				if (doPlus) {
					srcX = x;
					srcY = y;
					dstX = p.x;
					dstY = p.y;
				}
				else {
					srcX = p.x;
					srcY = p.y;
					dstX = x;
					dstY = y;
				}
				BufferedImage subImage = image.getSubimage(srcX * bsWidth, srcY * bsHeight, bsWidth, bsHeight);
				g.drawImage(bio.filter(subImage, null), dstX * bsWidth, dstY * bsHeight, null);
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

	public static class Table {
		private KeyEntry[] table;
		private Dimension dimension;

		public Table(Dimension dimension, int[] mekoKey) {
			table = new KeyEntry[dimension.width * dimension.height];
			for (int i = 0; i < dimension.width * dimension.height; i++)
				table[i] = new KeyEntry(i, mekoKey[i % mekoKey.length]);
			Arrays.sort(table);
			this.dimension = dimension;
		}

		public Point transform(int x, int y) {
			int n = table[y * dimension.width + x].n;
			return new Point(n % dimension.width, n / dimension.width);
		}
	}

	public static class KeyEntry implements Comparable {
		public int n;
		public int key;

		public KeyEntry(int n, int key) {
			this.n = n;
			this.key = key;
		}

		public int compareTo(Object t) {
			return key - ((KeyEntry)t).key;
		}
	}
}
