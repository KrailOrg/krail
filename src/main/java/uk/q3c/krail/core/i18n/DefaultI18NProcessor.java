/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.i18n;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.ScopedUI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class to manipulate Vaadin component settings to reflect locale changes. Class or field annotations can be used to specify the keys to use, and
 * this {@link I18NProcessor} implementation looks up the key values and sets caption, description and value properties of the component.
 * <p>
 * <p>
 * When a locale change occurs in {@link CurrentLocale}, {@link ScopedUI} updates itself and its current view. Other views, which may have already been
 * constructed, are updated as they become active.
 * <p>
 * <p>A {@link I18NFieldScanner} is used to read metadata (the annotations) from the target's fields
 * <p>
 * For a full description see https://sites.google.com/site/q3cjava/internationalisation-i18n
 *
 * @author David Sowerby 8 Feb 2013
 */
public class DefaultI18NProcessor implements I18NProcessor {
    private static Logger log = LoggerFactory.getLogger(DefaultI18NProcessor.class);
    private final Translate translate;
    private CurrentLocale currentLocale;
    private Provider<I18NFieldScanner> i18NFieldScannerProvider;

    @Inject
    protected DefaultI18NProcessor(CurrentLocale currentLocale, Translate translate, Provider<I18NFieldScanner> i18NFieldScannerProvider) {
        super();
        this.currentLocale = currentLocale;
        this.translate = translate;
        this.i18NFieldScannerProvider = i18NFieldScannerProvider;
    }

    /**
     * Scans the {@code target} for fields with either a field or class I18N annotation. Caption, description and value are applied according to the value of
     * the combined annotations. Field annotations take precedence over class annotations, that is if there is any I18N field annotation, all I18N class
     * annotations are ignored.  If there are multiple values for any given annotation method of caption(), description(), value() or locale(), the end
     * result will be one of the values supplied by an annotation but which one is indeterminate.
     * <p>
     * Drill down into a component (to look for nested components with I18N annotations) will occur only when a field or class is annotated with @I18N
     *
     * @param target
     *         the object to process for I18N annotation.  If null, is just ignored
     */
    @Override
    public void translate(@Nullable Object target) {
        if (target == null) {
            return;
        }
        log.debug("scanning class '{}' for I18N annotations", target.getClass());
        List<Object> processedFields = new ArrayList<Object>();
        translate(processedFields, target);
    }

    /**
     * Translates {@code target} and keeps a running list of processed fields (or more accurately, the object contained by a Field).  The latter is to ensure
     * that the same field is not evaluated twice - but is not only a waste of effort, but causes loops, where, for example a component contains a reference
     * to its parent.
     * <p>
     * Nulls are entirely valid if a Field has not been constructed, and are therefore just ignored.
     *
     * @param processedFields
     *         the fields already processed
     * @param target
     *         the field to be evaluated now
     */
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    protected void translate(@Nonnull List<Object> processedFields, @Nullable Object target) {
        if (target == null) {
            return;
        }
        processedFields.add(target);
        I18NFieldScanner i18NFieldScanner = i18NFieldScannerProvider.get();
        i18NFieldScanner.scan(target);

        try {
            processComponents(i18NFieldScanner.annotatedComponents(), target);
        } catch (Exception e) {
            throw new I18NException("I18N processing failed", e);
        }
    }

    protected void processComponents(Map<AbstractComponent, AnnotationInfo> componentAnnotations, Object target) throws NoSuchFieldException,
            IllegalAccessException {
        for (Map.Entry<AbstractComponent, AnnotationInfo> entry : componentAnnotations.entrySet()) {
            AbstractComponent component = entry.getKey();
            AnnotationInfo annotationInfo = entry.getValue();
            AnnotationValues annotationValues = annotationValues(annotationInfo.getAnnotations());
            if (component instanceof Table) {
                processTable((Table) component, annotationValues, annotationInfo);
            } else if (component instanceof Grid) {
                processGrid((Grid) component, annotationValues, annotationInfo);
            } else {
                applyAnnotationValues(component, annotationValues, annotationInfo);
            }

        }
    }

    /**
     * Returns a set of values from a list of annotations.  There is no guarantee of evaluation order, so if a field has, for example, two annotations with a
     * caption() value, it is uncertain which will be selected
     *
     * @param annotations
     *         a list of annotations to evaluate
     *
     * @return a set of values from a list of annotations
     */
    private AnnotationValues annotationValues(List<Annotation> annotations) {
        AnnotationValues av = new AnnotationValues();
        for (Annotation annotation : annotations) {
            //if there is a value, use it, but don't overwrite existing with empty
            Optional<I18NKey> optKey = retrieveKey(annotation, "caption");
            if (optKey.isPresent()) {
                av.captionKey = optKey;
            }
            optKey = retrieveKey(annotation, "description");
            if (optKey.isPresent()) {
                av.descriptionKey = optKey;
            }
            optKey = retrieveKey(annotation, "value");
            if (optKey.isPresent()) {
                av.valueKey = optKey;
            }
            Optional<Locale> optLocale = retrieveLocale(annotation);
            if (optLocale.isPresent()) {
                av.locale = optLocale;
            }
        }
        return av;
    }

