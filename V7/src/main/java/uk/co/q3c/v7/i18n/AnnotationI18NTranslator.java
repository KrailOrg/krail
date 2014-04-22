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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.ui.ScopedUI;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * Utility class to manipulate Vaadin component settings to reflect locale changes. This implementation uses field
 * annotations to specify the keys to use, and this {@link I18NTranslator} implementation then looks up the key values
 * and sets caption, description and value properties of the component.
 * <p>
 * <p>
 * All the annotation parameters are optional. If caption or description keys are not specified, then the caption or
 * description of the component is set to null. If the value key is not specified, the value of the component remains
 * unchanged.
 * <p>
 * The value parameter is only relevant to components which implement the {@link com.vaadin.data.Property} interface
 * (for example {@link Label}), and if a value key is specified for any other component, it is ignored
 * <p>
 * The annotations used are those registered in {@link I18NModule}. {@link I18N} is registered by default, but because
 * annotations cannot be extended, and have limitations strict limitations on parameter types, you will probably need to
 * define your own {@link I18N} equivalent annotations.
 * <p>
 * When a locale change occurs in {@link CurrentLocale}, {@link ScopedUI} updates itself and its current view. Other
 * views, which may have already been constructed, are updated as they become active.
 * <p>
 * Container / composite components may be annotated with {@link I18NContainer}, which then has each of its child
 * components passed to this class for translation. {@link I18NContainer} may be applied to a class or field - so if you
 * have a component probably the best way usually is to annotate the class so that it wil always be
 * 
 * @author David Sowerby 8 Feb 2013
 * 
 */
public class AnnotationI18NTranslator implements I18NTranslator {
	private static Logger log = LoggerFactory.getLogger(AnnotationI18NTranslator.class);
	private final CurrentLocale currentLocale;
	private final Map<String, I18NAnnotationReader> registeredAnnotations;
	private final Translate translate;

	@Inject
	protected AnnotationI18NTranslator(CurrentLocale currentLocale, Translate translate,
			Map<String, I18NAnnotationReader> registeredAnnotations) {
		super();
		this.currentLocale = currentLocale;
		this.translate = translate;
		this.registeredAnnotations = registeredAnnotations;
	}

	/**
	 * @see uk.co.q3c.v7.i18n.I18NTranslator#translate(uk.co.q3c.v7.i18n.I18NListener)
	 */
	@Override
	public void translate(Object target) {
		Class<?> clazz = target.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			// nested call for @I18NContainer on the field or class
			if (field.isAnnotationPresent(I18NContainer.class)
					|| (field.getType().isAnnotationPresent(I18NContainer.class))) {
				field.setAccessible(true);
				try {
					translate(field.get(target));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("Unable to process @I18NContainer for " + field.getName() + " in "
							+ target.getClass().getName(), e);
				}
			} else {
				if (AbstractComponent.class.isAssignableFrom(field.getType())) {
					processComponent(target, field);
				} else {
					log.warn(
							"I18N annotation can only be applied to components, annotation on field {} of class {} has been ignored",
							field.getName(), target.getClass().getName());
				}
			}
		}
	}

	private void processComponent(Object target, Field field) {
		Annotation[] fieldAnnotations = field.getAnnotations();
		Annotation i18Nannotation = null;
		for (Annotation fieldAnnotation : fieldAnnotations) {
			if (registeredAnnotations.keySet().contains(fieldAnnotation.getClass().getName())) {
				i18Nannotation = fieldAnnotation;
				break;
			}
		}
		if (i18Nannotation != null) {
			applyAnnotation(target, field, i18Nannotation);
		}
	}

	private void applyAnnotation(Object target, Field field, Annotation annotation) {

		I18NAnnotationReader reader = registeredAnnotations.get(annotation.getClass().getName());

		Locale locale = null;
		if (Strings.isNullOrEmpty(reader.locale(annotation))) {
			locale = currentLocale.getLocale();
		} else {
			locale = new Locale(reader.locale(annotation));
		}

		// get the keys from the reader
		I18NKey<?> captionKey = reader.caption(annotation);
		I18NKey<?> descriptionKey = reader.description(annotation);
		I18NKey<?> valueKey = reader.value(annotation);

		// check for nulls. Nulls are used for caption and description so that content can be cleared.
		// for value, this is not the case, as it may be a bad idea
		String captionValue = captionKey.isNullKey() ? null : translate.from(captionKey, locale);
		String descriptionValue = descriptionKey.isNullKey() ? null : translate.from(descriptionKey, locale);

		// set caption and description
		field.setAccessible(true);
		try {
			AbstractComponent c = (AbstractComponent) field.get(target);
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
					Property<String> c = (Property<String>) field.get(target);
					String valueValue = valueKey.isNullKey() ? null : translate.from(valueKey, locale);
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
				Table table = (Table) field.get(target);
				Object[] columns = table.getVisibleColumns();
				List<String> headers = new ArrayList<>();
				for (Object column : columns) {
					if (column instanceof LabelKey) {
						LabelKey columnid = (LabelKey) column;
						String header = translate.from(columnid, locale);
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
