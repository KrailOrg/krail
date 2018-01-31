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

package uk.q3c.krail.testutil.view;

import com.vaadin.ui.Component;
import uk.q3c.krail.core.i18n.Caption;
import uk.q3c.krail.core.view.ViewBase;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks a Vaadin component container (for example a KrailView) to ensure that all fields have a @Caption annotation
 * <p>
 * Created by David Sowerby on 19 Jan 2016
 */
public class ViewFieldChecker {
    private Set<String> fieldsMissingAnnotation;
    private Set<String> fieldsMissingId;
    private Set<String> fieldsNotConstructed;
    private ViewBase view;
    private Set<String> fieldsWithoutI18N;
    private Set<String> fieldsWithoutId;

    public ViewFieldChecker(ViewBase view, Set<String> fieldsWithoutI18N, Set<String> fieldsWithoutId) {
        this.view = view;
        this.fieldsWithoutI18N = fieldsWithoutI18N;
        this.fieldsWithoutId = fieldsWithoutId;
        fieldsMissingAnnotation = new HashSet<>();
        fieldsMissingId = new HashSet<>();
        fieldsNotConstructed = new HashSet<>();
    }

    /**
     * Returns true only if all of the component fields of {@code out} have a @Caption annotation
     *
     * @return true only if all of the component fields of {@code out} have a @Caption annotation
     */
    public boolean check() throws IllegalAccessException {
        Class<? extends ViewBase> cut = view.getClass();
        System.out.println("Checking " + cut + " for I18N annotated component fields");
        boolean allI18N = true;
        Field[] declaredFields = cut.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            if (Component.class.isAssignableFrom(field.getType())) {
                if (!field.isAnnotationPresent(Caption.class)) {
                    annotationMissing(field);
                }
                field.setAccessible(true);
                Component component = (Component) field.get(view);
                if (component == null) {
                    System.out.println(field.getName() + "has not been constructed");
                    fieldsNotConstructed.add(field.getName());
                }
            }
        }
        return fieldsMissingAnnotation.isEmpty() && fieldsMissingId.isEmpty() && fieldsNotConstructed.isEmpty();
    }

    private void annotationMissing(Field field) {
        if (!fieldsWithoutI18N.contains(field.getName())) {
            System.out.println("Field does not have a caption annotation: " + field.getName());
            fieldsMissingAnnotation.add(field.getName());
        }
    }
}
