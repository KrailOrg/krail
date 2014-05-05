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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.MessageFormat;
import uk.co.q3c.v7.base.ui.ScopedUI;

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
	private final Set<String> registeredAnnotations;
	private final Translate translate;
	private final I18NReader i18nReader;
	private final I18NFlexReader i18nFlexReader;
	private final Set<String> registeredValueAnnotations;
	private final I18NValueReader i18nValueReader;
	private final I18NValueFlexReader i18nValueFlexReader;

	private class AnnotationPair {
		Annotation capdAnnotation;
		Annotation valueAnnotation;

		public boolean isIncomplete() {
			return capdAnnotation == null || valueAnnotation == null;
		}
	}

	@Inject
	protected DefaultI18NProcessor(CurrentLocale currentLocale, Translate translate,
			@I18N Set<String> registeredAnnotations, @I18NValue Set<String> registeredValueAnnotations,
			I18NReader i18nReader, I18NFlexReader i18nFlexReader, I18NValueReader i18nValueReader,
			I18NValueFlexReader i18nValueFlexReader) {
		super();
		this.translate = translate;
		this.registeredAnnotations = registeredAnnotations;
		this.i18nReader = i18nReader;
		this.i18nFlexReader = i18nFlexReader;
		this.registeredValueAnnotations = registeredValueAnnotations;
		this.i18nValueReader = i18nValueReader;
		this.i18nValueFlexReader = i18nValueFlexReader;
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
			// Only look for class annotation if field doesn't have both
			AnnotationPair annotationPair = null;
			AnnotationPair fieldAnnotationPair = fieldAnnotation(field);

			// if either the capd or value annotation is not present on the field we need to check the class for
			// annotations
			if (fieldAnnotationPair.isIncomplete()) {
				AnnotationPair classAnnotationPair = classAnnotation(field);
				annotationPair = mergeAnnotations(fieldAnnotationPair, classAnnotationPair);
			} else {
				annotationPair = fieldAnnotationPair;
			}

			// if there is a capd annotation translate and apply it, then drill down
			if (annotationPair.capdAnnotation != null) {
				translate(target, field, annotationPair.capdAnnotation);
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
						String msg = MessageFormat.format(
								"cannot drill down, object for field '{0}' has not been constructed", field.getName());
						throw new I18NException(msg);
					}
				} catch (Exception e) {
					throw new I18NException("failed to drill down into I18N component", e);
				}
			}

			// if there is a value annotation translate and apply it (no drill down on value annotations)
			if (annotationPair.valueAnnotation != null) {
				translateValue(target, field, annotationPair.valueAnnotation);
			}

		}
	}

	private void translateValue(Object target, Field field, Annotation valueAnnotation) {
		field.setAccessible(true);
		// if it is a component, process the caption etc
		if (AbstractComponent.class.isAssignableFrom(field.getType())) {
			try {
				AbstractComponent component = (AbstractComponent) field.get(target);
				applyValueAnnotation(component, field, valueAnnotation);
			} catch (Exception e) {
				throw new I18NException("unable to apply value annotion to " + field.getName());
			}
		}

	}

	private void applyValueAnnotation(AbstractComponent component, Field field, Annotation annotation) {
		checkNotNull(component);
		checkNotNull(field);
		checkNotNull(annotation);

		I18NValueAnnotationReader<?> reader = (annotation instanceof I18NValueFlex) ? i18nValueFlexReader
				: i18nValueReader;
		if (annotation instanceof I18NValueFlex) {
			i18nValueFlexReader.setAnnotation((I18NValueFlex) annotation);
		} else {
			i18nValueReader.setAnnotation(annotation);
		}

		// set caption and description
		field.setAccessible(true);
		try {
			component.setLocale(reader.locale());
			if (Property.class.isAssignableFrom(component.getClass())) {
				@SuppressWarnings("unchecked")
				Property<String> property = (Property<String>) component;
				property.setValue(reader.value());
				log.debug("value set to '{}'", property.getValue());
			}

		} catch (IllegalArgumentException e) {
			String msg = MessageFormat.format("Unable to set I18N value for '{}'", field.getName());
			throw new I18NException(msg, e);
		}

	}

	/**
	 * Merge the field and class annotations with the field annotations taking precedence
	 *
	 * @param fieldAnnotationPair
	 * @param classAnnotationPair
	 * @return
	 */
	private AnnotationPair mergeAnnotations(AnnotationPair fieldAnnotationPair, AnnotationPair classAnnotationPair) {
		if (fieldAnnotationPair.capdAnnotation == null) {
			fieldAnnotationPair.capdAnnotation = classAnnotationPair.capdAnnotation;
		}
		if (fieldAnnotationPair.valueAnnotation == null) {
			fieldAnnotationPair.valueAnnotation = classAnnotationPair.valueAnnotation;
		}
		return fieldAnnotationPair;
	}

	private AnnotationPair fieldAnnotation(Field field) {
		log.debug("checking field '{}' for annotations", field.getName());
		Annotation[] annotations = field.getAnnotations();
		return scanAnnotations(annotations);

	}

	private AnnotationPair scanAnnotations(Annotation[] annotations) {
		AnnotationPair pair = new AnnotationPair();
		boolean capdFound = false;
		boolean valueFound = false;
		for (Annotation annotation : annotations) {
			String annotationClassName = annotation.annotationType().getName();
			if (!capdFound) {
				if (registeredAnnotations.contains(annotationClassName)) {
					pair.capdAnnotation = annotation;
					capdFound = true;
				}
			}
			if (!valueFound) {
				if (registeredValueAnnotations.contains(annotationClassName)) {
					pair.valueAnnotation = annotation;
					valueFound = true;
				}
			}
		}

		return pair;
	}

	private AnnotationPair classAnnotation(Field field) {
		Class<?> clazz = field.getType();
		log.debug("checking class '{}' of field '{}' for annotations", clazz.getName(), field.getName());
		Annotation[] annotations = clazz.getAnnotations();
		return scanAnnotations(annotations);

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
					String msg = MessageFormat.format(
							"object for field '{0}' has not been constructed, i18N cannot be applied", field.getName());
					throw new I18NException(msg);
				}
			} catch (Exception e) {
				String msg = MessageFormat.format("unable to access field '{0}', i18N cannot be applied",
						field.getName());
				throw new I18NException(msg, e);
			}
		}

	}

	private <T extends Annotation> void applyAnnotation(AbstractComponent component, Field field, Annotation annotation) {
		checkNotNull(component);
		checkNotNull(field);
		checkNotNull(annotation);

		I18NAnnotationReader<?> reader = (annotation instanceof I18NFlex) ? i18nFlexReader : i18nReader;
		if (annotation instanceof I18NFlex) {
			i18nFlexReader.setAnnotation((I18NFlex) annotation);
		} else {
			i18nReader.setAnnotation(annotation);
		}

		// set caption and description
		field.setAccessible(true);
		try {

			component.setLocale(reader.locale());
			log.debug("locale set to '{}'", component.getLocale().toLanguageTag());

			component.setCaption(reader.caption());
			log.debug("caption set to '{}'", component.getCaption());

			component.setDescription(reader.description());
			log.debug("description set to '{}'", component.getDescription());

		} catch (IllegalArgumentException e) {
			String msg = MessageFormat.format("Unable to set I18N caption or description for '{}'", field.getName());
			throw new I18NException(msg, e);
		}

		// These components have a value. Usually I18N would only be used for Label values. If no key is provided
		// the component value is left unchanged
		// if (reader.value() != null) {
		// if (Property.class.isAssignableFrom(field.getType())) {
		// try {
		// @SuppressWarnings("unchecked")
		// Property<String> c = (Property<String>) component;
		// c.setValue(reader.value());
		// log.debug("value set to '{}'", c.getValue());
		// } catch (Exception e) {
		// String msg = MessageFormat.format("Unable to set I18N value for ", field.getName());
		// throw new I18NException(msg, e);
		// }
		// }
		// }

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
						String header = translate.from(columnid, reader.locale());
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
