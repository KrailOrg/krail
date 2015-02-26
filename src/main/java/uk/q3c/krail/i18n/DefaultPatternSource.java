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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages the retrieval of I18N patterns from potentially multiple sources.  Caches values for keys once they have
 * been located.  Note that the scope of this class is set in {@link I18NModule#bindPatternSource()} or its sub-class.
 * <p>
 * Created by David Sowerby on 07/12/14.
 */

public class DefaultPatternSource implements OptionContext, PatternSource<LoadingCache<PatternCacheKey, String>> {


    private static Logger log = LoggerFactory.getLogger(DefaultPatternSource.class);
    private LoadingCache<PatternCacheKey, String> cache;
    private Option option;


    @Inject

    protected DefaultPatternSource(Option option,
                                   PatternCacheLoader cacheLoader) {
        this.option = option;
        option.init(this);
        //CacheLoader has no interface so the cast is necessary to allow alternative PatternCacheLLoader implementations
        //although all implementations would need to extend CacheLoader
        cache = CacheBuilder.newBuilder()
                            .maximumSize(option.get(1000, LabelKey.Maximum_Cache_Size))
                            .build((CacheLoader) cacheLoader);

    }


    /**
     * Retrieves a pattern string from cache, or loads the cache with a value from persistence if needed (see {@link
     * DefaultPatternCacheLoader}. This method always returns a value.  Keys without values will have a value
     * assigned of the key.name(), with underscores replaced by spaces.
     *
     * Note that this method does not check that the {@code locale} is supported ... it is assumed that by this stage
     * any unsupported locales have been dealt with.  This method will, however, return the name of the key if no
     * entry is found, including when a locale is not supported
     *
     * @param key
     *         the I18NKey to identify the pattern
     * @param locale
     *         the locale for the translation - not that this is not checked to be a supported locale
     * @param <E>
     *         an Enum implementing I18NKey
     *
     * @return a pattern for the key and locale, or the name of the key if no value is found for the key
     */
    @Override
    public <E extends Enum<E> & I18NKey> String retrievePattern(E key, Locale locale) {
        checkNotNull(key);
        checkNotNull(locale);
        PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
        return cache.getUnchecked(cacheKey);
    }

    @Override
    public LoadingCache<PatternCacheKey, String> getCache() {
        return cache;
    }

    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }

    @Override
    public void clearCache() {
        cache.invalidateAll();
    }

    /**
     * Clears the cache of all entries for {@code source}.  T
     *
     * @param source
     */
    @Override
    public void clearCache(String source) {
        List<PatternCacheKey> keysToRemove = new ArrayList<>();
        for (PatternCacheKey key : cache.asMap()
                                        .keySet()) {
            if (key.getSource()
                   .equals(source)) {
                keysToRemove.add(key);
            }
        }
        cache.invalidateAll(keysToRemove);
    }
}
