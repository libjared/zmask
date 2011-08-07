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
import java.awt.Dimension;
import javax.swing.SpinnerNumberModel;
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
	private GeneralPropertiesHandler propertyHandler;
	private static GeneralProperties instance = null;
	private Resources resources;

	/* Properties */
	protected static Dimension blockSize = new Dimension(8, 8);

	private GeneralProperties() {
		propertyHandler = new GeneralPropertiesHandler(this);
		resources = new Resources("org.zkt.zmask.resources.Properties");

		/* Cannot assign this directly for whatever reason */
		PropertyDescription[] propertyArray = {
			new PropertyDescription("blockSize", PropertyDescription.TYPE_SPINNER, "Block size", propertyHandler),
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

	/**
	 * Get the block size
	 *
	 * @return the block size
	 */
	public Dimension getBlockSize() {
		return blockSize;
	}

	/**
	 * Set the block size
	 *
	 * @param blockSize new block size
	 */
	public void setBlockSize(Dimension blockSize) {
		this.blockSize = blockSize;
	}


	private static class GeneralPropertiesHandler implements PropertyHandler {

		GeneralProperties generalProperties;
		SpinnerNumberModel bsModel;

		protected GeneralPropertiesHandler(GeneralProperties generalProperties) {
			this.generalProperties = generalProperties;

			Integer value = new Integer(generalProperties.blockSize.height);
			Integer min = new Integer(2);
			Integer step = new Integer(1);
			bsModel = new SpinnerNumberModel(value, min, null, step);
		}

		public void setProperty(String key, Object value) throws PropertyException {
			if (key.equals("blockSize")) {
				int bs = ((Integer)value).intValue();
				bsModel.setValue(new Integer(bs));
				Dimension newBlockSize = new Dimension(bs, bs);
				if (!generalProperties.blockSize.equals(newBlockSize))
					generalProperties.setBlockSize(newBlockSize);
			}
			else {
				throw new PropertyException(key);
			}
		}

		public Object getProperty(String key) throws PropertyException {
			if (key.equals("blockSize"))
				return new Integer(generalProperties.blockSize.height);

			throw new PropertyException(key);
		}

		public Object getModel(String key) throws PropertyException {
			if (key.equals("blockSize"))
				return bsModel;
			else
				throw new PropertyException(key);
		}

	}
}
