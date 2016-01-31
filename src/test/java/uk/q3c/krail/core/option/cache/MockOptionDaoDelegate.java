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

import uk.q3c.krail.core.option.OptionException;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.persist.common.option.OptionDaoDelegate;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by David Sowerby on 27/06/15.
 */
public class MockOptionDaoDelegate implements OptionDaoDelegate {


    private int clearCount;
    private String connectionUrl;
    private long count;
    private Optional<?> deleteValue;
    private Map<OptionCacheKey, Optional<?>> getValues = new HashMap<>();
    private Map<OptionCacheKey, Optional<?>> highestRanked = new HashMap<>();
    private Map<OptionCacheKey, Optional<?>> lowestRanked = new HashMap<>();

    @Override
    public <V> void write(@Nonnull OptionCacheKey<V> cacheKey, @Nonnull String value) {
    }

    @Nonnull
    @Override
    public Optional<?> deleteValue(@Nonnull OptionCacheKey cacheKey) {
        return deleteValue;
    }

    @Nonnull
    @Override
    public Optional<?> getValue(@Nonnull OptionCacheKey cacheKey) {
        switch (cacheKey.getRankOption()) {
            case HIGHEST_RANK:
                return highestRanked.get(cacheKey);
            case LOWEST_RANK:
                return lowestRanked.get(cacheKey);
            case SPECIFIC_RANK:
                return
                        getValues.get(cacheKey);
            default:
                throw new OptionException("Unrecognised rank");
        }

    }


    @Override
    public String connectionUrl() {
        return connectionUrl;
    }

    @Override
    public long clear() {
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
