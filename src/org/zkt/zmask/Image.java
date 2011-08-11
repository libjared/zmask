/*
 * Image.java
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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Deque;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;
import org.zkt.zmask.tools.Select;
import org.zkt.zmask.tools.Tool;

/**
 * Represent an image
 *
 * @author zqad
 */
public class Image {
	//private BufferedImage currentImage;
	private Deque<ImageChange> history;
	private Deque<ImageChange> future;
	private boolean changed = false;
	private ImagePanel imagePanel;
	private Rectangle selection = null;
	private Dimension imageSize;
	private int points = 0;
	private File file = null;
	private String format;

	public Image(BufferedImage baseImage, ImagePanel imagePanel) {
		this.imagePanel = imagePanel;
		this.imageSize = new Dimension(baseImage.getWidth(),
			baseImage.getHeight());

		/* Init the history and future linked list, these will contain
		 * a linear undo buffer; history before currentImage and future
		 * after currentImage
		 */
		this.history = new LinkedBlockingDeque<ImageChange>();
		this.history.add(new ImageChange(baseImage, null, "Open", null,
			points));
		this.future = new LinkedBlockingDeque<ImageChange>();
	}

	public void addTool(Tool tool, String description) {
		if (description == null)
			description = tool.getName();

		ImageChange lastImage = history.peek();

		ImageChange ic = new ImageChange(lastImage.getImage(),
			lastImage.getPosition(), description, tool, points);
		history.push(ic);
		resetFuture();

		points++;

		repaint();
		State.refreshButtons();
	}

	public void addImage(BufferedImage image, Point position,
		String description, boolean isBase, Tool tool) {
		// Calculate points
		ImageChange lastImage = history.peek();
		Point lastPosition = lastImage.getPosition();
		int lastWidth = lastImage.getImage().getWidth();
		int lastHeight = lastImage.getImage().getHeight();

		if (isBase) {
			// New base image, reset points
			points = 0;
		}
		else {
			// Use position from selection if null
			if (position == null)  {
				position = selection.getLocation();
			}

			if ((position == lastPosition || position.equals(lastPosition))
				&& image.getWidth() == lastWidth
				&& image.getHeight() == lastHeight) {
				// Same sub image changed as the last time
				// low point increase
				points += 1;
			}
			else {
				// Another square to draw, high points
				points += 10;
			}
		}

		// Add index image?
		BufferedImage indexImage = null;
		if (points > 100) {
			indexImage = new BufferedImage(imageSize.width,
				imageSize.height, lastImage.getImage().getType());
			this.drawImage((Graphics2D)indexImage.getGraphics(), 0, 0);
			points = 0;
		}

		// Use tool from last change?
		if (tool == null)
			tool = lastImage.getTool();

		// Create image change object
		ImageChange ic = new ImageChange(image, position, description,
			tool, points);
		if (indexImage != null) {
			ic.setIndexImage(indexImage);
		}

		// Add to history, clear future pending tree-based undo
		history.push(ic);
		resetFuture();
		if (isBase) {
			this.imageSize = new Dimension(image.getWidth(),
				image.getHeight());
		}
		changed = true;
		imagePanel.updateParentWindowTitle();
		repaint();
		State.refreshButtons();
	}

	public void drawImage(Graphics2D g, int dispX, int dispY) {
		Deque<ImageChange> drawStack = new LinkedBlockingDeque<ImageChange>();

		Point prevPosition = null;
		BufferedImage prevBI = null;
		for (ImageChange ic : history) {
			Point position = ic.getPosition();
			BufferedImage bi = ic.getImage();

			// Does the image cover all of the frame? Push and break.
			if (position == null || ic.getIndexImage() != null) {
				drawStack.push(ic);
				break;
			}

			// If the image is not below the previous image, push to stack.
			if (!(position == prevPosition && position.equals(prevPosition)
				&& bi == prevBI && bi.getWidth() == prevBI.getWidth()
				&& bi.getHeight() == prevBI.getHeight())) {
				drawStack.push(ic);
			}

			prevPosition = position;
			prevBI = bi;
		}

		for (ImageChange ic : drawStack) {
			Point position = ic.getPosition();
			int x = position == null ? 0 : position.x;
			int y = position == null ? 0 : position.y;
			if (ic.getIndexImage() != null)
				g.drawImage(ic.getIndexImage(), null, 0 + dispX, 0 + dispY);
			g.drawImage(ic.getImage(), null, x + dispX, y + dispY);
		}
	}

