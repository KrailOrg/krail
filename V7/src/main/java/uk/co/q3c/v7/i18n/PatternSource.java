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

import java.util.Locale;
import java.util.Set;

/**
 * Krail allows for multiple sources for I18N patterns.  The order in which they are accessed in the search for a
 * translation is determined by {@link I18NModule} or its sub-class.  Implementations may include, for example, using
 * property files, Java files, database ... or any other method the Krail developer wishes to use.
 * <p/>
 * Created by David Sowerby on 04/11/14.
 */
public interface PatternSource {

    /**
     * Returns the translated String pattern for {@code key}, for {@code locale}, or {@link Optional.isAbsent()} if
     * there is no pattern for the key.  Implementations are expected to return {@link Optional.isAbsent()}, regardless
     * of the reason for a pattern being absent - for example, a database implementation should return {@link
     * Optional.isAbsent()} if the database connection is lost, or if there is just no key in the database
     *
     * @param key
     *         the key to look up
     *
     * @return the String pattern for {@code key}, or {@link Optional.isAbsent()} if there is no pattern for the key
     */
    Optional<String> retrievePattern(I18NKey key, Locale locale);

    /**
     * Generates an implementation specific stub for the key - value pair.  This is typically used as part of the
     * process to generate files for translation
     *
     * @param key
     *         the key the stub will be for
     * @param locale
     *         the locale to generate the stub for
     */
    void generateStub(I18NKey key, Locale locale);

    /**
     * Generates implementation specific stubs for all the {@code locales}.  For some implementations this may be more
     * efficient than repeated calls to {@link #generateStub(I18NKey, Locale)}
     *
     * @param key
     *         the the stub(s) will be for
     * @param locales
     *         set of Locale instances for which to generate a stub
     */
    void generateStub(I18NKey key, Set<Locale> locales);
}
