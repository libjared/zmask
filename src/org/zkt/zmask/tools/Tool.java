/*
 * Tool.java
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

package org.zkt.zmask.tools;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Tool parent class
 *
 * @author zqad
 */
public interface Tool {
	public void update(int dstX, int dstY, int modifiers);

	public void draw(Graphics2D graphics, Point disp);

	public void commit(int modifiers);

	public void recommit();

	public String getName();

	public boolean isUndoable();
}
