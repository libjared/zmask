/*
 * ZmaskFrame.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import java.util.List;
import java.util.ResourceBundle;
import java.beans.PropertyVetoException;
import org.zkt.zmask.Image.StepException;
import org.zkt.zmask.masks.RunMask;
import org.zkt.zmask.utils.ComponentGroup;
import org.zkt.zmask.utils.Resources;
import org.zkt.zmask.utils.FileManager;

/**
 * The application's main frame.
 *
 * @author zqad
 */
public class ZmaskFrame extends JFrame {
	public static final long serialVersionUID = 1;

	private ActionMap actions;
	private Resources resources;
	private Resources actionResources;
	private ActionListener actionHandler;

	private ComponentGroup cgAll, cgUndoPossible, cgRedoPossible, cgChanged, cgImageActive, cgSelectionActive;

	private JToggleButton handToggleButton;
	private JToggleButton selectToggleButton;
	private JToggleButton zoomToggleButton;
	private JFileChooser openFileChooser;
	private JFileChooser saveAsFileChooser;
	private JProgressBar progressBar;
	private JDesktopPane mainDesktopPane;

	public ZmaskFrame() {
		super();

		actions = new ActionMap();
		resources = new Resources("org.zkt.zmask.resources.ZmaskFrame");
		actionResources = new Resources("org.zkt.zmask.resources.ActionNames"); // NOI18N

		cgAll = new ComponentGroup();
		cgUndoPossible = new ComponentGroup();
		cgRedoPossible = new ComponentGroup();
		cgChanged = new ComponentGroup();
		cgImageActive = new ComponentGroup();
		cgSelectionActive = new ComponentGroup();

		actionHandler = new ActionHandler();

		initComponents();

		int messageTimeout = resources.getInt("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
				});
		messageTimer.setRepeats(false);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		/*TaskMonitor taskMonitor = new TaskMonitor();
		taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
				public void propertyChange(java.beans.PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if ("started".equals(propertyName)) {
				progressBar.setVisible(true);
				progressBar.setIndeterminate(true);
				} else if ("done".equals(propertyName)) {
				progressBar.setVisible(false);
				progressBar.setValue(0);
				} else if ("message".equals(propertyName)) {
				String text = (String)(evt.getNewValue());
				messageTimer.restart();
				} else if ("progress".equals(propertyName)) {
				int value = (Integer)(evt.getNewValue());
				progressBar.setVisible(true);
				progressBar.setIndeterminate(false);
				progressBar.setValue(value);
				}
				}
				});*/

		// Init state
		State.setMainDesktopPane(mainDesktopPane);
		RunMask.init();

