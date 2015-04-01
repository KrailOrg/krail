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

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.krail.core.user.opt.OptionModule;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a cache implementation for {@link Option}.  The {@code get()} methods use the value of the {@link OptionCacheKey} to determines which {@link
 * UserHierarchy} to use, and whether to take the lowest, highest or specific ranked value.
 * <p>
 * Scope is set in {@link OptionModule} but it is assumed that this class needs to be thread safe.
 * Created by David Sowerby on 19/02/15.
 */

@ThreadSafe
public class DefaultOptionCache implements OptionCache {

    private static Logger log = LoggerFactory.getLogger(DefaultOptionCache.class);
    private final LoadingCache<OptionCacheKey, Optional<Object>> cache;
    private final Provider<OptionDao> daoProvider;

    @Inject
    public DefaultOptionCache(Provider<OptionDao> daoProvider, OptionCacheProvider cacheProvider) {
        this.daoProvider = daoProvider;
        cache = cacheProvider.get();
    }

    /**
     * Write value to the store, and updates the cache
     *
     * @param cacheKey
     *         unique identifier
     * @param value
     *         the value to set
     * @param <T>
     *         the type of the value
     */
    @Override
    public synchronized <T> void write(@Nonnull OptionCacheKey cacheKey, @Nonnull T value) {
        checkNotNull(cacheKey);
        checkNotNull(value);
        // write to store first just in case there's a problem
        log.debug("writing value {} for cacheKey {} via option dao ", value, cacheKey);
        daoProvider.get()
                   .write(cacheKey, value);
        // TODO this does not update the cache
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull OptionCacheKey optionCacheKey) {
        checkNotNull(optionCacheKey);
        checkNotNull(defaultValue);
        //this will trigger the cacheLoader if not already in the cache
        Optional<Object> optionalValue;
        try {
            optionalValue = cache.getUnchecked(optionCacheKey);
            if (!optionalValue.isPresent()) {
                return defaultValue;
            }
        } catch (Throwable e) {
            log.error("Returning default value of {}, exception or error was thrown during load. Exception was:  {}", defaultValue, e);
            return defaultValue;
        }
        Object value = optionalValue.get();
        if (value.getClass()
                 .isAssignableFrom(defaultValue.getClass())) {
            //noinspection unchecked
            return (T) value;
        } else {
            log.error("option value returned is of the wrong type for {}, returning default of {}", optionCacheKey, defaultValue);
            return defaultValue;
        }
    }


    @Override
    @Nullable
    public synchronized Object delete(@Nonnull OptionCacheKey optionCacheKey) {
        checkNotNull(optionCacheKey);
        // delete from store first just in case there's a problem
        Object result = daoProvider.get()
                                   .deleteValue(optionCacheKey);
        cache.invalidate(optionCacheKey);

        return result;
    }


    @Nullable
    @Override
    public synchronized Object getIfPresent(@Nonnull OptionCacheKey optionCacheKey) {
        checkNotNull(optionCacheKey);
        return cache.getIfPresent(optionCacheKey);
    }

    @Override
    public synchronized CacheStats stats() {
        return cache.stats();
    }

    @Override
    public synchronized void flush() {
        cache.invalidateAll();
    }

    @Override
    public long cacheSize() {
        return cache.size();
    }


}
