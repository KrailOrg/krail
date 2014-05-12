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

import java.io.File;
import java.util.Locale;
import java.util.Set;

import uk.co.q3c.util.ResourceUtils;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.i18n.SupportedLocales;

import com.google.inject.Inject;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

public class LocaleContainer extends IndexedContainer {
	public enum PropertyName {
		NAME, FLAG
	}

	public final static String OPTION_LOCALE_FLAG_SIZE = "localeFlagSize";
	private final Set<Locale> supportedLocales;
	private final UserOption userOption;

	@Inject
	protected LocaleContainer(@SupportedLocales Set<Locale> supportedLocales, UserOption userOption) {
		super();
		this.supportedLocales = supportedLocales;
		this.userOption = userOption;
		fillContainer();
	}

	/**
	 * Loads the container with text from {@link Locale#getDisplayName()}, and an icon for the coutry flag if there is
	 * one. If there is no image flag, the flag property is left as null.
	 */
	@SuppressWarnings("unchecked")
	private void fillContainer() {

		addContainerProperty(PropertyName.NAME, String.class, null);
		addContainerProperty(PropertyName.FLAG, Resource.class, null);

		File webInfDir = ResourceUtils.configurationDirectory();
		File iconsDir = new File(webInfDir, "icons");
		File flagsDir = new File(iconsDir, "flags_iso");

		Integer flagSize = userOption.getOptionAsInt(this.getClass().getSimpleName(), OPTION_LOCALE_FLAG_SIZE, 48);
		File flagSizedDir = new File(flagsDir, flagSize.toString());

		for (Locale supportedLocale : supportedLocales) {
			String id = supportedLocale.toLanguageTag();
			Item item = addItem(id);
			item.getItemProperty(PropertyName.NAME).setValue(supportedLocale.getDisplayName());

			// if the directory is missing don't bother with file
			if (flagSizedDir.exists()) {
				String filename = supportedLocale.getCountry().toLowerCase() + ".png";
				File file = new File(flagSizedDir, filename);
				if (file.exists()) {
					FileResource resource = new FileResource(file);
					item.getItemProperty(PropertyName.FLAG).setValue(resource);
				}

			}
		}

		sort(new Object[] { PropertyName.NAME }, new boolean[] { true });
	}

}
