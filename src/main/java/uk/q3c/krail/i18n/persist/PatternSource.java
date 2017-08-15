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

package uk.q3c.krail.i18n.persist;

import uk.q3c.krail.i18n.I18NKey;

import java.lang.annotation.Annotation;
import java.util.Locale;

/**
 * Created by David Sowerby on 07/12/14.
 *
 * @param <C> the cache type in use
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
    void clearCache(Class<? extends Annotation> source);
}
