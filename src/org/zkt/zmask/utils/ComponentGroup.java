/*
 * ComponentGroup.java
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

package org.zkt.zmask.utils;

import java.util.List;
import java.util.LinkedList;
import java.awt.Component;

/**
 * Grouping of java.awt.Component:s
 *
 * @author zqad
 */
public class ComponentGroup {
	List<Component> members;

	public ComponentGroup() {
		members = new LinkedList<Component>();
	}

	public void setEnabled(boolean v) {
		for (Component c : members)
			c.setEnabled(v);
	}

	public void add(Component c) {
		members.add(c);
	}
}
