package uk.q3c.krail.i18n;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionContext;

import java.util.Locale;
import java.util.Set;

/**
 * Manages the retrieval of I18N patterns from potentially multiple sources.  Caches values for keys once they have
 * been
 * located.  Note that the scope of this class must be the same or narrower than for {@link CurrentLocale}
 * <p>
 * Created by David Sowerby on 07/12/14.
 */
@VaadinSessionScoped
public class DefaultPatternSource implements UserOptionContext, PatternSource {
    public enum UserOptionProperty {MAX_CACHE_SIZE}


    private static Logger log = LoggerFactory.getLogger(DefaultPatternSource.class);
    private LoadingCache<PatternCacheKey, String> cache;
    private Set<Locale> supportedLocales;
    private UserOption userOption;


    @Inject

    protected DefaultPatternSource(@SupportedLocales Set<Locale> supportedLocales, UserOption userOption,
                                   PatternCacheLoader cacheLoader) {
        this.supportedLocales = supportedLocales;
        this.userOption = userOption;
        userOption.configure(this, UserOptionProperty.class);
        //CacheLoader has no interface so the cast is necessary to allow alternative PatternCacheLLoader implementations
        //although all implementations would need to extend CacheLoader
        cache = CacheBuilder.newBuilder()
                            .maximumSize(userOption.get(1000, UserOptionProperty.MAX_CACHE_SIZE))
                            .build((CacheLoader) cacheLoader);

    }

    /**
     * Retrieves a pattern string from cache, or loads the cache with a value from persistence if needed.  The
     * required Locale is checked for each
     * source in turn, and if that fails to provide a result then the next candidate Locale is used, and each source
     * tried again.  If all candidate locales, for all sources, is exhausted and still no pattern is found, then the
     * name of the key is returned.
     *
     * The that sources are accessed is determined by {@link #bundleSourceOrder}
     *
     * The standard Java method for identifying candidate locales is used - see ResourceBundle.Control.getCandidateLocales
     *
     * @param key
     * @param locale
     * @param <E>
     *
     * @return
     */
    @Override
    public <E extends Enum<E> & I18NKey> String retrievePattern(E key, Locale locale) {
        PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
        return cache.getUnchecked(cacheKey);
    }


    @Override
    public UserOption getUserOption() {
        return userOption;
    }
}