	private void step(int steps, Deque<ImageChange> from,
		Deque<ImageChange> to) throws StepException {

		int i;
		changed = true;
		imagePanel.updateParentWindowTitle();

		// Move objects from the from stack to the to stack
		for (i = 0; i < steps; i++) {
			ImageChange ic = from.pop();
			if (ic == null) {
				throw new StepException(i, steps);
			}
			to.push(ic);
		}

		// Retrieve state from image change
		ImageChange ic = this.history.peek();
		if (ic.getPosition() == null) {
			BufferedImage bi = ic.getImage();
			this.imageSize = new Dimension(bi.getWidth(),
				bi.getHeight());
			imagePanel.updateSize();
		}
		Tool tool = ic.getTool();
		if (tool != null)
			tool.recommit();
		else
			selection = null; // TODO: ugly
		this.points = ic.getPoint();
		State.refreshButtons();
		repaint();
	}

	public void undo() throws StepException {
		undo(1);
	}

	public void undo(int steps) throws StepException {
		// Sanity check
		if (history.size() <= 1) {
			// The icon is obviously not greyed out
			State.refreshButtons();
			return;
		}

		step(steps, history, future);
	}

	public void redo() throws StepException {
		redo(1);
	}
	public void redo(int steps) throws StepException {
		// Sanity check
		if (future.size() == 0) {
			// The icon is obviously not greyed out
			State.refreshButtons();
			return;
		}

		step(steps, future, history);
	}

	public boolean isUndoPossible() {
		return (history.size() > 1);
	}

	public boolean isRedoPossible() {
		return (future.size() > 0);
	}

	private List<String> getStringList(Deque<ImageChange> list, int maxCount) {
		List<String> result = null;
		int i = 0;
		// Create a list from the stack in question
		for (ImageChange ic : list) {
			// Implement a limit, maxCount = 0 => no limit
			if (maxCount != 0 && i++ > maxCount) {
				break;
			}

			if (result == null) {
				// Lazy initialization so that we can return null
				// if the list is empty.
				result = new Vector<String>(Math.min(list.size(), maxCount));
			}
			result.add(ic.getDescription());
		}

		return result;
	}

	public List<String> getUndoList(int maxCount) {
		return getStringList(history, maxCount);
	}

	public List<String> getRedoList(int maxCount) {
		return getStringList(future, maxCount);
	}

	public int getImageWidth() {
		return imageSize.width;
	}

	public int getImageHeight() {
		return imageSize.height;
	}

	public int getImageType() {
		return history.peek().getImage().getType();
	}

	public BufferedImage currentSelectionImage(boolean clone) {
		if (selection == null)
			return null;

		ImageChange ic = history.peek();
		BufferedImage currentBI = ic.getImage();
		BufferedImage resultBI;

		// Is the current selection equal to the position and size of
		// the last image?
		if (selection.getLocation().equals((Object)ic.getPoint()) &&
			selection.getWidth() == currentBI.getWidth() &&
			selection.getHeight() == currentBI.getHeight()) {
			if (clone) {
				// Allocate and draw only if clone requested
				resultBI = new BufferedImage((int)selection.getWidth(),
					(int)selection.getHeight(), currentBI.getType());
				resultBI.getGraphics().drawImage(currentBI, 0, 0, null);
			}
			else {
				// The correct image is in currentBI and no
				// cloning requested
				return currentBI;
			}
		}
		else {
			// We allocate it locally in both this and the above
			// case to avoid an allocation if we can get away
			// without cloning
			resultBI = new BufferedImage((int)selection.getWidth(),
				(int)selection.getHeight(), currentBI.getType());
			this.drawImage((Graphics2D)resultBI.getGraphics(), -1 * (int)selection.getX(),
				-1 * (int)selection.getY());
		}
		return resultBI;
	}

	public ImagePanel getImagePanel() {
		return imagePanel;
	}

	public Rectangle getSelection() {
		return selection;
	}

	private void resetFuture() {
		if (future.size() > 0) {
			future = new LinkedBlockingDeque<ImageChange>();
		}
	}

	public void setSelection(Rectangle selection) {
		this.selection = selection;
		State.refreshButtons();
		repaint();
	}

	public void shiftSelection(int x, int y) {
		Select.shift(this, x, y);
	}

	private void repaint() {
		imagePanel.repaint();
	}

	public boolean isChanged() {
		return changed;
	}

	public void clearChanged() {
		changed = false;
		imagePanel.updateParentWindowTitle();
	}

	public class StepException extends Exception {
		public static final long serialVersionUID = 1;

		protected int tried;
		protected int succeeded;

		public StepException(int tried, int succeeded) {
			super("Tried to run " + tried + " steps, " + succeeded +
				" succeeded.");
			this.tried = tried;
			this.succeeded = succeeded;
		}

		/**
		 * Get the value of tried
		 *
		 * @return the value of tried
		 */
		public int getTried() {
			return tried;
		}

		/**
		 * Get the value of succeeded
		 *
		 * @return the value of succeeded
		 */
		public int getSucceeded() {
			return succeeded;
		}
	}

	public void setFileAndFormat(File file, String format) {
		this.file = file;
		this.format = format;
		imagePanel.updateParentWindowTitle(file.getName());
	}

	public File getFile() {
		return file;
	}

	public String getFormat() {
		return format;
	}
}
