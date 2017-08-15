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

package uk.q3c.krail.core.option.cache;

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import uk.q3c.krail.option.persist.OptionCacheKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by David Sowerby on 28/06/15.
 */
public class MockCache implements LoadingCache<OptionCacheKey, Optional<?>> {
    Map<OptionCacheKey, Optional<?>> values = new HashMap<>();


    @Override
    public Optional<?> get(OptionCacheKey key) throws ExecutionException {
        return null;
    }


    @Override
    public Optional<?> getUnchecked(OptionCacheKey key) {
        return values.get(key);
    }


    @Override
    public ImmutableMap<OptionCacheKey, Optional<?>> getAll(Iterable<? extends OptionCacheKey> keys) throws ExecutionException {
        return null;
    }


    @Override
    public Optional<?> apply(OptionCacheKey key) {
        return null;
    }


    @Override
    public void refresh(OptionCacheKey key) {

    }


    @Override
    public Optional<?> getIfPresent(Object key) {
        return values.get(key);
    }


    @Override
    public Optional<?> get(OptionCacheKey key, Callable<? extends Optional<?>> valueLoader) throws ExecutionException {
        return null;
    }


    @Override
    public ImmutableMap<OptionCacheKey, Optional<?>> getAllPresent(Iterable<?> keys) {
        return null;
    }

    @Override
    public void put(OptionCacheKey key, Optional<?> value) {

    }


    @Override
    public void putAll(Map<? extends OptionCacheKey, ? extends Optional<?>> m) {

    }


    @Override
    public void invalidate(Object key) {

    }


    @Override
    public void invalidateAll(Iterable<?> keys) {

    }

    @Override
    public void invalidateAll() {

    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public CacheStats stats() {
        return null;
    }

    @Override
    public ConcurrentMap<OptionCacheKey, Optional<?>> asMap() {
        return null;
    }

    @Override
    public void cleanUp() {

    }

    public void setValue(OptionCacheKey cacheKey, Optional<?> value) {
        values.put(cacheKey, value);
    }
}
