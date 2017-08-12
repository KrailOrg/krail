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

package uk.q3c.krail.core.option;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.I18NKey;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A utility class to find {@link OptionKey} instances by reflection
 * <p>
 * Created by David Sowerby on 07/08/15.
 */
public class OptionKeyLocator {
    private static Logger log = LoggerFactory.getLogger(OptionKeyLocator.class);


    /**
     * Scans the entire application by reflection for instance of {@link OptionKey} and returns a Set of data types they use.  Only works with static fields,
     * as {@link OptionContext} implementations are not instantiated (but there is no reason not to use static fields for Option keys
     *
     * @return Set of data types used by {@link OptionKey}
     */
    public Set<Class<?>> contextKeyTypes() {
        Reflections reflections = new Reflections();
        Set<Class<? extends OptionContext>> contexts = reflections.getSubTypesOf(OptionContext.class);
        Set<Class<?>> keyTypes = new HashSet<>();

        for (Class<? extends OptionContext> contextClass : contexts) {
            Set<Field> contextFields = ReflectionUtils.getAllFields(contextClass, p -> p.getType()
                                                                                        .equals(OptionKey.class));
            for (Field contextField : contextFields) {
                try {
                    contextField.setAccessible(true);
                    final OptionKey key = (OptionKey) contextField.get(null);
                    Object defaultValue = key.getDefaultValue();
                    if (defaultValue instanceof I18NKey) {
                        keyTypes.add(I18NKey.class);
                    } else if (defaultValue.getClass()
                                           .isEnum()) {
                        keyTypes.add(Enum.class);
                    } else {
                        keyTypes.add(defaultValue.getClass());
                    }
                } catch (IllegalAccessException | NullPointerException e) {
                    log.warn("unable to read field {}", contextField.getName());
                }
            }
        }
        return keyTypes;
    }


    /**
     * {@inheritDoc}
     */
    public Map<OptionKey, Class<?>> contextKeyMap(OptionContext context) {
        Set<Field> contextFields = ReflectionUtils.getAllFields(context.getClass(), p -> p.getType()
                                                                                          .equals(OptionKey.class));
        Map<OptionKey, Class<?>> keys = new HashMap<>();
        for (java.lang.reflect.Field field : contextFields) {
            field.setAccessible(true);

            try {
                OptionKey key = (OptionKey) field.get(context);
                if (key != null) {
                    keys.put(key, field.getType());
                }
            } catch (IllegalAccessException e) {
                log.error("Unable to access field {}", field.getName());
            }
        }
        return keys;
    }

}


