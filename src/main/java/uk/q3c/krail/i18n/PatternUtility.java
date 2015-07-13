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

import uk.q3c.krail.core.user.opt.Option;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Implementations provide some general purpose utilities for managing I18N Patterns
 * <p>
 * Created by David Sowerby on 14/12/14.
 */
public interface PatternUtility {

    /**
     * Write out the keys and values for the combined {@code sources}.  This method calls {@link
     * PatternSource#retrievePattern (Enum, Locale)} for each key in {@code keyClass}, then writes out all the keys
     * and values using the {@code writer}.  Keys without values will have a value assigned of the key.name(), with
     * underscores replaced by spaces, so the output will always list all keys.
     * <p>
     * This method effectively merges all the keys for all the sources, using the source ordering provided in {@link
     * PatternSource}, and fills in any missing values according to the auto-stub {@link Option} settings in the {@link
     * PatternCacheLoader}
     *
     * @param writer
     *         the BundleWriter implementation to use
     * @param keyClass
     * @param locales
     *         the locales to write out.  For class and property writers multiple locales will output multiple files
     * @param bundleName
     *         optionally use a bundle name different to that defined by the {@code keyClass}
     */
    <E extends Enum<E> & I18NKey> void writeOut(BundleWriter writer, Class<E> keyClass, Set<Locale> locales,
                                                Optional<String> bundleName) throws IOException;

    /**
     * Write out the keys and values for a specific source and locale,  Only those keys which have a value assigned
     * (which could be an empty String) are written out.
     *
     * @param writer
     *         the BundleWriter implementation to use @param keyClass
     * @param locale
     *         the locale to write out.
     * @param bundleName
     *         optionally use a bundle name different to that defined by the {@code keyClass}
     */
    <E extends Enum<E> & I18NKey> void writeOutExclusive(String source, BundleWriter writer, Class<E> keyClass,
                                                         Locale locale, Optional<String> bundleName) throws IOException;


    /**
     * Exports all keys (and translations where available) to the database writer identified by {@code writer}.
     *
     * @param writer
     *         the writer to use to export
     * @param locales
     *         the locales that entries should be created for
     *
     * @throws IOException
     *         if the write cannot take place for any reason
     */
    void exportKeysToDatabase(@Nonnull Set<Locale> locales, @Nonnull DatabaseBundleWriter writer) throws IOException;
}
