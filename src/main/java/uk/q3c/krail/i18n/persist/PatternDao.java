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

import java.util.Optional;

/**
 * Common interface for persisting I18N key-value pairs (with Locale) using {@link PatternCacheKey}
 * <p>
 * Created by David Sowerby on 15/04/15.
 */
public interface PatternDao {

    /**
     * Write {@code value} to persistence for the I18NKey & Locale provided by (@code cacheKey}
     *
     * @param cacheKey specifies the I18NKey & Locale
     * @param value    the value to write
     * @return the saved entity
     */
    Object write(PatternCacheKey cacheKey, String value);

    /**
     * Delete the {@code value} entry from persistence for the I18NKey & Locale provided by (@code cacheKey}
     *
     * @param cacheKey specifies the I18NKey & Locale to identify the entry to delete
     * @return the Optional with previous value for the cacheKey, or Optional.empty() if there was no previous value
     */

    Optional<String> deleteValue(PatternCacheKey cacheKey);


    /**
     * Gets a value from persistence for the I18NKey & Locale provided by (@code cacheKey}.  This method only evaluates a result for the {@link
     * PatternCacheKey#getActualLocale()} as the logic for checking Locale alternatives is contained within {@link PatternSource}.  Also note that {@link
     * PatternCacheKey#getActualLocale()} is initially set to the same value as {@link PatternCacheKey#getRequestedLocale()}
     *
     * @param cacheKey specifies the I18NKey & Locale to locate a value for
     * @return an Optional wrapped value if there is one or an Optional.empty() if not
     */

    Optional<String> getValue(PatternCacheKey cacheKey);


    /**
     * Returns the connection url
     *
     * @return the connection url
     */
    String connectionUrl();

    /**
     * returns the number of entries
     *
     * @return the number of entries
     */
    long count();


}
