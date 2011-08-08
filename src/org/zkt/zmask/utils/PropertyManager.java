/*
 * PropertyManager.java
 * Copyright (C) 2011  Jonas Eriksson
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import org.zkt.zmask.GeneralProperties;
import org.zkt.zmask.masks.RunMask;
import org.zkt.zmask.masks.MaskProperties;
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;

/**
 * PropertyManager
 *
 * @author zqad
 */
public class PropertyManager {

	public static void saveProperties() {
		synchronize(true);
	}

	public static void loadProperties() {
		synchronize(false);
	}

	private static void synchronize(boolean toDisk) {
		String path = ".zmaskrc";

		/* Overload user-supplied settings */
		try {
			path = System.getProperty("user.home") + "/.zmaskrc";
		}
		catch (SecurityException e) {
			// TODO: alert user
			e.printStackTrace();
		}

		Properties props = new Properties();

		try {
			FileInputStream in = new FileInputStream(path);
			props.load(in);
			in.close();
		}
		catch (FileNotFoundException e) {
			// Ignore
		}
		catch (IOException e) {
			// TODO
		}

		/* General properties */
                GeneralProperties gp = GeneralProperties.getInstance();
		synchronizeProperties(gp, props, toDisk);

		/* All mask properties */
		for (MaskProperties mp : RunMask.getAllMaskProperties()) {
			synchronizeProperties(mp, props, toDisk);
		}


		if (toDisk) {
			try {
				FileOutputStream out = new FileOutputStream(path);
				props.store(out, "Zmask properties");
				out.close();
			}
			catch (FileNotFoundException e) {
				// TODO
			}
			catch (IOException e) {
				// TODO
			}
		}
	}

	private static void synchronizeProperties(PropertyContainer pc, Properties props,
			boolean toDisk) {
		for (PropertyDescription pd : pc.getProperties()) {
			PropertyHandler ph = pd.getHandler();
			String key = pc.getKey() + "." + pd.getKey();
			try {
				if (toDisk) {
					props.setProperty(key, ph.getProperty(pd.getKey()).toString());
				}
				else {
					String value = props.getProperty(key);
					if (value == null)
						continue;

					switch (pd.getType()) {
					case PropertyDescription.TYPE_BOOLEAN:
						ph.setProperty(pd.getKey(), Boolean.valueOf(value));
						break;
					case PropertyDescription.TYPE_SPINNER:
						ph.setProperty(pd.getKey(), Integer.valueOf(value));
						break;
					case PropertyDescription.TYPE_RADIOS:
						ph.setProperty(pd.getKey(), value);
						break;
					}
				}
			}
			catch (PropertyException pe) {
				// TODO
			}
		}
	}
}
