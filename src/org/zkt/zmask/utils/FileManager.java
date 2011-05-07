/*
 * FileManager.java
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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Handle loading and saving of image files
 *
 * @author zqad
 */
public class FileManager {

	public static BufferedImage loadFile(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);

		if (image.getType() == BufferedImage.TYPE_CUSTOM) {
			/* The image will not be accepted by filters and
			 * transformations, we need to rebuild it. */
			BufferedImage ti = image;
			image = new BufferedImage(ti.getWidth(), ti.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(ti, 0, 0, null);
		}

		return image;
	}

	public static boolean saveFile(RenderedImage image, String format,
			File file) throws IOException {
		return ImageIO.write(image, format, file);
	}

	private static Map<String, FileNameExtensionFilter> allFilters;

	private static void initAllFilters() {
		allFilters = new HashMap<String, FileNameExtensionFilter>();

		allFilters.put("jpeg", new FileNameExtensionFilter("JPEG file (*.jpeg; *.jpg; *.jpe)", "jpg", "jpeg", "jpe"));
		allFilters.put("gif",  new FileNameExtensionFilter("GIF file (*.gif)", "gif"));
		allFilters.put("png",  new FileNameExtensionFilter("PNG file (*.png)", "png"));
		allFilters.put("bmp",  new FileNameExtensionFilter("BMP file (*.bmp)", "bmp", "wbmp"));
		allFilters.put("tiff", new FileNameExtensionFilter("TIFF file (*.tiff, *.tif)", "tif", "tiff"));
	}

	public static List<FileNameExtensionFilter> generateFileFilters(boolean write) {
		if (allFilters == null)
			initAllFilters();

		String[] formatSuffixes;
		Set<FileNameExtensionFilter> result = new HashSet<FileNameExtensionFilter>();
		List<String> allExtensions = new LinkedList<String>();
		StringBuilder allText;
		if (write) {
			allText = new StringBuilder("Image files (");
			formatSuffixes = ImageIO.getWriterFileSuffixes();
		}
		else {
			allText = new StringBuilder("By extension (");
			formatSuffixes = ImageIO.getReaderFileSuffixes();
		}

		for (String suffix : formatSuffixes) {
			FileNameExtensionFilter filter;
			if (suffix.equalsIgnoreCase("jpg"))
				filter = allFilters.get("jpeg");
			else if (suffix.equalsIgnoreCase("jpeg"))
				filter = allFilters.get("jpeg");
			else if (suffix.equalsIgnoreCase("jpe"))
				filter = allFilters.get("jpeg");
			else if  (suffix.equalsIgnoreCase("png"))
				filter = allFilters.get("png");
			else if  (suffix.equalsIgnoreCase("gif"))
				filter = allFilters.get("gif");
			else if  (suffix.equalsIgnoreCase("bmp"))
				filter = allFilters.get("bmp");
			else if  (suffix.equalsIgnoreCase("wbmp"))
				filter = allFilters.get("bmp");
			else if  (suffix.equalsIgnoreCase("tif"))
				filter = allFilters.get("tiff");
			else if  (suffix.equalsIgnoreCase("tiff"))
				filter = allFilters.get("tiff");
			else
				filter = new FileNameExtensionFilter(suffix.toUpperCase() + " file (*." + suffix + ")", suffix);
			result.add(filter);

			for (String extension : filter.getExtensions()) {
				allText.append("*." + extension + "; ");
				allExtensions.add(extension);
			}
		}

		allText.delete(allText.length() - 2, allText.length());
		allText.append(")");

		FileNameExtensionFilter all = new FileNameExtensionFilter(allText.toString(), allExtensions.toArray(new String[allExtensions.size()]));

		List<FileNameExtensionFilter> finalResult = new LinkedList<FileNameExtensionFilter>();
		finalResult.add(all);
		for (FileNameExtensionFilter filter : result)
			finalResult.add(filter);

		return finalResult;
	}
}
