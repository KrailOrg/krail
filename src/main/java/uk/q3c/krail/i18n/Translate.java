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

import java.text.Collator;
import java.util.Locale;

/**
 * Interface for providing translations of I18N patterns retrieved using {@link I18NKey}.  A check for supported locales can be disabled (generally used by
 * utility classes)
 *
 * Revised by David Sowerby on 14/07/15.
 * Created by David Sowerby on 23/10/14.
 */
public interface Translate {

    /**
     * Returns a translated pattern, with parameters substituted by the provided arguments
     *
     * @param checkLocaleIsSupported
     *         if true, the locale is first checked to ensure that it is a supported locale, as defined by {@link I18NModule}
     * @param key
     *         the pattern key
     * @param locale
     *         the Locale to use for translation
     * @param arguments
     *         any arguments used for the pattern parameters
     * @param <E>
     *         an I18NKey
     *
     * @return the translated pattern, with parameters substituted by the provided arguments
     *
     * @throws UnsupportedLocaleException
     *         if #checkLocaleIsSupported is true, and locale has not been defined as a supported locale in {@link I18NModule}
     */
    <E extends Enum<E> & I18NKey> String from(boolean checkLocaleIsSupported, I18NKey key, Locale locale, Object... arguments);


    /**
     * The same as calling {@link #from(boolean, I18NKey, Locale, Object...)} } with checkLocaleIsSupported==true
     */
    <E extends Enum<E> & I18NKey> String from(I18NKey key, Locale locale, Object... arguments);


    /**
     * The same as {@link #from(I18NKey, Locale, Object...)}, but using {@link CurrentLocale} and checkLocaleIsSupported==true
     */
    <E extends Enum<E> & I18NKey> String from(I18NKey key, Object... arguments);


    /**
     * convenience method to get Collator instance for the {@link CurrentLocale}
     *
     * @return Collator instance for the {@link CurrentLocale}
     */
    Collator collator();
}
