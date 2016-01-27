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

package uk.q3c.krail.core.i18n;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import uk.q3c.krail.core.validation.ValidationKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities methods to manipulate I18N Patterns
 * <p>
 * Created by David Sowerby on 14/12/14.
 */
public class DefaultPatternUtility implements PatternUtility {

    private PatternSource patternSource;
    private Set<Locale> supportedLocales;


    @Inject
    protected DefaultPatternUtility(PatternSource patternSource, @SupportedLocales Set<Locale> supportedLocales) {
        this.patternSource = patternSource;
        this.supportedLocales = supportedLocales;
    }

    /**
     * Export I18N Pattern key value pairs from {@code source} to {@code targetDao} for all bundles and locales specified.  Unlike {@link #export(PatternDao,
     * Set, Set)}, which uses
     * {@link PatternSource}, this method takes input directly from the source {@link PatternDao} without assessing Locale candidates
     *
     * @param source
     * @param target
     *         the PatternDao to send the output to
     * @param bundles
     *         the I18NKey classes to export (each key class is equivalent to a bundle)
     * @param locales
     *         the Locales to export
     *
     * @return a count of the keys processed
     */
    @Override
    public long export(@Nonnull PatternDao source, @Nonnull PatternDao target, @Nonnull Set<Class<? extends I18NKey>> bundles, @Nonnull Set<Locale> locales,
                       boolean autoStub, boolean stubWithKeyName, @Nullable String stubValue) {
        checkNotNull(source);
        checkNotNull(target);
        checkNotNull(bundles);
        checkNotNull(locales);
        int c = 0;
        for (Locale locale : locales) {
            for (Class<? extends I18NKey> bundleClass : bundles) {
                I18NKey[] keys = bundleClass.getEnumConstants();
                for (I18NKey key : keys) {
                    PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
                    Optional<String> pattern = source.getValue(cacheKey);
                    if (pattern.isPresent()) {
                        target.write(cacheKey, pattern.get());
                        c++;
                    } else if (autoStub) {
                        target.write(cacheKey, stubValue((Enum) key, stubWithKeyName, stubValue));
                        c++;
                    }
                }
            }
        }
        return c;
    }

    protected String stubValue(Enum key, boolean stubWithKeyName, String stubValue) {
        return ((stubWithKeyName) ? key.name() : stubValue).replace('_', ' ');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long exportCoreKeys(@Nonnull PatternDao target) {
        ImmutableSet<Class<? extends I18NKey>> bundles = ImmutableSet.of(LabelKey.class, DescriptionKey.class, MessageKey.class, ValidationKey.class);
        return export(target, bundles, supportedLocales);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long export(@Nonnull PatternDao target, @Nonnull Set<Class<? extends I18NKey>> bundles, @Nonnull Set<Locale> locales) {
        checkNotNull(target);
        checkNotNull(bundles);
        checkNotNull(locales);
        int c = 0;
        for (Locale locale : locales) {
            for (Class<? extends I18NKey> bundleClass : bundles) {
                I18NKey[] keys = bundleClass.getEnumConstants();
                for (I18NKey key : keys) {

                    String pattern = patternSource.retrievePattern((Enum) key, locale);
                    PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
                    target.write(cacheKey, pattern);
                    c++;
                }
            }
        }
        return c;
    }


}
