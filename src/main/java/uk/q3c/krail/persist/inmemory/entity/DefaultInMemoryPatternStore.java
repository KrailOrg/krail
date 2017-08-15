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

package uk.q3c.krail.persist.inmemory.entity;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import uk.q3c.krail.i18n.persist.PatternCacheKey;
import uk.q3c.krail.persist.inmemory.InMemoryPatternStore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simplistic in memory store for I18N patterns, useful only for testing
 * <p>
 * Created by David Sowerby on 18/06/15.
 */
@Singleton
public class DefaultInMemoryPatternStore implements InMemoryPatternStore {

    private Map<PatternCacheKey, String> store = new HashMap<>();

    @Override
    public synchronized void put(PatternCacheKey cacheKey, String value) {
        store.put(cacheKey, value);
    }

    @Override
    public synchronized String remove(PatternCacheKey cacheKey) {
        return store.remove(cacheKey);
    }

    @Override
    public synchronized String get(PatternCacheKey cacheKey) {
        return store.get(cacheKey);
    }

    @Override
    public synchronized ImmutableMap<PatternCacheKey, String> getAsMap() {
        return ImmutableMap.copyOf(store);
    }

    @Override
    public synchronized long count() {
        return store.size();
    }
}
