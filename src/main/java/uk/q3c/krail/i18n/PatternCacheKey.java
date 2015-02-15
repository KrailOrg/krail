package uk.q3c.krail.i18n;

import java.util.Locale;

/**
 * Created by David Sowerby on 08/12/14.
 */
public class PatternCacheKey {


    private final Enum<?> key;
    private final Locale requestedLocale; // the locale actually requested
    private Locale actualLocale; // the actual locale the value was found in (from candidates). Not part of the key
    private String source; // the source the value was found, in, not part of the key

    /**
     * A cache key used to uniquely identify an I18N pattern from its I18NKey and locale.
     *
     * @param key
     * @param requestedLocale
     */
    public <E extends Enum<E> & I18NKey> PatternCacheKey(E key, Locale requestedLocale) {

        this.key = key;
        this.requestedLocale = requestedLocale;
        this.actualLocale = requestedLocale; // initially the same
    }

    public Locale getActualLocale() {
        return actualLocale;
    }

    public void setActualLocale(Locale actualLocale) {
        this.actualLocale = actualLocale;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Enum<?> getKey() {
        return key;
    }

    public Locale getRequestedLocale() {
        return requestedLocale;
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
        if (requestedLocale != null ? !requestedLocale.equals(that.requestedLocale) : that.requestedLocale != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (requestedLocale != null ? requestedLocale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PatternCacheKey{" +
                "key=" + key +
                ", requestedLocale=" + requestedLocale +
                ", actualLocale=" + actualLocale +
                ", source='" + source + '\'' +
                '}';
    }
}
