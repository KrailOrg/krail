/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.base.view.template;

import uk.co.q3c.v7.i18n.I18NListener;
import uk.co.q3c.v7.i18n.I18NTranslator;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Extends {@link VerticalLayout} but unlike most ViewTemplates, only adds a little functionality.
 * <p>
 * <li>implements {@link I18NListener} and passes Locale change to all contained components
 * <li>provides a {@link #set(int, Component)} method, to enable explicit setting the position of a component.
 * 
 * @author David Sowerby 29 Mar 2013
 * 
 */
public class VerticalStackViewTemplate extends VerticalLayout implements ViewTemplate {

	protected VerticalStackViewTemplate() {
		super();
	}

	public VerticalStackViewTemplate(Component... children) {
		super(children);
	}

	@Override
	public void localeChange(I18NTranslator translator) {
		translator.translate(this);
	}

	/**
	 * Explicitly sets the position of a component. If a {@code index} would be out of bounds, blank panels are used to
	 * fill the intervening gaps
	 * 
	 * @see com.vaadin.ui.AbstractOrderedLayout#addComponent(com.vaadin.ui.Component, int)
	 */
	public void set(int index, Component component) {
		while (index >= getComponentCount()) {
			addComponent(blankPanel());
		}
		components.set(index, component);
	}

	private Panel blankPanel() {
		return new Panel("blank");
	}

}
