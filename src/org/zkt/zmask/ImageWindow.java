/*
 * ImageWindow.java
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * Window used to display an image panel
 *
 * @author zqad
 */
public class ImageWindow extends JInternalFrame {
	public static final long serialVersionUID = 1;

	private String filename;
	private ImagePanel imagePanel;
	private Container parent;
	private JScrollPane scrollPane;
	private final int MIN_WIDTH = 100;
	private final int MIN_HEIGHT = 100;

	public ImageWindow(String filename, BufferedImage image,
			Container parent) {
		// Resizable, closable, maximizable and iconifiable
		super(filename, true, true, true, true);

		// Init close button
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				close();
			}

			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				State.refreshButtons();
			}
		});

		// Save variables
		this.filename = filename;
		this.parent = parent;

		// Create an imagepanel which will show the graphics, contained
		// in a ScrollPane and add the ScrollPane
		imagePanel = new ImagePanel(image, this);
		scrollPane = new JScrollPane(imagePanel);
		add(scrollPane);

		// Update the window title and pack
		updateTitle();
		pack();

		// Shrink the window so it fits inside desktop pane
		Dimension size = getSize();

		// Get height and width as the minimum between the available
		// size and the needed size
		Dimension parentSize = parent.getSize();
		int height = Math.min(parentSize.height , size.height);
		int width = Math.min(parentSize.width, size.width);

		// If height is to small, we need to add width for a scrollIncremental bar,
		// and it might not be room for it. Add as much as possible.
		int vScrollBarWidth = scrollPane.getVerticalScrollBar().getMaximumSize().width;
		if (height < size.height) {
			int available = parentSize.width - width;
			width += available > vScrollBarWidth ? vScrollBarWidth : available;
		}

		// The same goes for width
		int hScrollBarHeight = scrollPane.getHorizontalScrollBar().getMaximumSize().height;
		if (width < size.width) {
			int available = parentSize.height - height;
			height += available > hScrollBarHeight ? hScrollBarHeight : available;
		}

		// Make sure that we fulfil the minimum width and height
		width = width < MIN_WIDTH ? MIN_WIDTH : width;
		height = height < MIN_HEIGHT ? MIN_HEIGHT : height;

		// Commit
		size.setSize(width, height);
		setPreferredSize(size);
		pack();

		// All done
		setVisible(true);
	}

	private void updateTitle() {
		String changedAppend = "";
		if (imagePanel.isChanged()) {
			changedAppend = "* ";
		}
		setTitle(filename + changedAppend + " [" + imagePanel.getImageWidth()
			+ "x" + imagePanel.getImageHeight() + "]");
	}

	public String getFilename() {
		return filename;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void close() {
		setVisible(false);
		try {
			// Strange that this is needed, since we do a remove..
			setSelected(false);
		}
		catch (Exception e) {
			// Not much we can do
		}
		parent.remove(this);
		State.refreshButtons();
	}

	public ImagePanel getImagePanel() {
		return imagePanel;
	}
}
