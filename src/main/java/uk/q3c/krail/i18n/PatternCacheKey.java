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

package uk.q3c.krail.i18n;


import java.lang.annotation.Annotation;
import java.util.Locale;

/**
 * Uniquely identifies a Pattern in the cache.  See {@link #equals} and the javadoc against the fields for which fields are used to form that unique key
 * <p>
 * Created by David Sowerby on 08/12/14.
 */
public class PatternCacheKey {


    private final I18NKey key; // the I18NKey to look up the pattern.  Part of the key
    private final Locale requestedLocale; // the locale actually requested.  Part of the key
    private Locale actualLocale; // the actual locale the value was found in (from candidates). Not part of the key
    private Class<? extends Annotation> source; // the source the value was found, in, not part of the key

    /**
     * A cache key used to uniquely identify an I18N pattern from its I18NKey and locale.
     *
     * @param key
     *         an I18NKey
     * @param requestedLocale
     */
    public PatternCacheKey(I18NKey key, Locale requestedLocale) {

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

    public Class<? extends Annotation> getSource() {
        return source;
    }

    public void setSource(Class<? extends Annotation> source) {
        this.source = source;
    }

    public I18NKey getKey() {
        return key;
    }

    public Enum getKeyAsEnum() {
        return (Enum) key;
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
        return !(requestedLocale != null ? !requestedLocale.equals(that.requestedLocale) : that.requestedLocale != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        return 31 * result + (requestedLocale != null ? requestedLocale.hashCode() : 0);
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
