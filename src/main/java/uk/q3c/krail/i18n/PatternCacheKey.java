package uk.q3c.krail.i18n;

import java.util.Locale;

/**
 * Created by David Sowerby on 08/12/14.
 */
public class PatternCacheKey {


    private final Enum<?> key;
    private final Locale locale;

    /**
     * A cache key used to uniquely identify an I18N pattern from its I18NKey, locale and an optional context.
     * The context can be used where bulk loading a set of values - for a form for example - reduces latency from
     * persistence.
     *
     * @param key
     * @param locale
     * @param context
     */
    public <E extends Enum<E> & I18NKey> PatternCacheKey(E key, Locale locale) {

        this.key = key;
        this.locale = locale;
    }

    public Enum<?> getKey() {
        return key;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatternCacheKey)) {
            return false;
        }

        PatternCacheKey that = (PatternCacheKey) o;

        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }
}
