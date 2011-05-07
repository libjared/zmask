/*
 * ZmaskProperties.java
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

package org.zkt.zmask;

import javax.swing.ActionMap;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.BoxLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.ComponentOrientation;
import org.zkt.zmask.utils.Resources;
import org.zkt.zmask.utils.Property;
import org.zkt.zmask.masks.RunMask;
import org.zkt.zmask.masks.MaskProperties;

/**
 * Display a properties dialog
 *
 * @author zqad
 */
public class ZmaskPropertiesDialog extends JDialog {
	public static final long serialVersionUID = 1;

	private ActionMap actions;
	private Resources resources;

	public ZmaskPropertiesDialog(Frame parent) {
		super(parent);

		actions = new ActionMap();
		resources = new Resources("org.zkt.zmask.resources.Properties");
		initComponents();
		setSize(400, 300);
	}

	/* This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle(resources.getString("title"));
		setModal(true);
		setName("propertiesDialog"); // NOI18N
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());

		/* General panel */
		JPanel generalPanel = new JPanel();
		//constructSettings(generalPanel, generalPropertiesArray);


		/* Tabbed panel */
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(resources.getString("general.title"), null,
				  generalPanel,
				  resources.getString("general.description"));

		/* Add all mask properties */
		for (MaskProperties mp : RunMask.getAllMaskProperties()) {
			JPanel maskTab = constructComponents(mp.getProperties());
			tabbedPane.addTab(mp.getName(), null, maskTab, mp.getDescription());
		}

		add(tabbedPane, BorderLayout.CENTER);

		/* Button panel 1, to have the buttons below the tabbed pane */
		JPanel buttonPanel1 = new JPanel();
		buttonPanel1.setLayout(new BorderLayout());
		add(buttonPanel1, BorderLayout.PAGE_END);

		/* Button panel 2, to have the buttons aligned to the right */
		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.setLayout(new FlowLayout());
		buttonPanel1.add(buttonPanel2, BorderLayout.LINE_END);

		/* Ok button */
		JButton okButton = new javax.swing.JButton();
		okButton.setName("okButton");
		okButton.setText(resources.getString("ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		buttonPanel2.add(okButton);
		getRootPane().setDefaultButton(okButton);

		/* Apply button */
		JButton applyButton = new javax.swing.JButton();
		applyButton.setName("Button");
		applyButton.setText(resources.getString("apply"));
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});
		buttonPanel2.add(applyButton, BorderLayout.LINE_END);

		/* Cancel button */
		JButton cancelButton = new javax.swing.JButton();
		cancelButton.setName("cancelButton");
		cancelButton.setText(resources.getString("cancel"));
		//TODO:cancelButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		buttonPanel2.add(cancelButton, BorderLayout.LINE_END);

		pack();
	}

	private void ok() {
	}

	private void apply() {
	}

	private void cancel() {
		dispose();
	}

	private JPanel constructComponents(Property[] pa) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (Property p : pa) {
			if (p.getType() == Property.TYPE_BOOLEAN) {
				JCheckBox cb = new JCheckBox(p.getDescription());
				cb.setName(p.getKey());
				panel.add(cb);
			}
		}

		return panel;
	}
}
