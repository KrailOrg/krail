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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.opt.cache.OptionKeyException;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data Access Object for {@link InMemoryOptionStore}
 * <p>
 * Created by David Sowerby on 20/02/15.
 */
public class InMemoryOptionDao implements OptionDao {

    private static Logger log = LoggerFactory.getLogger(InMemoryOptionDao.class);
    private InMemoryOptionStore optionStore;

    @Inject
    public InMemoryOptionDao(InMemoryOptionStore optionStore) {
        this.optionStore = optionStore;
    }


    @Override
    @Nonnull
    public LinkedHashMap<String, Object> getValuesForRanks(@Nonnull OptionCacheKey cacheKey, List<String> rankNames) {
        checkKey(cacheKey, false);


        Map<String, Object> valueMapForOptionKey = optionStore.valueMapForOptionKey(cacheKey.getHierarchy()
                                                                                            .persistenceName(),
                rankNames, cacheKey.getOptionKey());


        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

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

    private void checkKey(OptionCacheKey cacheKey, boolean isSpecific) {
        checkNotNull(cacheKey);

        switch (cacheKey.getRankOption()) {
            case HIGHEST_RANK:
            case LOWEST_RANK:
                if (isSpecific) {
                    throw new OptionKeyException("Cache key must NOT be set to SPECIFIC");
                }
                break;
            case SPECIFIC_RANK:
                if (!isSpecific) {
                    throw new OptionKeyException("Cache key MUST be set to SPECIFIC");
                }
        }

    }

    @Override
    public void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Object value) {
        checkKey(cacheKey, true);
        checkNotNull(value);
        String hierarchyName = cacheKey.getHierarchy()
                                       .persistenceName();

        String rankName = cacheKey.getRequestedRankName();
        OptionKey optionKey = cacheKey.getOptionKey();
        optionStore.setValue(hierarchyName, rankName, optionKey, value);
    }

    @Override
    public Object delete(@Nonnull OptionCacheKey cacheKey) {
        checkKey(cacheKey, true);
        String hierarchyName = cacheKey.getHierarchy()
                                       .persistenceName();

        String rankName = cacheKey.getRequestedRankName();
        OptionKey optionKey = cacheKey.getOptionKey();
        return optionStore.deleteValue(hierarchyName, rankName, optionKey);
    }

    @Override
    public Optional<Object> get(@Nonnull OptionCacheKey cacheKey) {
        checkKey(cacheKey, true);
        String hierarchyName = cacheKey.getHierarchy()
                                       .persistenceName();

        String rankName = cacheKey.getRequestedRankName();
        OptionKey optionKey = cacheKey.getOptionKey();
        Object result = optionStore.getValue(hierarchyName, rankName, optionKey);
        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }

    }
}