    /**
     * Returns an I18NKey value for the {@code annotationMethod} or Optional.empty() if none is found (which could be
     * either the method not being present or present but not returning a value.
     *
     * @param i18NAnnotation
     *         the annotation to assess
     * @param annotationMethod
     *         the method name to look for
     *
     * @return an I18NKey value for the {@code annotationMethod} or Optional.empty() if none is found
     */
    protected Optional<I18NKey> retrieveKey(@Nonnull Annotation i18NAnnotation, @Nonnull String annotationMethod) {
        checkNotNull(i18NAnnotation);
        checkNotNull(annotationMethod);
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

                } catch (Exception e) {
                    log.error("Unable to read annotation", e);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * returns a locale from  {@code i18NAnnotation} if it has one, or Optional.empty() if it has not
     *
     * @param i18NAnnotation
     *         the annotation to assess
     *
     * @return a locale from  {@code i18NAnnotation} if it has one, or Optional.empty() if it has not
     */
    protected Optional<Locale> retrieveLocale(@Nonnull Annotation i18NAnnotation) {
        checkNotNull(i18NAnnotation);

        //if there is not locale method, simply return empty()
        try {
            Method method = i18NAnnotation.annotationType()
                                          .getDeclaredMethod("locale");
            String tag = (String) method.invoke(i18NAnnotation);
            if ((tag == null) || (tag.isEmpty())) {
                return Optional.empty();
            }
            return Optional.of(Locale.forLanguageTag(tag));

        } catch (NoSuchMethodException e) {
            return Optional.empty();

        } catch (Exception e) {
            log.error("Unable to read annotation", e);
            return Optional.empty();
        }


    }

    /**
     * Sets the I18N values for the Table itself, and also iterates the visible columns for column ids which are I18NKeys, and translates those as well
     *
     * @param table
     *         the table to process
     * @param annotationValues
     *         the values to apply
     * @param annotationInfo
     *         used primarily for the Field name
     */
    protected void processTable(Table table, AnnotationValues annotationValues, AnnotationInfo annotationInfo) {
        // Table columns need special treatment
        applyAnnotationValues(table, annotationValues, annotationInfo);

        // do the column headers
        Object[] columns = table.getVisibleColumns();
        Locale locale = annotationValues.locale.isPresent() ? annotationValues.locale.get() : currentLocale.getLocale();

        List<String> headers = new ArrayList<>();
        for (Object column : columns) {
            if (column instanceof I18NKey) {
                I18NKey columnKey = (I18NKey) column;
                String header = translate.from(columnKey, locale);
                headers.add(header);
            } else {
                headers.add(column.toString());
            }
        }
        String headerArray[] = headers.toArray(new String[headers.size()]);
        table.setColumnHeaders(headerArray);

    }

    /**
     * Applies annotation values to {@code component}
     *
     * @param component
     *         the component to be updated
     * @param annotationValues
     *         the annotation values to apply
     * @param annotationInfo
     *         used primarily to identify the Field, and therefore its name
     */
    private void applyAnnotationValues(AbstractComponent component, AnnotationValues annotationValues, AnnotationInfo annotationInfo) {
        // set locale first
        Locale locale = annotationValues.locale.isPresent() ? annotationValues.locale.get() : currentLocale.getLocale();
        component.setLocale(locale);

        // set caption, description & value if available
        if (annotationValues.captionKey.isPresent()) {
            component.setCaption(translate.from(annotationValues.captionKey.get(), locale));
        }
        if (annotationValues.descriptionKey.isPresent()) {
            component.setDescription(translate.from(annotationValues.descriptionKey.get(), locale));
        }
        if (annotationValues.valueKey.isPresent()) {
            if (component instanceof Property) {
                //noinspection unchecked
                ((Property) component).setValue(translate.from(annotationValues.valueKey.get(), locale));
            } else {
                log.warn("Field {} has a value annotation but does not implement Property.  Annotation ignored", annotationInfo.getField()
                                                                                                                               .getName());
            }
        }

    }

    /**
     * Sets the I18N values for the Grid itself, and also iterates the columns for column ids which are I18NKeys, and translates those as well
     *
     * @param grid
     *         the Grid to process
     * @param annotationValues
     *         the annotation values to apply
     * @param annotationInfo
     *         used primarily to identify the Field, and therefore its name
     */
    protected void processGrid(Grid grid, AnnotationValues annotationValues, AnnotationInfo annotationInfo) {

        // do the grid itself
        applyAnnotationValues(grid, annotationValues, annotationInfo);

        // now do the column headers
        Locale locale = annotationValues.locale.isPresent() ? annotationValues.locale.get() : currentLocale.getLocale();
        final List<Grid.Column> columns = grid.getColumns();

        for (Grid.Column column : columns) {
            if (column.getPropertyId() instanceof I18NKey) {
                I18NKey columnKey = (I18NKey) column.getPropertyId();
                String header = translate.from(columnKey, locale);
                column.setHeaderCaption(header);
            } else {
                column.setHeaderCaption(column.getPropertyId()
                                              .toString());
            }
        }
    }


    private static class AnnotationValues {
        Optional<I18NKey> captionKey = Optional.empty();
        Optional<I18NKey> descriptionKey = Optional.empty();
        Optional<I18NKey> valueKey = Optional.empty();
        Optional<Locale> locale = Optional.empty();
    }

}
