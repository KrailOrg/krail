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

package uk.q3c.krail.core.persist.cache.option;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.persist.common.option.OptionDaoDelegate;

import java.util.Optional;

/**
 * A cache for use with {@link Option}.  Implementations should be thread safe
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
public interface OptionCache {

    LoadingCache<OptionCacheKey, Optional<?>> getCache();

    /**
     * Passes the call to the underlying persistence, and if that is successful, writes the entry to the cache
     * as well.
     *
     * @param optionCacheKey a unique identifier for the entry to be written
     * @param value          the value to bw written
     * @param <T>            the type of the value
     */
    <T> void write(OptionCacheKey<T> optionCacheKey, Optional<T> value);

    /**
     * If there is a value in the cache (which is actually held as an Optional<T>), then the Optional is returned.
     * If there is no cache entry, {@link DefaultOptionCacheLoader} is invoked. This will populate the cache with a result from persistence or an
     * {@link Optional#empty()} if no value is found.
     * <p>
     * The result from the loader takes into account whether the {@link OptionCacheKey#getRankOption()} is set to highest, lowest or specific
     * <p>
     *
     * @param defaultValue   the value, wrapped in Optional, to use if none found
     * @param optionCacheKey unique identifier
     * @param <T>            the type of the value
     * @return the value for the key, if returns the {@code defaultValue} if none found, or an error or exception occurs
     * while trying to load the cache
     */
    <T> Optional<T> get(Optional<T> defaultValue, OptionCacheKey<T> optionCacheKey);

    /**
     * Pass the delete call to the underlying {@link OptionDaoDelegate}, then removes the entry from the cache
     *
     * @param optionCacheKey a unique identifier for the entry to be deleted
     * @return the previous value before being deleted
     */
    Optional<?> delete(OptionCacheKey<?> optionCacheKey);

    /**
     * Returns a value from the cache only if it is present in the cache (that is, no attempt is made to load the cache
     * with a value if it is not present)
     *
     * @param optionCacheKey unique identifier
     * @return Returns a value from the cache only if it is present in the cache, otherwise null
     */
    Optional<?> getIfPresent(OptionCacheKey<?> optionCacheKey);

    CacheStats stats();

    /**
     * Invalidates all entries in the cache see {@link Cache#invalidateAll()}.  If you want the results to be immediate you may need to follow this with {@link
     * #cleanup}
     */
    void flush();

    long cacheSize();

    /**
     * Performs any pending maintenance operations needed by the cache. See {@link Cache#cleanUp()}
     */
    void cleanup();

    /**
     * {@link #flush()} followed by {@link #cleanup()}
     */
    void clear();
}
