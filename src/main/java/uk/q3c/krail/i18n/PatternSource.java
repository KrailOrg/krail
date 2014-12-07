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
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The {@link PatternSource} builds on standard Java {@link ResourceBundle} functionality and is used by the {@link
 * Translate} implementation to retrieve localisation patterns. Multiple sources of bundles may be required, for
 * example, using property files, Java classes, database .. or any other method the Krail developer wishes to use.
 * These are specified in the {@link I18NModule} or its sub-class, as is the order in which they are accessed in the
 * search for a translation. <p/>
 * In addition, {@link PatternSource} implementations support the generation of stubs to assist when building up
 * localisation values, and the writing out of key-value pairs via a {@link BundleWriter}.
 * <p>
 * <p>
 * Supported locales are defined in the {@link I18NModule} or a sub-class of it.
 * <p>
 * Implementations should not assume that only valid locales will be passed to its methods (where valid means that a
 * locale has been included as a supported locale)
 * <p>
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
    <E extends Enum<E> & I18NKey> Optional<String> retrievePattern(E key, Locale locale);

    /**
     * Generates an implementation specific stub for the key - value pair.  This is typically used as part of the
     * process to generate files for translation.   The value for
     * the stub is determined by the implementation, usually from UserOption.
     *
     * @param overwrite
     *         if true, the stub overwrites any exiting value.  If overwrite is false, then a stub is only generated if
     *         key either does not exist or has an empty value
     * @param key
     *         the key the stub will be for
     * @param locale
     *         the locale to generate the stub for
     */
    <E extends Enum<E> & I18NKey> void generateStub(String source, E key, Locale locale, boolean overwrite);

    /**
     * Generates implementation specific stubs for all the {@code locales}.  For some implementations this may be more
     * efficient than repeated calls to {@link #generateStub(I18NKey, Locale)}.  Does not overwrite an existing entry
     * for {@code key}.  The value for the stub is determined by the implementation, usually from UserOption.
     *
     * @param overwrite
     *         if true, the stub overwrites any exiting value.  If overwrite is false, then a stub is only generated if
     *         key either does not exist or has an empty value
     * @param key
     *         the the stub(s) will be for
     * @param locales
     *         set of Locale instances for which to generate a stub
     */
    <E extends Enum<E> & I18NKey> void generateStub(String source, E key, Set<Locale> locales, boolean overwrite);

    /**
     * Generates implementation specific stubs for all the supported locales{@code locales}.Does not overwrite an
     * existing entry
     * for {@code key}.  The value for the stub is determined by the implementation, usually from UserOption.
     *
     * @param overwrite
     *         if true, the stub overwrites any exiting value.  If overwrite is false, then a stub is only generated if
     *         key either does not exist or has an empty value
     * @param key
     *         the key the stub(s) will be for
     */
    <E extends Enum<E> & I18NKey> void generateStub(String source, E key, boolean overwrite);

    /**
     * Write the key-value set(s) to persistence, for all {@code locales}.  {@link BundleWriter} implementations must
     * provide
     * their own methods for setting up file paths, database connection or other pre-requisites.
     *
     * @param keyClass
     *         the keys to use
     * @param locales
     *         the locales to write files for
     * @param allKeys
     *         if true, output is generated for all the keys of the keyClass, otherwise only the keys defined in a
     *         locale are
     *         written out for that locale. If allKeys is true, but a key has no value, the value is set according to
     *         options provided by the implementation
     * @param <E>
     *         then Enum class represented by the keyClass
     *
     * @throws IOException
     * @see #writeOut(Class, boolean)
     */

    <E extends Enum<E> & I18NKey> void writeOut(String source, BundleWriter<E> writer, E sampleKey, Set<Locale> 
            locales, boolean allKeys) throws IOException;

    /**
     * Write the key-value set(s) for all supported locales, to persistence.  Individual implementations will
     * provide their own methods for setting up file paths, database connection or other pre-requisites
     *
     * @param keyClass
     *         the keys to use
     * @param locales
     *         the locales to write files for
     * @param allKeys
     *         if true, output is generated for all the keys of the keyClass, otherwise only the keys defined in a
     *         locale are
     *         written out for that locale. If allKeys is true, but a key has no value, the value is set according to
     *         options provided by the implementation
     * @param <E>
     *         then Enum class represented by the keyClass
     *
     * @throws IOException
     * @see #writeOut(Class, Set, boolean)
     */
    <E extends Enum<E> & I18NKey> void writeOut(String source, BundleWriter<E> writer, E sampleKey, boolean allKeys) 
            throws IOException;

    /**
     * Merge key-value pairs "top-down" from one source to the next source, from the sources specified in {@code
     * sources}, for the given {@code locale}s.  The result that the last source in the {@link sources} will contain a
     * merge of all the previous sources.
     * <p>
     * If {@code overwrite} is true, all non-null, non-empty values are transferred from one source to the next source,
     * overwriting any values that are already there. <p/> If {@code overwrite} is false, values are only copied from
     * one source to the next if the second source has no matching key, or the value of that key is null or empty.
     *
     * @param otherSource
     * @param overwrite
     *
     * @see #mergeSource(Set, PatternSource)
     */
    public <E extends Enum<E> & I18NKey> void mergeSources(E sampleKey, Set<Locale> locales, boolean overwrite, 
                                                           String... sources);


    /**
     * Set the value for a key, for a given {@code source} and {@code locale}
     */
    <E extends Enum<E> & I18NKey> void setKeyValue(String source, E key, Locale locale, String value);

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence.  Applies to all
     * supported locales
     */
    <E extends Enum<E> & I18NKey> void reset(String source, E sampleKey);

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence, for all {@code
     * #locales}
     */
    <E extends Enum<E> & I18NKey> void reset(String source, E sampleKey, Set<Locale> locales);

    /**
     * Returns a list of sources in the order they are processed when locating a key value
     *
     * @param sampleKey
     *         any constant from the key class for which the result is needed
     *
     * @return
     */
    List<String> bundleSourceOrder(I18NKey sampleKey);

    /**
     * Gets the {@link UserOption} source order for the bundle with {@code baseName}
     *
     * @param baseName
     *         the base name of a bundle, for example, "Labels"
     *
     * @return a
     */

    List<String> getOptionSourceOrder(String baseName);

    List<String> getOptionSourceOrderDefault();

    void setOptionSourceOrderDefault(String... tags);

    void setOptionSourceOrder(String baseName, String... tags);
}
