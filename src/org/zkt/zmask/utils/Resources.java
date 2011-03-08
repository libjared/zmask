/*
 * Resources.java
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

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;

/**
 * Resource handling class
 *
 * @author zqad
 */
public class Resources {

	private ResourceBundle r;
	private ConcurrentMap<String, Icon> icons;

	public Resources(String bundlePath) {
		r = ResourceBundle.getBundle(bundlePath);
		icons = new ConcurrentHashMap<String, Icon>();
	}

	public int getInt(String p) {
		return Integer.parseInt(r.getString(p));
	}

	public Icon getIcon(String p) {
		String iconPath = r.getString(p);
		Icon i = icons.get(iconPath);
		if (i == null) {
			URL iconUrl = Resources.class.getResource("/org/zkt/zmask/resources/" + iconPath);
			i = new ImageIcon(iconUrl);
			icons.put(iconPath, i);
		}
		return i;
	}

	public String getString(String p) {
		return r.getString(p);
	}

}
