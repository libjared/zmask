/*
 * GeneralProperties.java
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

import java.awt.image.BufferedImage;
import org.zkt.zmask.utils.PropertyDescription;
import org.zkt.zmask.utils.PropertyException;
import org.zkt.zmask.utils.PropertyHandler;
import org.zkt.zmask.utils.Resources;

/**
 * Handles all general properties in the application as a singelton
 * implementing the same property interface as masks do
 *
 * @author zqad
 */
public class GeneralProperties {

	private PropertyDescription[] propertyArray;
	private GeneralPropertyHandler propertyHandler;
	private static GeneralProperties instance = null;
	private Resources resources;

	private GeneralProperties() {
		propertyHandler = new GeneralPropertyHandler();
		resources = new Resources("org.zkt.zmask.resources.Properties");

		/* Cannot assign this directly for whatever reason */
		PropertyDescription[] propertyArray = {
		};
		this.propertyArray = propertyArray;
	}

	public static GeneralProperties getInstance() {
		if (instance == null) {
			instance = new GeneralProperties();
		}
		return instance;
	}

	public String getName() {
		return resources.getString("general.title");
	}

	public String getDescription() {
		return resources.getString("general.description");
	}

	public PropertyDescription[] getProperties() {
		return propertyArray;
	}

	private static class GeneralPropertyHandler implements PropertyHandler {

		protected GeneralPropertyHandler() {
		}

		public void setProperty(String key, Object value) throws PropertyException {
				throw new PropertyException(key);
		}

		public Object getProperty(String key) throws PropertyException {
			throw new PropertyException(key);
		}

		public boolean checkProperty(String key, Object value) throws PropertyException {
			// All values are valid
			return true;
		}
	}
}
