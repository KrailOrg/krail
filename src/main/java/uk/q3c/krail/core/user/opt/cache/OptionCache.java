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

package uk.q3c.krail.core.user.opt.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionDao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * @param optionCacheKey
     *         a unique identifier for the entry to be written
     * @param value
     *         the value to bw written
     * @param <T>
     *         the type of the value
     */
    <T> void write(@Nonnull OptionCacheKey optionCacheKey, @Nonnull Optional<T> value);

    /**
     * If there is a value in the cache (which is actually held as an Optional<T>), then the Optional is returned.
     * If there is no cache entry, the loader will be invoked ({@link DefaultOptionCacheLoader} by default), which will
     * populate the cache with a result from persistence or an {@link Optional#empty()} if no value is
     * found..
     * <p>
     * The result from the loader is actually the highest ranked value of the hierarchy
     * <p>
     *
     * @param defaultValue
     *         the value, wrapped in Optional, to use if none found
     * @param optionCacheKey
     *         unique identifier
     * @param <T>
     *         the type of the value
     *
     * @return the value for the key, if returns the {@code defaultValue} if none found, or an error or exception occurs
     * while trying to load the cache
     */
    <T> Optional<T> get(@Nonnull Optional<T> defaultValue, @Nonnull OptionCacheKey optionCacheKey);

    /**
     * Pass the delete call to the underlying {@link OptionDao}, then removes the entry from the cache
     *
     * @param optionCacheKey
     *         a unique identifier for the entry to be deleted
     *
     * @return the previous value before being deleted
     */
    @Nullable
    Optional<?> delete(@Nonnull OptionCacheKey optionCacheKey);

    /**
     * Returns a value from the cache only if it is present in the cache (that is, no attempt is made to load the cache
     * with a value if it is not present)
     *
     * @param optionCacheKey
     *         unique identifier
     *
     * @return Returns a value from the cache only if it is present in the cache, otherwise null
     */
    @Nullable
    Optional<?> getIfPresent(@Nonnull OptionCacheKey optionCacheKey);

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
