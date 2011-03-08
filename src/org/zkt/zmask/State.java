/*
 * State.java
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * Tie-together class used to update, keep and sometimes detect the state of
 * the application.
 * @author zqad
 */
public class State {
	/* Definitions */
	public static enum Tools {
		SELECT_TOOL,
		ZOOM_TOOL,
		HAND_TOOL,
	}

	public static enum MouseButton {
		NO_BUTTON,
		LEFT_BUTTON,
		MIDDLE_BUTTON,
		RIGHT_BUTTON,
	}

	/* Variables */
	protected static Dimension blockSize = new Dimension(8, 8);
	protected static JDesktopPane mainDesktopPane;
	private static Tools tool = Tools.SELECT_TOOL;

	/**
	 * Get the block size
	 *
	 * @return the block size
	 */
	public static Dimension getBlockSize() {
		return blockSize;
	}

	/**
	 * Set the block size
	 *
	 * @param blockSize new block size
	 */
	public static void setBlockSize(Dimension blockSize) {
		State.blockSize = blockSize;
	}

	/**
	 * Get the tool currently in use
	 *
	 * @return the tool currently in use
	 */
	public static Tools getCurrentTool() {
		return State.tool;
	}

	public static void setCurrentTool(Tools tool) {
		List<Image> images = getAllImages();
		for (Image image : images) {
			image.getImagePanel().setTool(tool);
		}
		State.tool = tool;
	}

	public static void resetCursorPosition(ImagePanel ip) {

	}

	public static void setCursorPositionHover(ImagePanel ip, int x, int y) {
	}

	public static void setCursorPositionPressed(ImagePanel ip, int x, int y,
		int startX, int startY) {
	}

	public static void setMainDesktopPane(JDesktopPane mainDesktopPane) {
		State.mainDesktopPane = mainDesktopPane;
	}

	public static JDesktopPane getMainDesktopPane() {
		return mainDesktopPane;
	}

	public static Image getCurrentImage() {
		JInternalFrame window = mainDesktopPane.getSelectedFrame();

		if (window instanceof ImageWindow) {
			return ((ImageWindow)window).getImagePanel().getImage();
		}
		return null;
	}

	public static List<Image> getAllImages() {
		JInternalFrame windows[] = mainDesktopPane.getAllFrames();
		List<Image> result = new Vector<Image>(windows.length);

		// Iterate through all windows, add images as they are found
		for (JInternalFrame window : windows)  {
			if (window instanceof ImageWindow) {
				result.add(((ImageWindow)window).getImagePanel().getImage());
			}
		}

		return result;
	}

	public static void refreshButtons() {
		Zmask.getFrame().refreshButtons();
	}

	public static Statusbar getStatusbar() {
		return Zmask.getFrame().getStatusbar();
	}

	public static String getSelectionString() {
		Rectangle selection = getCurrentImage().getSelection();
		return "";
	}
}
