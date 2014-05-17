/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.base.view.component;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.LocaleChangeListener;

import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

public class OtherSelector implements LocaleChangeListener {
	private static Logger log = LoggerFactory.getLogger(OtherSelector.class);
	private final LocaleContainer localeContainer;

	@Inject
	protected OtherSelector(LocaleContainer localeContainer, CurrentLocale currentLocale) {
		this.localeContainer = localeContainer;
		currentLocale.addListener(this);
		buildUI();
	}

	private ComboBox sample;

	private void buildUI() {
		sample = new ComboBox();
		sample.setContainerDataSource(localeContainer);
		sample.setInputPrompt("No country selected");

		// Sets the combobox to show a certain property as the item caption
		sample.setItemCaptionPropertyId(LocaleContainer.PropertyName.NAME);
		sample.setItemCaptionMode(ItemCaptionMode.PROPERTY);

		// Sets the icon to use with the items
		sample.setItemIconPropertyId(LocaleContainer.PropertyName.FLAG);

		// Set a reasonable width
		sample.setWidth(350.0f, Unit.PIXELS);

		// Set the appropriate filtering mode for this example
		sample.setFilteringMode(FilteringMode.CONTAINS);
		sample.setImmediate(true);

		// Disallow null selections
		sample.setNullSelectionAllowed(false);

		sample.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				final String valueString = String.valueOf(event.getProperty().getValue());
				Notification.show("Value changed:", valueString, Notification.Type.TRAY_NOTIFICATION);
			}
		});
	}

	public ComboBox getSample() {
		return sample;
	}

	@Override
	public void localeChanged(Locale locale) {
		log.debug("responding in change to new locale of {}", locale.getDisplayName());
		String newValue = locale.getDisplayName();
		String oldValue = (String) sample.getValue();
		sample.setValue(newValue);
		log.debug("combo value changed from {} to {}", oldValue, newValue);

	}
}