		setSize(700, 500);
	}

	public void showAboutBox() {
		if (aboutBox == null) {
			aboutBox = new ZmaskAboutBox(this);
			aboutBox.setLocationRelativeTo(this);
		}
		aboutBox.setVisible(true);
	}

	private void initActions() {
	}


	private JButton createButton(String button, String action, ComponentGroup cg, boolean enabled) {
		JButton b = new JButton();

		b.addActionListener(actionHandler);
		b.setActionCommand(action);
		b.setIcon(resources.getIcon(button + ".icon"));
		b.setToolTipText(actionResources.getString(action + ".short"));
		b.setFocusable(false);
		b.setEnabled(enabled);
		b.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		b.setName(button);
		b.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

		cgAll.add(b);
		if (cg != null)
			cg.add(b);

		return b;
	}

	private JToggleButton createToggleButton(String button, String action, ComponentGroup cg, boolean enabled, boolean selected) {
		JToggleButton b = new JToggleButton();

		b.addActionListener(actionHandler);
		b.setActionCommand(action);
		b.setIcon(resources.getIcon(button + ".icon"));
		b.setEnabled(enabled);
		b.setSelected(selected);
		b.setToolTipText(actionResources.getString(action + ".short"));
		b.setFocusable(false);
		b.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		b.setName(button);
		b.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

		cgAll.add(b);
		if (cg != null)
			cg.add(b);

		return b;
	}

	private JMenuItem createMenuItem(String menuitem, String action, ComponentGroup cg) {
		return createMenuItem(menuitem, action, cg, null);
	}

	private JMenuItem createMenuItem(String menuItem, String action, ComponentGroup cg, KeyStroke ks) {
		JMenuItem i = new JMenuItem();

		i.setActionCommand(action);
		if (ks != null)
			i.setAccelerator(ks);
		i.setText(resources.getString(menuItem + ".text"));
		i.setName(menuItem); // NOI18N
		i.addActionListener(actionHandler);

		cgAll.add(i);
		if (cg != null)
			cg.add(i);

		return i;
	}

	/* This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		mainDesktopPane = new JDesktopPane();
		JPanel mainPanel = new JPanel();
		progressBar = new javax.swing.JProgressBar();
		status = new org.zkt.zmask.Statusbar();

		mainPanel.setName("mainPanel"); // NOI18N
		mainPanel.setLayout(new java.awt.GridBagLayout());

		/* *********** *
		 * * Toolbar * *
		 * *********** */
		JToolBar mainToolBar = new JToolBar();
		mainToolBar.setFloatable(false);
		mainToolBar.setRollover(true);
		mainToolBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		mainToolBar.setName("mainToolBar"); // NOI18N

		/* Open / save */
		mainToolBar.add(createButton("openButton", "open", null, true));
		mainToolBar.add(createButton("saveButton", "save", cgChanged, false));

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		/* Undo / redo */
		mainToolBar.add(createButton("undoButton", "undo", cgUndoPossible, false));
		mainToolBar.add(createButton("redoButton", "redo", cgRedoPossible, false));

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		/* Tools */
		selectToggleButton = createToggleButton("selectToggleButton", "toolSelect", cgImageActive, false, true);
		mainToolBar.add(selectToggleButton);
		zoomToggleButton = createToggleButton("zoomToggleButton", "toolZoom", cgImageActive, false, false);
		mainToolBar.add(zoomToggleButton);
		handToggleButton = createToggleButton("handToggleButton", "toolHand", cgImageActive, false, false);
		mainToolBar.add(handToggleButton);

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		/* Selection shifts */
		mainToolBar.add(createButton("shiftSelectionLeftButton", "shiftSelectionLeft", cgSelectionActive, false));
		mainToolBar.add(createButton("shiftSelectionDownButton", "shiftSelectionDown", cgSelectionActive, false));
		mainToolBar.add(createButton("shiftSelectionUpButton", "shiftSelectionUp", cgSelectionActive, false));
		mainToolBar.add(createButton("shiftSelectionRightButton", "shiftSelectionRight", cgSelectionActive, false));

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		/* Rotate */
		mainToolBar.add(createButton("rotateCCWButton", "rotateCCW", cgImageActive, false));
		mainToolBar.add(createButton("rotateCWButton", "rotateCW", cgImageActive, false));

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		/* Selection content modifiers */
		mainToolBar.add(createButton("rgbRotateButton", "rgbRotate", cgSelectionActive, false));
		mainToolBar.add(createButton("xorButton", "xor", cgSelectionActive, false));
		mainToolBar.add(createButton("invertButton", "invert", cgSelectionActive, false));
		mainToolBar.add(createButton("flipVerticalButton", "flipVertical", cgSelectionActive, false));
		mainToolBar.add(createButton("flipHorizontalButton", "flipHorizontal", cgSelectionActive, false));
		mainToolBar.add(createButton("verticalGlassButton", "verticalGlass", cgSelectionActive, false));
		mainToolBar.add(createButton("horizontalGlassButton", "horizontalGlass", cgSelectionActive, false));
		mainToolBar.add(createButton("winButton", "win", cgSelectionActive, false));
		mainToolBar.add(createButton("mekoPlusButton", "mekoPlus", cgSelectionActive, false));
		mainToolBar.add(createButton("mekoMinusButton", "mekoMinus", cgSelectionActive, false));
		mainToolBar.add(createButton("flButton", "fl", cgSelectionActive, false));
		mainToolBar.add(createButton("q0Button", "q0", cgSelectionActive, false));

		/* ********** *
		 * * Layout * *
		 * ********** */
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		mainPanel.add(mainToolBar, gridBagConstraints);

		mainDesktopPane.setMinimumSize(new java.awt.Dimension(640, 480));
		mainDesktopPane.setName("mainDesktopPane"); // NOI18N
		mainDesktopPane.setPreferredSize(null);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipady = 75;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		mainPanel.add(mainDesktopPane, gridBagConstraints);

		status.setMaximumSize(new java.awt.Dimension(758, 26));
		status.setMinimumSize(new java.awt.Dimension(758, 26));
		status.setName("status"); // NOI18N
		status.setPreferredSize(new java.awt.Dimension(758, 26));

		progressBar.setMaximumSize(new java.awt.Dimension(148, 14));
		progressBar.setMinimumSize(new java.awt.Dimension(148, 14));
		progressBar.setName("progressBar"); // NOI18N

		javax.swing.GroupLayout statusLayout = new javax.swing.GroupLayout(status);
		status.setLayout(statusLayout);
		statusLayout.setHorizontalGroup(
				statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusLayout.createSequentialGroup()
					.addContainerGap(616, Short.MAX_VALUE)
					.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				);
		statusLayout.setVerticalGroup(
				statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE))
				);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
		mainPanel.add(status, gridBagConstraints);

		/* *********** *
		 * * Menubar * *
		 * *********** */
		JMenuBar menuBar = new JMenuBar();
		menuBar.setName("menuBar");

		/* File menu */
		JMenu fileMenu = new JMenu();
		fileMenu.setMnemonic('F');
		fileMenu.setText(resources.getString("fileMenu.text"));
		fileMenu.setName("fileMenu");

		fileMenu.add(createMenuItem("openMenuItem", "open", null, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)));
		fileMenu.add(createMenuItem("closeMenuItem", "close", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK)));
		fileMenu.add(createMenuItem("saveMenuItem", "save", cgChanged, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK)));
		fileMenu.add(createMenuItem("saveAsMenuItem", "saveAs", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));

		fileMenu.add(new javax.swing.JPopupMenu.Separator());

		fileMenu.add(createMenuItem("quitMenuItem", "quit", null, KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));

		menuBar.add(fileMenu);

		/* Edit menu */
		JMenu editMenu = new JMenu();
		editMenu.setMnemonic('E');
		editMenu.setText(resources.getString("editMenu.text")); // NOI18N
		editMenu.setName("editMenu"); // NOI18N

		editMenu.add(createMenuItem("undoMenuItem", "undo", cgUndoPossible, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK)));
		editMenu.add(createMenuItem("redoMenuItem", "redo", cgRedoPossible, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK)));

		menuBar.add(editMenu);

		/* View menu */
		JMenu viewMenu = new JMenu();
		viewMenu.setMnemonic('V');
		viewMenu.setText(resources.getString("viewMenu.text")); // NOI18N
		viewMenu.setName("viewMenu"); // NOI18N

		viewMenu.add(createMenuItem("zoomInMenuItem", "zoomIn", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_MASK)));
		viewMenu.add(createMenuItem("zoomOutMenuItem", "zoomOut", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK)));
		viewMenu.add(createMenuItem("zoom11MenuItem", "zoom11", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK)));
		viewMenu.add(createMenuItem("zoomFitMenuItem", "zoomFit", cgImageActive));

		menuBar.add(viewMenu);

		/* Image menu */
		JMenu imageMenu = new JMenu();
		imageMenu.setMnemonic('I');
		imageMenu.setText(resources.getString("imageMenu.text"));
		imageMenu.setName("imageMenu");

		imageMenu.add(createMenuItem("selectAllMenuItem", "selectAll", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK)));
		imageMenu.add(createMenuItem("selectNoneMenuItem", "selectNone", cgSelectionActive));

		imageMenu.add(new javax.swing.JPopupMenu.Separator());

		imageMenu.add(createMenuItem("shiftSelectionLeftMenuItem", "shiftSelectionLeft", cgSelectionActive, KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK)));
		imageMenu.add(createMenuItem("shiftSelectionDownMenuItem", "shiftSelectionDown", cgSelectionActive, KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK)));
		imageMenu.add(createMenuItem("shiftSelectionUpMenuItem", "shiftSelectionUp", cgSelectionActive, KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK)));
		imageMenu.add(createMenuItem("shiftSelectionRightMenuItem", "shiftSelectionRight", cgSelectionActive, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK)));

		imageMenu.add(new javax.swing.JPopupMenu.Separator());

		imageMenu.add(createMenuItem("rotateCCWMenuItem", "rotateCCW", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)));
		imageMenu.add(createMenuItem("rotateCWMenuItem", "rotateCW", cgImageActive, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK)));

		menuBar.add(imageMenu);

		/* Mask menu */
		JMenu maskMenu = new JMenu();
		maskMenu.setMnemonic('M');
		maskMenu.setText(resources.getString("maskMenu.text"));
		maskMenu.setName("maskMenu");

		maskMenu.add(createMenuItem("rgbRotateMenuItem", "rgbRotate", cgSelectionActive));
		maskMenu.add(createMenuItem("xorMenuItem", "xor", cgSelectionActive));
		maskMenu.add(createMenuItem("invertMenuItem", "invert", cgSelectionActive));
		maskMenu.add(createMenuItem("flipVerticalMenuItem", "flipVertical", cgSelectionActive));
		maskMenu.add(createMenuItem("flipHorizontalMenuItem", "flipHorizontal", cgSelectionActive));
		maskMenu.add(createMenuItem("verticalGlassMenuItem", "verticalGlass", cgSelectionActive));
		maskMenu.add(createMenuItem("horizontalGlassMenuItem", "horizontalGlass", cgSelectionActive));
		maskMenu.add(createMenuItem("winMenuItem", "win", cgSelectionActive));
		maskMenu.add(createMenuItem("mekoPlusMenuItem", "mekoPlus", cgSelectionActive));
		maskMenu.add(createMenuItem("mekoMinusMenuItem", "mekoMinus", cgSelectionActive));
		maskMenu.add(createMenuItem("flMenuItem", "fl", cgSelectionActive));
		maskMenu.add(createMenuItem("q0MenuItem", "q0", cgSelectionActive));

		menuBar.add(maskMenu);

		/* Help Menu */
		JMenu helpMenu = new JMenu();
		helpMenu.setMnemonic('H');
		helpMenu.setText(resources.getString("helpMenu.text"));
		helpMenu.setName("helpMenu");

		helpMenu.add(createMenuItem("aboutMenuItem", "showAboutBox", null));

		menuBar.add(helpMenu);

		/* Open file chooser */
		openFileChooser = new javax.swing.JFileChooser();
		openFileChooser.setName("openFileChooser");
		List<FileNameExtensionFilter> openFileChooserFilters = FileManager.generateFileFilters(false);
		for (FileNameExtensionFilter filter : openFileChooserFilters)
			openFileChooser.addChoosableFileFilter(filter);
		openFileChooser.setFileFilter(openFileChooserFilters.get(0));

		/* Save as file chooser */
		saveAsFileChooser = new javax.swing.JFileChooser();
		saveAsFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		saveAsFileChooser.setName("saveAsFileChooser");
		List<FileNameExtensionFilter> saveAsFileChooserFilters = FileManager.generateFileFilters(true);
		for (FileNameExtensionFilter filter : saveAsFileChooserFilters)
			saveAsFileChooser.addChoosableFileFilter(filter);
		saveAsFileChooser.setFileFilter(saveAsFileChooserFilters.get(0));

		setJMenuBar(menuBar);

		/* ***************** *
		 * * Settings pane * *
		 * ***************** */
		JOptionPane settingsPane = new JOptionPane();
		settingsPane.setName("settingsPane");

		add(mainPanel);
	}

	private class UndoRedoMouseAdapter extends MouseAdapter {
		private int steps;
		private boolean undo;

		public UndoRedoMouseAdapter(int steps, boolean undo) {
			super();
			this.steps = steps;
			this.undo = undo;
		}

		public void mouseClicked(MouseEvent evt) {
			try {
				if (undo)
					State.getCurrentImage().undo(steps);
				else
					State.getCurrentImage().redo(steps);
			}
			catch(StepException se) {
				// Something is wrong..
				State.refreshButtons();
				se.printStackTrace();
			}
		}
	}

	public Statusbar getStatusbar() {
		return status;
	}

	public void refreshButtons() {

		// Refresh buttons controlled by currentImage
		Image ci = State.getCurrentImage();
		if (ci == null) {
			cgImageActive.setEnabled(false);
		}
		else {
			cgImageActive.setEnabled(true);
			cgChanged.setEnabled(ci.isChanged());
			cgUndoPossible.setEnabled(ci.isUndoPossible());
			cgRedoPossible.setEnabled(ci.isRedoPossible());

			if (ci.getSelection() != null)
				cgSelectionActive.setEnabled(true);
			else
				cgSelectionActive.setEnabled(false);
		}
	}

	/************************
	 * ACTIONS
	 */

	// Open/save
	public void openAction() {
		// TODO: openFileChooser.setCurrentDirectory();

		int r = openFileChooser.showOpenDialog(this);

		if (r == JFileChooser.ERROR_OPTION) {
			// TODO
			System.out.println("wtf");
			return;
		}

		if (r != JFileChooser.APPROVE_OPTION)
			return;

		BufferedImage image;
		File file = openFileChooser.getSelectedFile();
		try {
			image = FileManager.loadFile(file);
		}
		catch (IOException e) {
			// TODO
			e.printStackTrace();
			return;
		}

		// Create window and add to destination desktop pane
		JDesktopPane destinationContainer = State.getMainDesktopPane();
		ImageWindow iw = new ImageWindow(file.getName(), image, destinationContainer);
		destinationContainer.add(iw);

		try {
			iw.setSelected(true);
		}
		catch (PropertyVetoException pve) {
			// Don't care
		}

	}

	public void saveAction() {
		// TODO
	}

	public void saveAsAction() {
		// Show
		int r = saveAsFileChooser.showOpenDialog(this);

		if (r == JFileChooser.ERROR_OPTION) {
			// TODO
			System.out.println("wtf");
			return;
		}

		if (r != JFileChooser.APPROVE_OPTION)
			return;

		/*try {
			result = FileManager.saveFile();
		}
		catch (IOException e) {
			// TODO
		}*/
	}

	public void closeAction() {
		// TODO
	}

	// Undo, redo
	public void undoAction() {
		try {
			State.getCurrentImage().undo();
		}
		catch (Image.StepException e) {
			State.refreshButtons();
		}
	}

	public void redoAction() {
		try {
			State.getCurrentImage().redo();
		}
		catch (Image.StepException e) {
			State.refreshButtons();
		}
	}

	// Tools
	public void toolSelectAction() {
		selectToggleButton.setSelected(true);
		zoomToggleButton.setSelected(false);
		handToggleButton.setSelected(false);
		State.setCurrentTool(State.Tools.SELECT_TOOL);
	}

	public void toolZoomAction() {
		selectToggleButton.setSelected(false);
		zoomToggleButton.setSelected(true);
		handToggleButton.setSelected(false);
		State.setCurrentTool(State.Tools.ZOOM_TOOL);
	}

	public void toolHandAction() {
		selectToggleButton.setSelected(false);
		zoomToggleButton.setSelected(false);
		handToggleButton.setSelected(true);
		State.setCurrentTool(State.Tools.HAND_TOOL);
	}

	// Shift selection
	public void shiftSelectionLeftAction() {
		State.getCurrentImage().shiftSelection(-1, 0);
	}

	public void shiftSelectionDownAction() {
		State.getCurrentImage().shiftSelection(0, 1);

	}

	public void shiftSelectionUpAction() {
		State.getCurrentImage().shiftSelection(0, -1);
	}

	public void shiftSelectionRightAction() {
		State.getCurrentImage().shiftSelection(1, 0);
	}

	// Rotate
	public void rotateCCWAction() {
		RunMask.run(State.getCurrentImage(), "RotateCCW");
	}

	public void rotateCWAction() {
		RunMask.run(State.getCurrentImage(), "RotateCW");
	}

	// Filters
	public void rgbRotateAction() {
		RunMask.run(State.getCurrentImage(), "RGBRotate");
	}

	public void xorAction() {
		RunMask.run(State.getCurrentImage(), "XOR");
	}

	public void invertAction() {
		RunMask.run(State.getCurrentImage(), "Invert");
	}

	public void flipVerticalAction() {
		RunMask.run(State.getCurrentImage(), "FlipVertical");
	}

	public void flipHorizontalAction() {
		RunMask.run(State.getCurrentImage(), "FlipHorizontal");
	}

	public void verticalGlassAction() {
		RunMask.run(State.getCurrentImage(), "VerticalGlass");
	}

	public void horizontalGlassAction() {
		RunMask.run(State.getCurrentImage(), "HorizontalGlass");
	}

	public void winAction() {
		RunMask.run(State.getCurrentImage(), "Win");
	}

	public void mekoPlusAction() {
		RunMask.run(State.getCurrentImage(), "MekoPlus");
	}

	public void mekoMinusAction() {
		RunMask.run(State.getCurrentImage(), "MekoMinus");
	}

	public void flAction() {
		RunMask.run(State.getCurrentImage(), "FL");
	}

	public void q0Action() {
		RunMask.run(State.getCurrentImage(), "Q0");
	}

	// Menu-only actions
	public void zoomInAction() {
		// TODO
	}

	public void zoomOutAction() {
		// TODO
	}

	public void zoom11Action() {
		// TODO
	}

	public void zoomFitAction() {
		// TODO
	}

	public void selectAllAction() {
		// TODO
	}

	public void selectNoneAction() {
		// TODO
	}

	public void quitAction() {
		// TODO: ask if save
		System.exit(0);
	}

	private org.zkt.zmask.Statusbar status;

	private final Timer messageTimer;

	private JDialog aboutBox;

	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String ac = ae.getActionCommand();

			/* This is terrible */
			if (ac.equals("close"))
				closeAction();
			else if (ac.equals("fl"))
				flAction();
			else if (ac.equals("flipHorizontal"))
				flipHorizontalAction();
			else if (ac.equals("flipVertical"))
				flipVerticalAction();
			else if (ac.equals("horizontalGlass"))
				horizontalGlassAction();
			else if (ac.equals("invert"))
				invertAction();
			else if (ac.equals("mekoMinus"))
				mekoMinusAction();
			else if (ac.equals("mekoPlus"))
				mekoPlusAction();
			else if (ac.equals("open"))
				openAction();
			else if (ac.equals("q0"))
				q0Action();
			else if (ac.equals("quit"))
				quitAction();
			else if (ac.equals("redo"))
				redoAction();
			else if (ac.equals("rgbRotate"))
				rgbRotateAction();
			else if (ac.equals("rotateCCW"))
				rotateCCWAction();
			else if (ac.equals("rotateCW"))
				rotateCWAction();
			else if (ac.equals("save"))
				saveAction();
			else if (ac.equals("saveAs"))
				saveAsAction();
			else if (ac.equals("selectAll"))
				selectAllAction();
			else if (ac.equals("selectNone"))
				selectNoneAction();
			else if (ac.equals("shiftSelectionDown"))
				shiftSelectionDownAction();
			else if (ac.equals("shiftSelectionLeft"))
				shiftSelectionLeftAction();
			else if (ac.equals("shiftSelectionRight"))
				shiftSelectionRightAction();
			else if (ac.equals("shiftSelectionUp"))
				shiftSelectionUpAction();
			else if (ac.equals("showAboutBox"))
				showAboutBox();
			else if (ac.equals("toolHand"))
				toolHandAction();
			else if (ac.equals("toolSelect"))
				toolSelectAction();
			else if (ac.equals("toolZoom"))
				toolZoomAction();
			else if (ac.equals("undo"))
				undoAction();
			else if (ac.equals("verticalGlass"))
				verticalGlassAction();
			else if (ac.equals("win"))
				winAction();
			else if (ac.equals("xor"))
				xorAction();
			else if (ac.equals("zoom11"))
				zoom11Action();
			else if (ac.equals("zoomFit"))
				zoomFitAction();
			else if (ac.equals("zoomIn"))
				zoomInAction();
			else if (ac.equals("zoomOut"))
				zoomOutAction();
		}
	}

}
