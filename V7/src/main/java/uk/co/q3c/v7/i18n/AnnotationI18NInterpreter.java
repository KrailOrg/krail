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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Table;

/**
 * Utility class to manipulate Vaadin component settings to reflect locale changes. {@link I18N} field annotations
 * specify the keys to use, and this class then looks up the key values and sets caption, description and value
 * properties of the component. All the annotation parameters are optional, and any which are not provided are just
 * ignored, and the associated component property is unchanged.
 * <p>
 * The value parameter is only relevant to things like Vaadin Labels, which implement the
 * {@link com.vaadin.data.Property} interface
 * <p>
 * The locale of all components with an {@link I18N} annotation is always updated to {@link CurrentLocale#getLocale()}
 * 
 * @author David Sowerby 8 Feb 2013
 * 
 */
public class AnnotationI18NInterpreter implements I18NInterpreter {
	private static Logger log = LoggerFactory.getLogger(AnnotationI18NInterpreter.class);
	private final Locale locale;
	private final Provider<I18NInterpreter> interpreterPro;

	@Inject
	protected AnnotationI18NInterpreter(CurrentLocale currentLocale, Provider<I18NInterpreter> interpreterPro) {
		super();
		locale = currentLocale.getLocale();
		this.interpreterPro = interpreterPro;
	}

	/**
	 * @see uk.co.q3c.v7.i18n.I18NInterpreter#interpret(uk.co.q3c.v7.i18n.I18NListener)
	 */
	@Override
	public void interpret(I18NListener listener) {
		Class<?> clazz = listener.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			// process any subitems which implement I18NListener
			if (I18NListener.class.isAssignableFrom(field.getType())) {
				processSubI18NListener(listener, field);
			}

			if (AbstractComponent.class.isAssignableFrom(field.getType())) {
				processComponent(listener, field);
			}

		}
	}

	private void processSubI18NListener(I18NListener listener, Field field) {
		field.setAccessible(true);
		field.setAccessible(true);
		try {
			I18NListener sub = (I18NListener) field.get(listener);
			sub.localeChange(interpreterPro.get());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Unable to process I18N sub-listener " + field.getName(), e);
		}
	}

	private void processComponent(I18NListener listener, Field field) {

		if (field.isAnnotationPresent(I18N.class)) {
			I18N annotation = field.getAnnotation(I18N.class);
			I18NKeys<?> captionKey = annotation.caption();
			I18NKeys<?> descriptionKey = annotation.description();
			I18NKeys<?> valueKey = annotation.value();

			String captionValue = captionKey.isNullKey() ? null : captionKey.getValue(locale);
			String descriptionValue = descriptionKey.isNullKey() ? null : descriptionKey.getValue(locale);

			field.setAccessible(true);
			try {
				AbstractComponent c = (AbstractComponent) field.get(listener);
				if (captionValue != null) {
					c.setCaption(captionValue);
				}
				if (descriptionValue != null) {
					c.setDescription(descriptionValue);
				}
				c.setLocale(locale);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Unable to set I18N caption or description for " + field.getName(), e);
			}

			// These components have a value. Usually I18N would only be used for Label values. If no key is provided
			// the component value is left unchanged
			if (valueKey != null) {
				if (Property.class.isAssignableFrom(field.getType())) {
					try {
						@SuppressWarnings("unchecked")
						Property<String> c = (Property<String>) field.get(listener);
						String valueValue = valueKey.isNullKey() ? null : valueKey.getValue(locale);
						if (valueValue != null) {
							c.setValue(valueValue);
						}
					} catch (Exception e) {
						log.error("Unable to set I18N value for " + field.getName(), e);

					}
				}
			}

			// Table columns need special treatment
			if (Table.class.isAssignableFrom(field.getType())) {
				try {
					Table table = (Table) field.get(listener);
					Object[] columns = table.getVisibleColumns();
					List<String> headers = new ArrayList<>();
					for (Object column : columns) {
						if (column instanceof LabelKeys) {
							LabelKeys columnid = (LabelKeys) column;
							String header = columnid.getValue(locale);
							headers.add(header);
						} else {
							headers.add(column.toString());
						}
					}
					String headerArray[] = headers.toArray(new String[] {});
					table.setColumnHeaders(headerArray);

				} catch (Exception e) {
					log.error("Unable to set I18N table columns headers for " + field.getName(), e);
				}

			}
		}

	}

	/**
	 * @see uk.co.q3c.v7.i18n.I18NInterpreter#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

}
