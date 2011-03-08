/*
 * ImageChange.java
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

package org.zkt.zmask;

import java.awt.Point;
import java.awt.image.BufferedImage;
import org.zkt.zmask.tools.Tool;

/**
 * Represent a change to an image
 *
 * @author zqad
 */
public class ImageChange {
	private BufferedImage image;
	private Point position;
	private String description;
	private Tool tool;
	private int points;
	private BufferedImage indexImage;

	public ImageChange(BufferedImage image, Point position, String description,
			Tool tool, int points) {
		this.image = image;
		this.position = position;
		this.description = description;
		this.tool = tool;
		this.points = points;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Point getPosition() {
		return position;
	}

	public String getDescription() {
		return description;
	}

	public Tool getTool() {
		return tool;
	}

	public int getPoint() {
		return points;
	}

	public BufferedImage getIndexImage() {
		return indexImage;
	}

	public void setIndexImage(BufferedImage indexImage) {
		this.indexImage = indexImage;
	}

}
