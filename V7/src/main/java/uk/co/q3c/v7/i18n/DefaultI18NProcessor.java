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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
 * annotations to specify the keys to use, and this {@link I18NProcessor} implementation then looks up the key values
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
public class DefaultI18NProcessor implements I18NProcessor {
	private static Logger log = LoggerFactory.getLogger(DefaultI18NProcessor.class);
	private final CurrentLocale currentLocale;
	private final Set<String> registeredAnnotations;
	private final Translate translate;

	@Inject
	protected DefaultI18NProcessor(CurrentLocale currentLocale, Translate translate,
			@I18N Set<String> registeredAnnotations) {
		super();
		this.currentLocale = currentLocale;
		this.translate = translate;
		this.registeredAnnotations = registeredAnnotations;
	}

	/**
	 * Scans the {@code target} for fields with either a field or class annotation of {@link I18N}. Each marked field is
	 * then passed to
	 *
	 * Translate the captions, descriptions and values of {@code target} to the current Locale. This should only be
	 * called with a {@code target} which is annotated with {@link I18N}, but no errors occur if it hasn't
	 *
	 * @param target
	 */
	@Override
	public void translate(Object target) {
		log.debug("scanning class '{}' for I18N annotations", target.getClass());
		Class<?> clazz = target.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			// Check field annotation first so that it overrides a class annotation

			Annotation annotation = fieldAnnotation(field);

			if (annotation == null) {
				annotation = classAnnotation(target, field);
				if (annotation != null) {
					translate(target, field, annotation);
				}
			} else {
				log.debug("field '{}' has @{} annotation", field.getName(), annotation.annotationType());
				translate(target, field, annotation);
			}

			if (annotation != null) {
				// now drill down unless it is a native Vaadin component
				try {
					Object drillDown = field.get(target);
					if (drillDown != null) {
						if (!drillDown.getClass().getName().startsWith("com.vaadin")) {
							log.debug("drilling down into field '{}'", field.getName());
							translate(drillDown);
						} else {
							log.debug("No drill down, field '{}' is a native Vaadin component of class '{}'",
									field.getName(), field.getType());
						}
					} else {
						log.warn("cannot drill down, object for field '{}' has not been constructed", field.getName());
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Annotation fieldAnnotation(Field field) {
		log.debug("checking field '{}' for annotations", field.getName());
		for (Annotation annotation : field.getAnnotations()) {
			String annotationClassName = annotation.annotationType().getName();
			if (registeredAnnotations.contains(annotationClassName)) {
				return annotation;
			}
		}

		return null;
	}

	private Annotation classAnnotation(Object target, Field field) {
		Class<?> clazz = field.getType();
		log.debug("checking class '{}' of field '{}' for annotations", clazz.getName(), field.getName());
		for (Annotation annotation : clazz.getAnnotations()) {
			String annotationClassName = annotation.annotationType().getName();
			if (registeredAnnotations.contains(annotationClassName)) {
				return annotation;
			}
		}
		return null;
	}

	public void translate(Object target, Field field, Annotation annotation) {
		field.setAccessible(true);
		// if it is a component, process the caption etc
		if (AbstractComponent.class.isAssignableFrom(field.getType())) {
			log.debug("field '{}' is a component, is class of '{}'", field.getName(), field.getType().getName());
			try {
				AbstractComponent component = (AbstractComponent) field.get(target);
				// in case component has not been constructed
				if (component != null) {
					applyAnnotation(component, field, annotation);
				} else {
					log.warn("object for field '{}' has not been constructed, i18N cannot be applied", field.getName());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	private <T extends Annotation> void applyAnnotation(AbstractComponent component, Field field, Annotation annotation) {
		checkNotNull(component);
		checkNotNull(field);
		checkNotNull(annotation);
		I18NKey<?> captionKey = (I18NKey<?>) annotationParam("caption", annotation);
		I18NKey<?> descriptionKey = (I18NKey<?>) annotationParam("description", annotation);
		I18NKey<?> valueKey = (I18NKey<?>) annotationParam("value", annotation);
		String localeString = (String) annotationParam("locale", annotation);

		Locale locale = null;
		if (Strings.isNullOrEmpty(localeString)) {
			locale = currentLocale.getLocale();
		} else {
			locale = Locale.forLanguageTag(localeString);
		}

		// check for nulls. Nulls are used for caption and description so that content can be cleared.
		// for value, this is not the case, as it may be a bad idea
		String captionValue = captionKey.isNullKey() ? null : translate.from(captionKey, locale);
		String descriptionValue = descriptionKey.isNullKey() ? null : translate.from(descriptionKey, locale);

		// set caption and description
		field.setAccessible(true);
		try {
			if (captionValue != null) {
				component.setCaption(captionValue);
				log.debug("caption set to '{}'", captionValue);
			}
			if (descriptionValue != null) {
				component.setDescription(descriptionValue);
				log.debug("description set to '{}'", descriptionValue);
			}
			component.setLocale(locale);
			log.debug("locale set to '{}'", locale.toLanguageTag());
		} catch (IllegalArgumentException e) {
			log.error("Unable to set I18N caption or description for " + field.getName(), e);
		}

		// These components have a value. Usually I18N would only be used for Label values. If no key is provided
		// the component value is left unchanged
		if (valueKey != null) {
			if (Property.class.isAssignableFrom(field.getType())) {
				try {
					@SuppressWarnings("unchecked")
					Property<String> c = (Property<String>) component;
					String valueValue = valueKey.isNullKey() ? null : translate.from(valueKey, locale);
					if (valueValue != null) {
						c.setValue(valueValue);
						log.debug("value set to '{}'", valueValue);
					}
				} catch (Exception e) {
					log.error("Unable to set I18N value for " + field.getName(), e);

				}
			}
		}

		// Table columns need special treatment
		if (Table.class.isAssignableFrom(field.getType())) {
			log.debug("field '{}' is a Table", field.getName());
			try {
				Table table = (Table) component;
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

	private Object annotationParam(String methodName, Annotation annotation) {
		try {
			Method method = annotation.getClass().getDeclaredMethod(methodName);
			Object paramValue = method.invoke(annotation);
			return paramValue;
		} catch (NoSuchMethodException e) {
			log.warn("I18N annotation class {} must define a '{}()' method", annotation.getClass(), methodName);
			return null;
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
