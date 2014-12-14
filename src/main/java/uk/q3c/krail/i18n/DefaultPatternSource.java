package uk.q3c.krail.i18n;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manages the retrieval of I18N patterns from potentially multiple sources.  Caches values for keys once they have
 * been located.  Note that the scope of this class is set in {@link I18NModule#bindPatternSource()} or its sub-class.
 * <p>
 * Created by David Sowerby on 07/12/14.
 */

public class DefaultPatternSource implements UserOptionContext, PatternSource<LoadingCache<PatternCacheKey, String>> {
    public enum UserOptionProperty {MAX_CACHE_SIZE}


    private static Logger log = LoggerFactory.getLogger(DefaultPatternSource.class);
    private LoadingCache<PatternCacheKey, String> cache;
    private UserOption userOption;


    @Inject

    protected DefaultPatternSource(UserOption userOption,
                                   PatternCacheLoader cacheLoader) {
        this.userOption = userOption;
        userOption.configure(this, UserOptionProperty.class);
        //CacheLoader has no interface so the cast is necessary to allow alternative PatternCacheLLoader implementations
        //although all implementations would need to extend CacheLoader
        cache = CacheBuilder.newBuilder()
                            .maximumSize(userOption.get(1000, UserOptionProperty.MAX_CACHE_SIZE))
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
        PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
        return cache.getUnchecked(cacheKey);
    }

    @Override
    public LoadingCache<PatternCacheKey, String> getCache() {
        return cache;
    }

    @Override
    public UserOption getUserOption() {
        return userOption;
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
