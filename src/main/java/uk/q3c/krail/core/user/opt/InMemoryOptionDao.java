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

package uk.q3c.krail.core.user.opt;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import uk.q3c.krail.core.user.opt.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data Access Object for {@link DefaultInMemoryOptionStore}
 * <br>
 * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDao} are wrapped in Optional.
 * <br>
 * Created by David Sowerby on 20/02/15.
 */
public class InMemoryOptionDao implements OptionDao {

    private InMemoryOptionStore optionStore;

    @Inject
    public InMemoryOptionDao(InMemoryOptionStore optionStore) {
        this.optionStore = optionStore;
    }

    @Override
    public <V> void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Optional<V> value) {
        checkRankOption(cacheKey, RankOption.SPECIFIC_RANK);
        checkNotNull(value);
        String hierarchyName = cacheKey.getHierarchy()
                                       .persistenceName();

        String rankName = cacheKey.getRequestedRankName();
        OptionKey optionKey = cacheKey.getOptionKey();
        optionStore.setValue(hierarchyName, rankName, optionKey, value);
    }


    @Override
    public Optional<?> deleteValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.SPECIFIC_RANK);
        String hierarchyName = cacheKey.getHierarchy()
                                       .persistenceName();

        String rankName = cacheKey.getRequestedRankName();
        OptionKey optionKey = cacheKey.getOptionKey();
        Optional<?> prevValue = optionStore.deleteValue(hierarchyName, rankName, optionKey);
        return prevValue;


    }


    @Nonnull
    @Override
    public Optional<?> getValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.SPECIFIC_RANK);
        String hierarchyName = cacheKey.getHierarchy()
                                       .persistenceName();

        String rankName = cacheKey.getRequestedRankName();
        OptionKey optionKey = cacheKey.getOptionKey();
        return optionStore.getValue(hierarchyName, rankName, optionKey);

    }


    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Optional<?> getHighestRankedValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.HIGHEST_RANK);
        ImmutableList<String> ranks = cacheKey.getHierarchy()
                                              .ranksForCurrentUser();
        LinkedHashMap<String, Optional<?>> values = getValuesForRanks(cacheKey, ranks);
        for (String rank : ranks) {
            if (values.containsKey(rank)) {
                return values.get(rank);
            }
        }
        return Optional.empty();
    }

    @Nonnull
    protected LinkedHashMap<String, Optional<?>> getValuesForRanks(@Nonnull OptionCacheKey cacheKey, List<String> rankNames) {


        Map<String, Optional<?>> valueMapForOptionKey = optionStore.valueMapForOptionKey(cacheKey.getHierarchy()
                                                                                            .persistenceName(), rankNames, cacheKey.getOptionKey());


        LinkedHashMap<String, Optional<?>> resultMap = new LinkedHashMap<>();

        //shortcut to doing nothing
        if (valueMapForOptionKey.isEmpty()) {
            return resultMap;
        }

        //select the rank names-value if it is in the user's rank names

        //not so easy to use Stream API, despite the IDE prompt, and not worth the effort
        // main challenge is that order here is important, and Stream forEach does not guarantee processing order
        for (String rankName : rankNames) {
            if (valueMapForOptionKey.containsKey(rankName)) {
                resultMap.put(rankName, valueMapForOptionKey.get(rankName));
            }
        }
        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Optional<?> getLowestRankedValue(@Nonnull OptionCacheKey cacheKey) {
        checkRankOption(cacheKey, RankOption.LOWEST_RANK);
        ImmutableList<String> ranks = cacheKey.getHierarchy()
                                              .ranksForCurrentUser();
        LinkedHashMap<String, Optional<?>> values = getValuesForRanks(cacheKey, ranks);
        ImmutableList<String> reversedRanks = ranks.reverse();
        for (String rank : reversedRanks) {
            if (values.containsKey(rank)) {
                return values.get(rank);
            }
        }
        return Optional.empty();
    }


    @Override
    public String connectionUrl() {
        return "In Memory Cache";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int clear() {
        int count = optionStore.size();
        optionStore.clear();
        return count - optionStore.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return optionStore.size();
    }
}
