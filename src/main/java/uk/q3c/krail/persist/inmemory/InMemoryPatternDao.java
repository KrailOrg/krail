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

package uk.q3c.krail.persist.inmemory;


import com.google.inject.Inject;
import uk.q3c.krail.i18n.persist.PatternCacheKey;
import uk.q3c.krail.i18n.persist.PatternDao;

import java.util.Optional;

import static com.google.common.base.Preconditions.*;

public class InMemoryPatternDao implements PatternDao {

    private InMemoryPatternStore store;

    @Inject
    public InMemoryPatternDao(InMemoryPatternStore inMemoryPatternStore) {
        this.store = inMemoryPatternStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object write(PatternCacheKey cacheKey, String value) {
        checkNotNull(cacheKey);
        checkNotNull(value);
        store.put(cacheKey, value);
        return value;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Optional<String> deleteValue(PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        String result = store.remove(cacheKey);
        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Optional<String> getValue(PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        String v = store.get(cacheKey);
        if (v == null) {
            return Optional.empty();
        } else {
            return Optional.of(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String connectionUrl() {
        return "in-memory";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return store.count();
    }


}
