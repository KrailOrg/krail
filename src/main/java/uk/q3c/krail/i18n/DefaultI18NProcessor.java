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
package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.util.MessageFormat;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class to manipulate Vaadin component settings to reflect locale changes. Class or field
 * annotations can be used to specify the keys to use, and this {@link I18NProcessor} implementation looks up the key
 * values
 * and sets caption, description and value properties of the component.
 * <p>
 * <p>
 * When a locale change occurs in {@link CurrentLocale}, {@link ScopedUI} updates itself and its current view. Other
 * views, which may have already been constructed, are updated as they become active.
 * <p>
 * For a full description see https://sites.google.com/site/q3cjava/internationalisation-i18n
 *
 * @author David Sowerby 8 Feb 2013
 */
public class DefaultI18NProcessor implements I18NProcessor {
    private static Logger log = LoggerFactory.getLogger(DefaultI18NProcessor.class);
    //this is also used to order priority in evaluate methods
    private final String[] methodNames = new String[]{"caption", "value", "description"};
    private final Translate translate;
    private CurrentLocale currentLocale;
    private Set<String> drillDownExclusions;

    @Inject
    protected DefaultI18NProcessor(CurrentLocale currentLocale, Translate translate, @DrillDownExclusions Set<String>
            drillDownExclusions) {
        super();
        this.currentLocale = currentLocale;
        this.translate = translate;
        this.drillDownExclusions = drillDownExclusions;
    }

