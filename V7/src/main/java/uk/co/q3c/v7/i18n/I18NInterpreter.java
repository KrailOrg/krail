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

import java.lang.reflect.Field;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.AbstractComponent;

/**
 * Utility class to manipulate Vaadin component settings to reflect locale changes
 * 
 * @author David Sowerby 8 Feb 2013
 * 
 */
public class I18NInterpreter {
	private static Logger log = LoggerFactory.getLogger(I18NInterpreter.class);
	private Locale locale;

	@Inject
	protected I18NInterpreter() {
		super();
	}

	public void interpret(I18NListener listener) {
		Class<?> clazz = listener.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (AbstractComponent.class.isAssignableFrom(field.getType())) {
				processComponent(listener, field);
			}

		}
	}

	private void processComponent(I18NListener listener, Field field) {
		if (field.isAnnotationPresent(I18N.class)) {
			I18N annotation = field.getAnnotation(I18N.class);
			LabelKeys key = annotation.caption();
			String value = "";
			if (key != null) {
				value = key.getValue(locale);
			}
			System.out.println(field.getName() + " has I18NLabel annotation with key = " + key.name() + " value= "
					+ value);

			field.setAccessible(true);
			try {
				AbstractComponent c = (AbstractComponent) field.get(listener);
				c.setCaption(value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Unable to set I18N values for " + field.getName(), e);
			}

		}

	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

}
