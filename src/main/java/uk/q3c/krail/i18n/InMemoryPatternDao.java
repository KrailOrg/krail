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

package uk.q3c.krail.i18n;


import com.google.inject.Inject;
import uk.q3c.krail.core.user.opt.cache.OptionKeyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class InMemoryPatternDao implements PatternDao {

    private InMemoryPatternStore store;

    @Inject
    public InMemoryPatternDao(InMemoryPatternStore inMemoryPatternStore) {
        this.store = inMemoryPatternStore;
    }

    /**
     * Write {@code value} to persistence for the I18NKey & Locale provided by (@code cacheKey}
     *
     * @param cacheKey
     *         specifies the I18NKey & Locale
     * @param value
     */
    @Override
    public void write(@Nonnull PatternCacheKey cacheKey, @Nonnull String value) {
        checkNotNull(cacheKey);
        checkNotNull(value);
        store.put(cacheKey, value);
    }

    /**
     * Delete the {@code value} entry from persistence for the I18NKey & Locale provided by (@code cacheKey}
     *
     * @param cacheKey
     *         specifies the I18NKey & Locale to identify the entry to delete
     *
     * @return the previous value for the entry, or null if there was no previous value
     */
    @Nullable
    @Override
    public String deleteValue(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        return store.remove(cacheKey);
    }

    /**
     * Gets a value from persistence for the I18NKey & Locale provided by (@code cacheKey}
     *
     * @param cacheKey
     *         specifies the hierarchy, rank and OptionKey for the entry to delete
     *
     * @return an Optional wrapped value if there is one or an Optional.empty() if not
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     */
    @Nonnull
    @Override
    public Optional<String> getValue(@Nonnull PatternCacheKey cacheKey) {
        checkNotNull(cacheKey);
        String v = store.get(cacheKey);
        if (v == null) {
            return Optional.empty();
        } else {
            return Optional.of(v);
        }
    }

    /**
     * Returns the connection url
     *
     * @return the connection url
     */
    @Override
    public String connectionUrl() {
        return "in-memory";
    }
}
