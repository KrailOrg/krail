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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * Utility class to manipulate Vaadin component settings to reflect locale changes. This implementation uses field
 * annotations to specify the keys to use, and this {@link I18NTranslator} implementation then looks up the key values
 * and sets caption, description and value properties of the component.
 * <p>
 * There are two sources of annotations. They may be applied to the UI Components directly, or on an entity which is
 * being used as a model for form creation.
 * <p>
 * All the annotation parameters are optional. If caption or description keys are not specified, then the caption or
 * description of the component is set to null. If the value key is not specified, the value of the component remains
 * unchanged.
 * <p>
 * The value parameter is only relevant to components which implement the {@link com.vaadin.data.Property} interface
 * (for example {@link Label}), and if a value key is specified for any other component, it is ignored
 * <p>
 * The annotations used are those registered using {@link CurrentLocale#registerAnnotation(Class, Provider)}. Note that
 * {@link I18N} is registered by default.
 * <p>
 * The locale of all components with a registered annotation is always updated to {@link CurrentLocale#getLocale()}
 * <p>
 * The call is cascaded to any contained properties which implement the {@link I18NListener} interface. Any compound
 * components you wish to include within the scope of I18N should therefore implement the {@link I18NListener}
 * interface.
 * 
 * @author David Sowerby 8 Feb 2013
 * 
 */
public class AnnotationI18NTranslator implements I18NTranslator {
	private static Logger log = LoggerFactory.getLogger(AnnotationI18NTranslator.class);
	private final CurrentLocale currentLocale;
	private final Provider<I18NTranslator> translatorPro;
	private final Map<Class<? extends Annotation>, Provider<? extends I18NAnnotationReader>> readers;
	private final Translate translate;

	@Inject
	protected AnnotationI18NTranslator(CurrentLocale currentLocale, Provider<I18NTranslator> translatorPro,
			Translate translate) {
		super();
		this.currentLocale = currentLocale;
		this.translatorPro = translatorPro;
		this.readers = currentLocale.getI18NReaders();
		this.translate = translate;

	}

	/**
	 * @see uk.co.q3c.v7.i18n.I18NTranslator#translate(uk.co.q3c.v7.i18n.I18NListener)
	 */
	@Override
	public void translate(I18NListener listener) {
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
		try {
			I18NListener sub = (I18NListener) field.get(listener);
			sub.localeChange(translatorPro.get());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("Unable to process I18N sub-listener " + field.getName(), e);
		}
	}

	private void processComponent(I18NListener listener, Field field) {

		for (Entry<Class<? extends Annotation>, Provider<? extends I18NAnnotationReader>> readerEntry : readers
				.entrySet()) {
			if (field.isAnnotationPresent(readerEntry.getKey())) {
				decodeAnnotation(listener, field, field.getAnnotation(readerEntry.getKey()), readerEntry.getValue());
			}
		}
		return;

	}

	private void decodeAnnotation(I18NListener listener, Field field, Annotation annotation,
			Provider<? extends I18NAnnotationReader> provider) {

		// get a reader
		I18NAnnotationReader reader = provider.get();

		// get the keys from the reader
		I18NKey<?> captionKey = reader.caption(annotation);
		I18NKey<?> descriptionKey = reader.description(annotation);
		I18NKey<?> valueKey = reader.value(annotation);

		// check for nulls. Nulls are used for caption and description so that content can be cleared.
		// for value, this is not the case, as it may be a bad idea
		String captionValue = captionKey.isNullKey() ? null : translate.from(captionKey);
		String descriptionValue = descriptionKey.isNullKey() ? null : translate.from(descriptionKey);

		// set caption and description
		field.setAccessible(true);
		try {
			AbstractComponent c = (AbstractComponent) field.get(listener);
			if (captionValue != null) {
				c.setCaption(captionValue);
			}
			if (descriptionValue != null) {
				c.setDescription(descriptionValue);
			}
			c.setLocale(currentLocale.getLocale());
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
					String valueValue = valueKey.isNullKey() ? null : translate.from(valueKey);
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
					if (column instanceof LabelKey) {
						LabelKey columnid = (LabelKey) column;
						String header = translate.from(columnid);
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

	@Override
	public Locale getLocale() {
		return currentLocale.getLocale();
	}

	public void apply(AbstractComponent component, Field fieldWithAnnotations) {

	}

}