    /**
     * Scans the {@code target} for fields with either a field or class I18N annotation. Caption, description and value
     * are applied according to the value of the combined annotations. Field annotations take precedence over class
     * annotations.  If there are multiple values for any given annotation method of caption(), description() or
     * value(), the end result will be one of the values supplied by an annotation but which one is indeterminate.
     * <p>
     * Drill down into the component is evaluated - it is assumed to be true, unless any annotation asserts it to be
     * false.
     *
     * @param target
     */
    @Override
    public void translate(Object target) {
        log.debug("scanning class '{}' for I18N annotations", target.getClass());
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            List<Annotation> i18NAnnotations = fieldAnnotations(field);
            AnnotationSet annotationSet = buildAnnotationSet(i18NAnnotations);
            final AnnotationSet mergedAnnotationSet = mergeClassAnnotations(field.getType(), annotationSet);
            Locale locale = evaluateLocale(annotationSet);

            applyAnnotationValues(target, field, mergedAnnotationSet, locale);
            if (Table.class.isAssignableFrom(field.getType())) {
                log.debug("field '{}' is a Table", field.getName());
                translateTable(target, field, locale);
            }

            boolean drillDown = evaluateDrillDown(mergedAnnotationSet, field);
            if (drillDown) {
                try {
                    log.debug("Drilling down into {}", field.getName());
                    field.setAccessible(true);
                    translate(field.get(target));
                } catch (IllegalAccessException e) {
                    log.warn("Unable to drill down into {}", field.getName());

                }
            }
        }
    }

    /**
     * Determines whether the processor should drill down into the current component for further I18N processing. If
     * there are different values set in different annotations, the order of priority is associated with the
     * annotation which provides the results, in this order, for: caption, value, description.  Annotations will
     * normally default to true, but as Krail developers will provide their own annotations, it is also possible that
     * no result is specified.  In this case, drillDown is returned as true.
     *
     * @param annotationSet
     */
    private boolean evaluateDrillDown(AnnotationSet annotationSet, Field field) {

        if (annotationSet.isEmpty()) {
            log.debug("Field {} is excluded from drill down, it has no I18N annotations", field.getName());
            return false;
        }

        // TODO  this would only happen by accident - flag it?
        Class<?> type = field.getType();
        if (type.isPrimitive()) {
            log.debug("Field is excluded from drill down, it is a primitive", field.getName());
            return false;
        }

        for (String exclusion : drillDownExclusions) {
            if (type.getName()
                    .startsWith(exclusion)) {
                log.debug("Field is excluded from drill down, it matches exclusion '{}'", exclusion);
                return false;
            }
        }

        for (String methodName : methodNames) {
            if (annotationSet.methodReturnValues.containsKey(methodName)) {
                Optional<Boolean> optDrillDown = annotationSet.methodReturnValues.get(methodName).drillDown;
                if (optDrillDown.isPresent()) {
                    return optDrillDown.get();
                }
            }
        }
        return true;
    }

    /**
     * Decides which locale to use from the settings in the annotations.  If there are different values set in
     * different annotations, the order of priority is associated with the annotation which provides the results, in
     * this order, for: caption, value, description.
     * <p>
     * If no Locale has been set by any of the annotations, {@link CurrentLocale} is used.
     *
     * @param annotationSet
     *
     * @return
     */
    protected Locale evaluateLocale(AnnotationSet annotationSet) {
        for (String methodName : methodNames) {
            if (annotationSet.methodReturnValues.containsKey(methodName)) {
                Optional<Locale> optLocale = annotationSet.methodReturnValues.get(methodName).locale;
                if (optLocale.isPresent()) {
                    return optLocale.get();
                }
            }
        }
        return currentLocale.getLocale();
    }


    protected AnnotationSet mergeClassAnnotations(Class<?> clazz, AnnotationSet fieldAnnotationSet) {
        List<Annotation> classAnnotations = classAnnotations(clazz);
        final AnnotationSet classAnnotationSet = buildAnnotationSet(classAnnotations);
        //field annotations override class annotations
        fieldAnnotationSet.methodReturnValues.forEach((k, v) -> classAnnotationSet.methodReturnValues.put(k, v));
        //class annotations end up being the merge of both field and class
        return classAnnotationSet;
    }


    /**
     * Identifies the first annotation available for each of the three possible methods ... caption() description()
     * value().  If the field has multiple annotations which return a value for the same method (for example multiple
     * caption() values), results are indeterminate, except that one of the values will be returned
     *
     * @param i18NAnnotations
     *
     * @return
     */
    protected AnnotationSet buildAnnotationSet(List<Annotation> i18NAnnotations) {


        AnnotationSet annotationSet = new AnnotationSet();
        for (String methodName : methodNames) {
            Optional<I18NKey> key = Optional.empty();
            Optional<Locale> locale = Optional.empty();
            Optional<Boolean> drillDown = Optional.empty();

            for (Annotation i18NAnnotation : i18NAnnotations) {


                key = (Optional<I18NKey>) key(i18NAnnotation, methodName);

                if (key.isPresent()) {
                    locale = readLocale(i18NAnnotation);
                    drillDown = readDrillDown(i18NAnnotation);
                    annotationSet.methodReturnValues.put(methodName, new AnnotationResult(i18NAnnotation, key,
                            locale, drillDown));
                    break;
                }
            }
        }
        return annotationSet;
    }

    private Optional<Boolean> readDrillDown(Annotation i18NAnnotation) {
        try {
            final Method drillDownMethod = i18NAnnotation.annotationType()
                                                         .getMethod("drillDown");
            boolean drillDown = (boolean) drillDownMethod.invoke(i18NAnnotation);
            return Optional.of(drillDown);
        } catch (Exception e) {
            log.warn("Unable to read drillDown method for {}", i18NAnnotation.annotationType());
            return Optional.of(true);
        }
    }

    private Optional<Locale> readLocale(Annotation i18NAnnotation) {
        try {
            final Method drillDownMethod = i18NAnnotation.annotationType()
                                                         .getMethod("locale");
            String localeTag = (String) drillDownMethod.invoke(i18NAnnotation);
            if (localeTag.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(Locale.forLanguageTag(localeTag));
        } catch (Exception e) {
            log.warn("Unable to read drillDown method for {}", i18NAnnotation.annotationType());
            return Optional.empty();
        }

    }

    /**
     * Returns an I18NKey value for the {@code annotationMethod} or Optional.empty() if none is found (which could be
     * either the method not being present or present but not returning a value.
     *
     * @param i18NAnnotation
     *
     * @return
     */
    protected Optional<?> key(Annotation i18NAnnotation, String annotationMethod) {
        Method[] methods = i18NAnnotation.annotationType()
                                         .getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName()
                      .equals(annotationMethod)) {
                try {
                    Object result = method.invoke(i18NAnnotation);
                    if (result != null) {
                        I18NKey key = (I18NKey) result;
                        return Optional.of(key);
                    } else {
                        return Optional.empty();
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }


    /**
     * Returns a list of valid I18N annotations for the {@code field}
     *
     * @param field
     *
     * @return
     */
    protected List<Annotation> fieldAnnotations(Field field) {
        Annotation[] annotations = field.getAnnotations();
        return validAnnotations(annotations);
    }

    /**
     * Returns a list of valid I18N annotations for the {@code clazz}
     *
     * @param clazz
     *
     * @return
     */
    protected List<Annotation> classAnnotations(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        return validAnnotations(annotations);
    }

    private List<Annotation> validAnnotations(Annotation[] annotations) {
        List<Annotation> list = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType()
                          .isAnnotationPresent(I18NAnnotation.class)) {
                list.add(annotation);
            }
        }
        return list;
    }


    public void applyAnnotationValues(@Nonnull Object target, @Nonnull Field field, @Nonnull AnnotationSet
            annotationSet, @Nonnull Locale locale) {
        field.setAccessible(true);
        // if it is a component, process the caption etc
        if (AbstractComponent.class.isAssignableFrom(field.getType())) {
            log.debug("field '{}' is a component, is class of '{}'", field.getName(), field.getType()
                                                                                           .getName());
            try {
                AbstractComponent component = (AbstractComponent) field.get(target);
                component.setLocale(locale);
                // in case component has not been constructed
                if (component != null) {
                    String methodName = "caption";
                    if (annotationSet.methodReturnValues.containsKey(methodName)) {
                        I18NKey key = annotationSet.methodReturnValues.get(methodName).methodValue.get();
                        component.setCaption(translate.from(key, locale));
                    }

                    methodName = "description";
                    if (annotationSet.methodReturnValues.containsKey(methodName)) {
                        I18NKey key = annotationSet.methodReturnValues.get(methodName).methodValue.get();
                        component.setDescription(translate.from(key, locale));
                    }

                    methodName = "value";
                    if (annotationSet.methodReturnValues.containsKey(methodName)) {
                        I18NKey key = annotationSet.methodReturnValues.get(methodName).methodValue.get();
                        if (Property.class.isAssignableFrom(field.getType())) {
                            try {
                                Property<String> c = (Property<String>) component;
                                c.setValue(translate.from(key, locale));
                                log.debug("value set to '{}'", c.getValue());
                            } catch (Exception e) {
                                String msg = MessageFormat.format("Unable to set I18N value for ", field.getName());
                                throw new I18NException(msg, e);
                            }
                        } else {
                            log.warn("value cannot be set for {}, it does not implement Property", field.getName());
                        }
                    }


                } else {
                    String msg = MessageFormat.format("object for field '{0}' has not been constructed, " +
                            "" + "i18N cannot be applied", field.getName());
                    throw new I18NException(msg);
                }
            } catch (Exception e) {
                String msg = MessageFormat.format("unable to access field '{0}', i18N cannot be applied", field
                        .getName());
                throw new I18NException(msg, e);
            }
        }

    }


    protected void translateTable(Object target, Field field, Locale locale) {
        field.setAccessible(true);
        // Table columns need special treatment
        AbstractComponent component = null;
        try {
            component = (AbstractComponent) field.get(target);
            Table table = (Table) component;
            Object[] columns = table.getVisibleColumns();
            List<String> headers = new ArrayList<>();
            for (Object column : columns) {
                if (column instanceof LabelKey) {
                    I18NKey columnKey = (I18NKey) column;
                    String header = translate.from(columnKey, locale);
                    headers.add(header);
                } else {
                    headers.add(column.toString());
                }
            }
            String headerArray[] = headers.toArray(new String[]{});
            table.setColumnHeaders(headerArray);
        } catch (IllegalAccessException e) {
            log.error("Unable to set I18N table columns headers for " + field.getName(), e);

        }


    }


    private class AnnotationSet {
        Map<String, AnnotationResult> methodReturnValues = new HashMap<>();

        /**
         * Returns false if any of the methodNames are missing
         *
         * @return
         */
        public boolean isComplete() {
            for (String methodName : methodNames) {
                if (!methodReturnValues.containsKey(methodName)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isEmpty() {
            return methodReturnValues.isEmpty();
        }
    }

    private class AnnotationResult {
        Annotation annotation;
        Optional<I18NKey> methodValue;
        Optional<Locale> locale;
        Optional<Boolean> drillDown;

        public AnnotationResult(Annotation i18NAnnotation, Optional<I18NKey> methodValue, Optional<Locale> locale,
                                Optional<Boolean> drillDown) {
            annotation = i18NAnnotation;
            this.methodValue = methodValue;
            this.locale = locale;
            this.drillDown = drillDown;
        }
    }

}
