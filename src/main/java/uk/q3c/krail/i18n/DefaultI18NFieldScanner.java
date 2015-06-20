/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.vaadin.ui.AbstractComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by David Sowerby on 10/05/15.
 */
public class DefaultI18NFieldScanner implements I18NFieldScanner {


    private static Logger log = LoggerFactory.getLogger(DefaultI18NFieldScanner.class);
    private Map<AbstractComponent, AnnotationInfo> components;
    private LinkedList<Object> drillDowns;
    private I18NHostClassIdentifier i18NHostClassIdentifier;
    private List<Object> processedDrillDowns;

    @Inject
    protected DefaultI18NFieldScanner(I18NHostClassIdentifier i18NHostClassIdentifier) {
        this.i18NHostClassIdentifier = i18NHostClassIdentifier;
        components = new HashMap<>();
        drillDowns = new LinkedList<>();
        processedDrillDowns = new ArrayList<>();
    }


    @Override
    @Nonnull
    public Map<AbstractComponent, AnnotationInfo> annotatedComponents() {
        return components;
    }

    /**
     * Scans the class of target for I18N annotated fields, working up the inheritance tree.  Uses {@link #i18NHostClassIdentifier} to resolve a class which
     * has
     * been enhanced
     *
     * @param target
     */
    @Override
    public void scan(@Nonnull Object target) {
        checkNotNull(target);
        drillDowns.clear();
        processedDrillDowns.clear();
        components.clear();
        drillDowns.add(target);
        doScan(target);
    }

    /**
     * @param target
     */
    protected void doScan(Object target) {
        Class<?> classToScan = i18NHostClassIdentifier.getOriginalClassFor(target);
        log.debug("scanning '{}' for I18N Annotations", classToScan.getName());
        try {
            while (!classToScan.equals(Object.class)) {
                findAnnotatedFields(classToScan.getDeclaredFields(), target);
                classToScan = classToScan.getSuperclass();
            }
        } catch (IllegalAccessException iae) {
            log.error("I18N scan failed", iae);
        }
        processedDrillDowns.add(target);
        drillDowns.remove(target);
        while (!drillDowns.isEmpty()) {
            Object next = drillDowns.getFirst();

            //beware duplicates, they will cause stack overflow
            if (processedDrillDowns.contains(next)) {
                drillDowns.remove(next);
            } else {
                doScan(next);
            }
        }
    }

    /**
     * Fields are included for processing if they have one or more annotations which is itself annotated with {@link I18NAnnotation}.   Another list is
     * constructed for components which the processor should drill down into.
     * <p>
     * Fields are included for drill down if they are:<ol><li>annotated with {@link I18N} with value=true (the default)
     *
     * @param declaredFields
     *         declared fields, taken from a class in the overall hierarchy of {@code target}
     */
    private void findAnnotatedFields(Field[] declaredFields, Object target) throws IllegalAccessException {


        for (Field field : declaredFields) {
            log.debug("Capture all field and class annotations for '{}'  ", field.getName());
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            Annotation[] classAnnotations = field.getType()
                                                 .getDeclaredAnnotations();

            //do field annotations first, we can ignore class annotations if there are field annotations
            boolean found = evaluateFieldFromAnnotations(target, field, fieldAnnotations);
            log.debug("I18N field annotations found, class annotations will be ignored");
            if (!found) {
                evaluateFieldFromAnnotations(target, field, classAnnotations);
            }
            evaluateDrillDown(target, field);
        }

    }

    /**
     * @param field
     *         the field being processed
     * @param annotations
     *         the annotations associated with the field, whether they came from the field itself, or the class of its type.
     *
     * @throws IllegalAccessException
     */
    private boolean evaluateFieldFromAnnotations(Object target, Field field, Annotation[] annotations) throws IllegalAccessException {
        log.debug("evaluating annotations for field '{}'", field.getName());
        AnnotationInfo annotationInfo = new AnnotationInfo(field);

        // identify any I18N annotations for the component, but exclude the I18N Drill down
        for (Annotation annotation : annotations) {

            //is it an I18N annotation?
            if (annotation.annotationType()
                          .isAnnotationPresent(I18NAnnotation.class)) {
                log.debug("Annotation @{} found for field '{}'", annotation.annotationType(), field.getName());
                //we don't want I18N in this list, it is used to include / exclude drill down
                if (!annotation.annotationType()
                               .equals(I18N.class)) {
                    annotationInfo.getAnnotations()
                                  .add(annotation);
                }

            }
        }

        if (annotationInfo.getAnnotations()
                          .isEmpty()) {
            log.debug("No I18N annotations found for '{}'", field.getName());
            return false;
        } else {
            if (AbstractComponent.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                AbstractComponent abstractComponent = (AbstractComponent) field.get(target);
                if (abstractComponent != null) {
                    components.put(abstractComponent, annotationInfo);
                }
                log.debug("I18N annotation(s) found for '{}', added to components list", field.getName());
                return true;
            } else {
                throw new I18NException("I18N annotations (except for @18N), can only be applied to AbstractComponent");
            }

        }


    }


    private void evaluateDrillDown(Object target, Field field) throws IllegalAccessException {

        //try field annotations first;

        // if I18N(drillDown = true, just add it to drill downs)
        I18N i18N = field.getAnnotation(I18N.class);
        if (i18N != null) {
            log.debug("evaluating '{}' for @I18N field annotation drill down", field.getName());
            if (i18N.drillDown()) {
                addDrillDown(target, field);
                log.debug("'{}' has field annotation @18N(drillDown=true), added to drill downs", field.getName());
                return;
            } else {
                log.debug("'{}' has field annotation @18N(drillDown=false), not added to drill downs", field.getName());
                return;
            }
        }
        log.debug("No @I18N field annotation found for '{}', check its class for @18N drill down", field.getName());
        Class<?> fieldType = field.getType();
        i18N = fieldType.getAnnotation(I18N.class);
        if (i18N != null) {
            if (i18N.drillDown()) {
                addDrillDown(target, field);
                log.debug("'{}' has class annotation @18N(drillDown=true), added to drill downs", field.getName());
                return;
            } else {
                log.debug("'{}' has class annotation @18N(drillDown=false), not added to drill downs", field.getName());
                return;
            }
        }
    }


    private void addDrillDown(Object target, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Object component = field.get(target);
        if (component != null) {
            drillDowns.add(component);
            log.debug("'{}' included for drill down", field.getName());
        } else {
            log.debug("'{}' is null, not added to drill downs", field.getName());
        }
    }

    /**
     * Returns all the objects that were drilled into, including the inital target submitted to {@link #scan}
     *
     * @return all the objects that were drilled into, including the inital target submitted to {@link #scan}
     */
    @Override
    public List<Object> processedDrillDowns() {
        return processedDrillDowns;
    }
}
