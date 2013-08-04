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

import java.text.MessageFormat;
import java.util.Locale;

import javax.inject.Inject;

/**
 * A utility class to retrieve an I18N value from a key. The same can be achieved using {@link I18NKey#getValue(Locale)}
 * , this class simply provides a slightly neater syntax, and a method for expanding a pattern with parameters.
 * 
 * @author David Sowerby 3 Aug 2013
 * 
 */
public class I18NValue {

	private final CurrentLocale currentLocale;

	@Inject
	protected I18NValue(CurrentLocale currentLocale) {
		super();
		this.currentLocale = currentLocale;
	}

	public String message(Locale locale, I18NKey<?> key, Object... arguments) {
		String pattern = key.getValue(locale);
		if (pattern == null) {
			return key.name().replace("_", " ");
		}
		if ((arguments == null) || (arguments.length == 0)) {
			return pattern;
		}
		String result = MessageFormat.format(pattern, arguments);
		return result;
	}

	public String message(I18NKey<?> key, Object... arguments) {
		return message(currentLocale.getLocale(), key, arguments);
	}

	public String message(I18NKey<?> key) {
		return message(currentLocale.getLocale(), key, (Object[]) null);
	}
}
