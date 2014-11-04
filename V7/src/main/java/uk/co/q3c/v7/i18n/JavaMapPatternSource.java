/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.i18n;

import com.google.common.base.Optional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * An implementation of {@link PatternSource} which uses static maps to provide the pattern for an {@link I18NKey}
 * <p/>
 * Created by David Sowerby on 04/11/14.
 */
public class JavaMapPatternSource implements PatternSource {

    /**
     * Returns the translated String pattern for {@code key}, for {@code locale}, or {@link Optional.isAbsent()} if
     * there is no pattern for the key.
     * <p/>
     * This implementation uses a static map to define the patterns for translation.  The {@link MapResourceBundle}
     * used to locate the pattern determined by the actual parameter value of key (that is, a key of type I18N<Labels>
     * will use the Labels class as the map source)
     * <p/>
     *
     * @param key
     *         the key to look up
     * @param locale
     *
     * @return the String pattern for {@code key}, or {@link Optional.isAbsent()} if there is no pattern for the key
     */
    @Override
    public Optional<String> retrievePattern(I18NKey key, Locale locale) {

        Type[] genericInterfaces = key.getClass()
                                      .getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];

        Type actualType = parameterizedType.getActualTypeArguments()[0];
        Class bundleClazz = (Class) actualType;

        MapResourceBundle bundle = (MapResourceBundle) ResourceBundle.getBundle(bundleClazz.getName(), locale);


        String pattern = bundle.getValue(key);
        if (pattern == null) {
            return Optional.absent();
        }
        return Optional.of(pattern);

    }

    /**
     * Generates an implementation specific stub for the key - value pair.  This is typically used as part of the
     * process to generate files for translation
     *
     * @param key
     *         the key the stub will be for
     * @param locale
     */
    @Override
    public void generateStub(I18NKey key, Locale locale) {

    }

    /**
     * Generates implementation specific stubs for all the {@code locales}.  For some implementations this may be more
     * efficient than repeated calls to {@link #generateStub(I18NKey, Locale)}
     *
     * @param key
     *         the the stub(s) will be for
     * @param locales
     */
    @Override
    public void generateStub(I18NKey key, Set<Locale> locales) {

    }
}
