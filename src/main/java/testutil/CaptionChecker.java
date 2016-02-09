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

package testutil;

import com.vaadin.ui.Component;
import uk.q3c.krail.core.i18n.Caption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checks a Vaadin component container (for example a KrailView) to ensure that all fields have a @Caption annotation
 * <p>
 * Created by David Sowerby on 19 Jan 2016
 */
public class CaptionChecker {

    /**
     * Returns true only if all of the component fields of {@code out} have a @Caption annotation
     *
     * @param cut class under test
     * @return true only if all of the component fields of {@code out} have a @Caption annotation
     */
    public boolean check(Class<?> cut, String... excludeFieldNames) {
        System.out.println("Checking " + cut + " for I18N annotated component fields");
        boolean allOk = true;
        List<String> excludes = excludeFieldNames == null ? new ArrayList<>() : Arrays.asList(excludeFieldNames);
        Field[] declaredFields = cut.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            if (!excludes.contains(field.getName())) {
                if (Component.class.isAssignableFrom(field.getType())) {
                    if (!field.isAnnotationPresent(Caption.class)) {
                        allOk = false;
                        System.out.println("Field does not have a caption annotation: " + field.getName());
                    }
                }
            }
        }
        return allOk;
    }
}
