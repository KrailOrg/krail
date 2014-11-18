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

package uk.q3c.krail.i18n;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Set;

/**
 * Krail allows for multiple sources for I18N patterns.  The order in which they are accessed in the search for a
 * translation is determined by {@link I18NModule} or its sub-class.  Implementations may include, for example, using
 * property files, Java files, database ... or any other method the Krail developer wishes to use.
 * <p/>
 * Supported locales are defined in the {@link I18NModule} or a sub-class of it.
 * <p/>
 * Implementations should assume that only valid locales will be passed to its methods (where valid means that a locale
 * has been included as a supported locale)
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
    <E extends Enum<E>> Optional<String> retrievePattern(I18NKey key, Locale locale);

    /**
     * Generates an implementation specific stub for the key - value pair.  This is typically used as part of the
     * process to generate files for translation.  Does not overwrite an existing entry for {@code key}.  The value for
     * the stub is determined by the implementation, usually from UserOption.
     *
     * @param key
     *         the key the stub will be for
     * @param locale
     *         the locale to generate the stub for
     */
    <E extends Enum<E>> void generateStub(I18NKey key, Locale locale);

    /**
     * Generates implementation specific stubs for all the {@code locales}.  For some implementations this may be more
     * efficient than repeated calls to {@link #generateStub(I18NKey, Locale)}.  Does not overwrite an existing entry
     * for {@code key}.  The value for the stub is determined by the implementation, usually from UserOption.
     *
     * @param key
     *         the the stub(s) will be for
     * @param locales
     *         set of Locale instances for which to generate a stub
     */
    <E extends Enum<E>> void generateStub(I18NKey key, Set<Locale> locales);

    /**
     * Generates implementation specific stubs for all the supported locales{@code locales}.
     *
     * @param key
     *         the the stub(s) will be for
     */
    <E extends Enum<E>> void generateStub(I18NKey key);

    /**
     * Write the key-value set(s) to persistence, for all {@code locales}.  Individual implementations will provide
     * their own methods for setting up file paths, database connection or other pre-requisites.
     *
     * @param keyClass
     *         the keys to use
     * @param locales
     *         the locales to write files for
     * @param allKeys
     *         if true, all the keys for the keyClass are generated, otherwise only the keys defined in a locale are
     *         written out for that locale. If allKeys is true, but a key has no value, the value is set according to
     *         options provided by the implementation
     * @param <E>
     *         then Enum class represented by the keyClass
     *
     * @throws IOException
     * @see #writeOut(Class, boolean)
     */

    <E extends Enum<E>> void writeOut(Class<? extends I18NKey> keyClass, Set<Locale> locales, boolean allKeys) throws
            IOException;

    /**
     * Write the key-value set(s) for all supported locales, to persistence.  Individual implementations will
     * provide their own methods for setting up file paths, database connection or other pre-requisites
     *
     * @param keyClass
     *         the keys to use
     * @param locales
     *         the locales to write files for
     * @param allKeys
     *         if true, all the keys for the keyClass are generated, otherwise only the keys defined in a locale are
     *         written out for that locale.  If allKeys is true, but a key has no value, the value is set according to
     *         options provided by the implementation
     * @param <E>
     *         then Enum class represented by the keyClass
     *
     * @throws IOException
     * @see #writeOut(Class, Set, boolean)
     */
    <E extends Enum<E>> void writeOut(Class<? extends I18NKey> keyClass, boolean allKeys) throws IOException;

    /**
     * Merge key-value pairs from {@code otherSource} into this source, for the given {@code locale}.  This method is
     * generally used for merging key-value pairs from an external source.  If you are merging another PatternSource
     * implementation, {@link #mergeSource(Set, PatternSource)} is probably a better option.
     * <p/>
     * If {@code overwrite} is true, all non-null, non-empty values are transferred from otherSource to this source,
     * overwriting any values that are already in this source. <p/> If {@code overwrite} is false, values from {@code
     * otherSource} are only written to this source, if the same key in {@code thisSource} has a null or empty value.
     *
     * @param otherSource
     * @param overwrite
     *
     * @see #mergeSource(Set, PatternSource)
     */
    <E extends Enum<E>> void mergeSource(Locale locale, EnumMap<E, String> otherSource, boolean overwrite);

    /**
     * Merge key-value pairs for {@code keyClass} from {@code otherSource} into this source, for the given {@code
     * locales}.
     * <p/>
     * If {@code overwrite} is true, all values are transferred from otherSource to this source,
     * overwriting any values that are already in this source. <p/> If {@code overwrite} is false, values from {@code
     * otherSource} are only written to this source, if the same key in {@code thisSource} is missing or has an empty
     * value.
     *
     * @param keyClass
     *         the I18NKeys to process
     * @param locales
     *         the locales to process
     * @param otherSource
     *         the other source of key-value mappings
     * @param overwrite
     *         if true, overwrite the key-value pairs in this source; if add keys which are missing from this source or
     *         overwrite those with an empty value.
     *
     * @see #mergeSource(Locale, EnumMap, boolean)
     */
    <E extends Enum<E>> void mergeSource(Class<? extends I18NKey> keyClass, Set<Locale> locales, PatternSource
            otherSource, boolean overwrite);


    /**
     * Set the value for a key, for a given Locale
     */
    <E extends Enum<E>> void setKeyValue(I18NKey key, Locale locale, String value);

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence.  Applies to all
     * supported locales
     */
    <E extends Enum<E>> void reset(Class<? extends I18NKey> keyClass);

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence, for all {@code
     * #locales}
     */
    <E extends Enum<E>> void reset(Class<? extends I18NKey> keyClass, Set<Locale> locales);
}
