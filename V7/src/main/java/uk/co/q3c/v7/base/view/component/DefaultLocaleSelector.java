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

import uk.co.q3c.v7.i18n.CurrentLocale;

import com.google.inject.Inject;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public class DefaultLocaleSelector implements LocaleSelector {

	private final CurrentLocale currentLocale;
	private final LocaleContainer container;
	private ComboBox combo;

	@Inject
	protected DefaultLocaleSelector(CurrentLocale currentLocale, LocaleContainer container) {
		super();
		this.currentLocale = currentLocale;
		this.container = container;
		buildUI();
	}

	private void buildUI() {
		combo = new ComboBox();
		combo.setContainerDataSource(container);
	}

	@Override
	public void localeChanged(Locale toLocale) {
		// TODO Auto-generated method stub
	}

	@Override
	public Component getComponent() {
		return combo;
	}
}

// public static IndexedContainer getISO3166Container() {
// IndexedContainer c = new IndexedContainer();
// fillIso3166Container(c);
// return c;
// }
//
// private static void fillIso3166Container(IndexedContainer container) {
// container.addContainerProperty(iso3166_PROPERTY_NAME, String.class,
// null);
// container.addContainerProperty(iso3166_PROPERTY_SHORT, String.class,
// null);
// container.addContainerProperty(iso3166_PROPERTY_FLAG, Resource.class,
// null);
// for (int i = 0; i < iso3166.length; i++) {
// String name = iso3166[i++];
// String id = iso3166[i];
// Item item = container.addItem(id);
// item.getItemProperty(iso3166_PROPERTY_NAME).setValue(name);
// item.getItemProperty(iso3166_PROPERTY_SHORT).setValue(id);
// item.getItemProperty(iso3166_PROPERTY_FLAG).setValue(
// new ThemeResource("../sampler/flags/" + id.toLowerCase()
// + ".gif"));
// }
// container.sort(new Object[] { iso3166_PROPERTY_NAME },
// new boolean[] { true });
// }

//
// // Creates a new combobox using an existing container
// sample = new ComboBox("Select your country",
// ExampleUtil.getISO3166Container());
// sample.setInputPrompt("No country selected");
//
// // Sets the combobox to show a certain property as the item caption
// sample.setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
// sample.setItemCaptionMode(ItemCaptionMode.PROPERTY);
//
// // Sets the icon to use with the items
// sample.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);
//
// // Set a reasonable width
// sample.setWidth(350.0f, Unit.PIXELS);
//
// // Set the appropriate filtering mode for this example
// sample.setFilteringMode(FilteringMode.CONTAINS);
// sample.setImmediate(true);
//
// // Disallow null selections
// sample.setNullSelectionAllowed(false);
//
// // Check if the caption for new item already exists in the list of item
// // captions before approving it as a new item.
// sample.setNewItemHandler(new NewItemHandler() {
// @Override
// public void addNewItem(final String newItemCaption) {
// boolean newItem = true;
// for (final Object itemId : sample.getItemIds()) {
// if (newItemCaption.equalsIgnoreCase(sample
// .getItemCaption(itemId))) {
// newItem = false;
// break;
// }
// }
// if (newItem) {
// // Adds new option
// if (sample.addItem(newItemCaption) != null) {
// final Item item = sample.getItem(newItemCaption);
// item.getItemProperty(ExampleUtil.iso3166_PROPERTY_NAME)
// .setValue(newItemCaption);
// sample.setValue(newItemCaption);
// }
// }
// }
// });
//
//
// ...
//
// sample.addValueChangeListener(new ValueChangeListener() {
// @Override
// public void valueChange(final ValueChangeEvent event) {
// final String valueString = String.valueOf(event.getProperty()
// .getValue());
// Notification.show("Value changed:", valueString,
// Type.TRAY_NOTIFICATION);
// }
// });
// }
