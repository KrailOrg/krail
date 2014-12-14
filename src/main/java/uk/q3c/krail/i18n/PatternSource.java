package uk.q3c.krail.i18n;

import java.util.Locale;

/**
 * Created by David Sowerby on 07/12/14.
 * @param C the cache type in use
 */
public interface PatternSource<C extends Object> {

    /**
     * Retrieve an I18N pattern to match the key and locale given
     *
     * @param key
     * @param locale
     * @param <E>
     *
     * @return
     */
    <E extends Enum<E> & I18NKey> String retrievePattern(E key, Locale locale);

    C getCache();

    /**
     * Clears the entire cache
     */
    void clearCache();

    /**
     * Clears the cache of all entries for {@code source}.  T
     *
     * @param source
     */
    void clearCache(String source);
}
