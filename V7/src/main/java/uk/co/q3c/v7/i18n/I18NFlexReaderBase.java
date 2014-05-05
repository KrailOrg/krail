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
package uk.co.q3c.v7.i18n;

import java.util.Locale;

import com.google.inject.Inject;
import com.mycila.inject.internal.guava.base.Strings;

/**
 * returns translated parameter values for an {@link I18NFlex} annotation. If a parameter has either a null keyClass or
 * an empty keyName, then an empty string is returned.
 *
 * @author David Sowerby
 * @date 3 May 2014
 */
public abstract class I18NFlexReaderBase {
	private final Translate translate;

	@Inject
	protected I18NFlexReaderBase(Translate translate) {
		this.translate = translate;

	}

	@SuppressWarnings("rawtypes")
	protected String decode(Class<? extends Enum> keyClass, String keyName, Locale locale) {
		if (keyClass == null || Strings.isNullOrEmpty(keyName)) {
			return "";
		}
		try {
			@SuppressWarnings("unchecked")
			Enum key = Enum.valueOf(keyClass, keyName);
			I18NKey<?> i18nKey = (I18NKey<?>) key;
			return translate.from(i18nKey, locale);
		} catch (Exception e) {
			throw new I18NException("Check that the key class and key make a valid combination");
		}
	}

}
