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
import uk.q3c.krail.core.validation.ValidationKey;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Utilities methods to manipulate I18N Patterns
 *
 * Created by David Sowerby on 14/12/14.
 */
public class DefaultPatternUtility implements PatternUtility {
    private Map<String, BundleReader> bundleReaders;
    private PatternSource patternSource;

    @Inject
    protected DefaultPatternUtility(Map<String, BundleReader> bundleReaders, PatternSource patternSource) {
        this.bundleReaders = bundleReaders;
        this.patternSource = patternSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E> & I18NKey> void writeOut(@Nonnull BundleWriter writer, @Nonnull Class<E> keyClass, @Nonnull Set<Locale> locales, @Nonnull
    Optional<String> bundleName) throws IOException {

        checkNotNull(writer);
        checkNotNull(keyClass);
        checkNotNull(locales);
        checkNotNull(bundleName);
        E[] keys = keyClass.getEnumConstants();


        for (Locale locale : locales) {
            DirectResourceBundle<E> bundle = new DirectResourceBundle<>(keyClass);
            for (E key : keys) {
                String pattern = patternSource.retrievePattern(key, locale);
                bundle.put(key, pattern);
            }
            writer.setBundle(bundle);
            writer.write(locale, bundleName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Enum<E> & I18NKey> void writeOutExclusive(@Nonnull String source, @Nonnull BundleWriter writer, @Nonnull Class<E> keyClass, @Nonnull
    Locale locale, @Nonnull Optional<String> bundleName) throws IOException {
        checkNotNull(source);
        checkNotNull(writer);
        checkNotNull(keyClass);
        checkNotNull(locale);
        checkNotNull(bundleName);
        E[] keys = keyClass.getEnumConstants();
        BundleReader reader = bundleReaders.get(source);


        DirectResourceBundle<E> bundle = new DirectResourceBundle<>(keyClass);
        for (E key : keys) {
            PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
            Optional<String> value = reader.getValue(cacheKey, source, false, false, source);
            if (value.isPresent()) {
                bundle.put(key, value.get());
            }
        }
        writer.setBundle(bundle);
        writer.write(locale, bundleName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exportKeysToDatabase(@Nonnull Set<Locale> locales, @Nonnull DatabaseBundleWriter writer) throws IOException {
        checkNotNull(writer);
        checkNotNull(locales);
        export(LabelKey.Yes, locales, writer);
        export(MessageKey.Invalid_URI, locales, writer);
        export(DescriptionKey.Auto_Stub, locales, writer);
        export(ValidationKey.AssertFalse, locales, writer);
    }


    /**
     * Writes out all the keys for each Locale
     *
     * @param sampleKey
     *         sample key to identify the bundle
     * @param locales
     *         the locales that need to be written
     * @param writer
     *         the writer to use to write
     * @param <E>
     *         an I18NKey
     *
     * @throws IOException
     *         if the write fails
     */
    protected <E extends Enum<E> & I18NKey> void export(@Nonnull E sampleKey, @Nonnull Set<Locale> locales, @Nonnull DatabaseBundleWriter writer) throws IOException {
        checkNotNull(writer);
        checkNotNull(locales);
        checkNotNull(sampleKey);
        for (Locale locale : locales) {
            writer.setBundle(sampleKey);
            writer.write(locale, Optional.empty());
        }
    }



}
