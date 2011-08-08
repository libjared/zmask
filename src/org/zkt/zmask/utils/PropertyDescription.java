/*
 * PropertyDescription.java
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

/**
 * PropertyDescription
 *
 * @author zqad
 */
public class PropertyDescription {

	public static final int TYPE_BOOLEAN = 0;
	public static final int TYPE_SPINNER = 1;
	public static final int TYPE_RADIOS = 2;

	private String key;
	private int type;
	private String text;
	private PropertyHandler handler;

	public PropertyDescription(String key, int type, String text,
			PropertyHandler handler) {
		this.key = key;
		this.type = type;
		this.text = text;
		this.handler = handler;
	}

	public String getKey() {
		return key;
	}

	public int getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public PropertyHandler getHandler() {
		return handler;
	}
}
