/*
 * RadioGroupModel.java
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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * RadioGroupModel
 *
 * @author zqad
 */
public class RadioGroupModel {

	private HashMap<String, ButtonStore> buttons;
	private ButtonGroup bg;

	public RadioGroupModel() {
		buttons = new HashMap<String, ButtonStore>();
	}

	private synchronized ButtonStore getButtonStore(String key) {
		ButtonStore bs = buttons.get(key);
		if (bs == null) {
			bs = new ButtonStore();
			buttons.put(key, bs);
		}

		return bs;
	}

	public void addRadio(String key, String desc, boolean selected) {
		ButtonStore bs = getButtonStore(key);
		bs.desc = desc;
		bs.selected = new Boolean(selected);
	}

	/**
	 * Add a button model to the group model, assumes that the buttongroup is set
	 */
	public void addButtonModel(String key, ButtonModel bm) {
		ButtonStore bs = getButtonStore(key);
		bs.bm = bm;
		if (bs.selected != null) {
			bg.setSelected(bm, bs.selected.booleanValue());
			bs.selected = null;
		}
	}

	/**
	 * Select a button, assumes that the buttongroup is set
	 */
	public void setSelected(String key) {
		ButtonStore bs = getButtonStore(key);
		bg.setSelected(bs.bm, true);
	}

	/**
	 * Get the key of the currently selected button, assumes that the
	 * buttongroup is set
	 */
	public String getSelectedKey() {
		ButtonModel bm = bg.getSelection();
		for (Map.Entry<String, ButtonStore> e : buttons.entrySet())
			if (e.getValue().bm == bm)
				return e.getKey();

		return null;
	}

	public void setButtonGroup(ButtonGroup bg) {
		this.bg = bg;
	}

	public List<Button> getButtons() {
		List<Button> list = new LinkedList<Button>();
		for (Map.Entry<String, ButtonStore> e : buttons.entrySet()) {
			ButtonStore bs = e.getValue();
			list.add(new Button(e.getKey(), bs.desc, bs.selected));
		}

		return list;
	}

	private class ButtonStore {
		protected String desc;
		protected ButtonModel bm;
		protected Boolean selected;
	}

	public class Button {
		private String key, text;
		private Boolean selected;

		public Button(String key, String text, Boolean selected) {
			this.key = key;
			this.text = text;
			this.selected = selected;
		}

		public String getKey() {
			return key;
		}

		public String getText() {
			return text;
		}

		public Boolean getSelected() {
			return selected;
		}
	}

}
