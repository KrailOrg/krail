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

import uk.q3c.krail.core.user.opt.OptionDao;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by David Sowerby on 27/06/15.
 */
public class MockOptionDao implements OptionDao {


    private int clearCount;
    private String connectionUrl;
    private long count;
    private Optional<?> deleteValue;
    private Map<OptionCacheKey, Optional<?>> getValues = new HashMap<>();
    private Map<OptionCacheKey, Optional<?>> highestRanked = new HashMap<>();
    private Map<OptionCacheKey, Optional<?>> lowestRanked = new HashMap<>();

    @Override
    public <V> Object write(@Nonnull OptionCacheKey cacheKey, @Nonnull Optional<V> value) {
        return null;
    }

    @Nonnull
    @Override
    public Optional<?> deleteValue(@Nonnull OptionCacheKey cacheKey) {
        return deleteValue;
    }

    @Nonnull
    @Override
    public Optional<?> getValue(@Nonnull OptionCacheKey cacheKey) {
        return getValues.get(cacheKey);
    }

    @Nonnull
    @Override
    public Optional<?> getHighestRankedValue(@Nonnull OptionCacheKey cacheKey) {
        return highestRanked.get(cacheKey);
    }

    @Nonnull
    @Override
    public Optional<?> getLowestRankedValue(@Nonnull OptionCacheKey cacheKey) {
        return lowestRanked.get(cacheKey);
    }

    @Override
    public String connectionUrl() {
        return connectionUrl;
    }

    @Override
    public int clear() {
        connectionUrl = null;
        count = 0;
        deleteValue = null;
        highestRanked = new HashMap<>();
        lowestRanked = new HashMap<>();
        getValues = new HashMap<>();
        int c = clearCount;
        clearCount = 0;
        return c;
    }

    @Override
    public long count() {
        return count;
    }

    public void setHighestRankedValue(OptionCacheKey cacheKey, Optional<?> value) {
        highestRanked.put(cacheKey, value);
    }

    public void setLowestRankedValue(OptionCacheKey cacheKey, Optional<?> value) {
        lowestRanked.put(cacheKey, value);
    }

    public void setValue(OptionCacheKey cacheKey, Optional<?> value) {
        getValues.put(cacheKey, value);
    }
}
