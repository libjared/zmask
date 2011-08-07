/*
 * Zmask.java
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

import java.util.ResourceBundle;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import org.zkt.zmask.utils.PropertyManager;

/**
 * The application's main class.
 *
 * @author zqad
 */
public class Zmask {
	private static ZmaskFrame frame;

	public static final String VERSION = ResourceBundle.getBundle("org.zkt.zmask.resources.Version").getString("version");

	private static Properties properties;

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		frame = new ZmaskFrame();
		frame.setVisible(true);
	}

	public static ZmaskFrame getFrame() {
		return frame;
	}

	public static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			String path;
			FileInputStream is;
			File file;

			/* Load the default settings */
			try {
				file = new File(Zmask.class.getResource("org.zkt.zmask.resources.DefaultProperties").toURI());
				is = new FileInputStream(file);
				properties.load(is);
				is.close();
			}
			catch (Exception e) {
				// This will not fail, promise
			}
		}

		return properties;
	}

}
