/*
 * Property.java
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

import org.zkt.zmask.utils.PropertyException;

/**
 * Property
 *
 * @author zqad
 */
public class Property {

	public static final int TYPE_BOOLEAN = 0;

	private String key;
	private int type;
	private String description;

	public Property(String key, int type, String description) {
		this.key = key;
		this.type = type;
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public int getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

}
