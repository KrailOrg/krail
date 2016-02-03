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

package testutil;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.option.OptionKey;
import uk.q3c.krail.core.persist.cache.option.OptionCache;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static uk.q3c.krail.core.user.profile.RankOption.SPECIFIC_RANK;

/**
 * Created by David Sowerby on 27/02/15.
 */
public class MockOption implements Option {

    private static Logger log = LoggerFactory.getLogger(MockOption.class);
    private UserHierarchy hierarchy = new TestUserHierarchy();
    private OptionCache optionCache;
    private Map<OptionKey, Optional<Object>> optionMap;

    @Inject
    protected MockOption() {
        optionMap = new HashMap<>();
    }

    @Override
    public <T> void set(@Nonnull OptionKey<T> optionKey, T value) {
        set(optionKey, 0, value);
    }

    @Override
    public synchronized <T> void set(@Nonnull OptionKey<T> optionKey, int hierarchyRank, @Nonnull T value) {
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);
        optionMap.put(optionKey, Optional.of(value));
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        T defaultValue = optionKey.getDefaultValue();
        Optional<Object> optionalValue = optionMap.get(optionKey);
        if (optionalValue == null) {
            return defaultValue;
        }
        if (optionalValue.isPresent()) {
            return (T) optionalValue.get();
        } else {
            return defaultValue;
        }
    }


    @Nonnull
    @Override
    public synchronized <T> T getLowestRanked(@Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        T defaultValue = optionKey.getDefaultValue();
        Optional<Object> optionalValue = optionMap.get(optionKey);
        if (optionalValue == null) {
            return defaultValue;
        }
        if (optionalValue.isPresent()) {
            return (T) optionalValue.get();
        } else {
            return defaultValue;
        }
    }


    @Nonnull
    @Override
    public synchronized <T> T getSpecificRanked(int hierarchyRank, @Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        T defaultValue = optionKey.getDefaultValue();
        Optional<Object> optionalValue = optionMap.get(optionKey);
        if (optionalValue == null) {
            return defaultValue;
        }
        if (optionalValue.isPresent()) {
            return (T) optionalValue.get();
        } else {
            return defaultValue;
        }
    }

    @Override
    public UserHierarchy getHierarchy() {
        return hierarchy;
    }

    @Override
    @Nullable
    public <T> T delete(OptionKey<T> optionKey, int hierarchyRank) {
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);

        return (T) optionCache.delete(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, optionKey));
    }


    private static class TestUserHierarchy implements UserHierarchy {


        @SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
        @Nonnull
        @Override
        public ImmutableList<String> ranksForCurrentUser() {
            return null;
        }


        @Override
        public String displayName() {
            return null;
        }


        @Override
        public String rankName(int hierarchyRank) {
            return null;
        }

        @Override
        public String highestRankName() {
            return null;
        }

        @Override
        public String lowestRankName() {
            return null;
        }

        @Override
        public int lowestRank() {
            return 5;
        }
    }

}

