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

import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a unique identifier for cached {@link Option}
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
@Immutable
public class OptionCacheKey {


    private final String userId;
    private final UserHierarchy hierarchy;
    private final String requestedRankName;
    private final OptionKey optionKey;
    private final RankOption rankOption;

    /**
     * Calls {@link OptionCacheKey#OptionCacheKey(UserHierarchy, RankOption, int, OptionKey)} with the requested rank assumed to be 0 (highest)
     */
    public OptionCacheKey(@Nonnull UserHierarchy hierarchy, @Nonnull RankOption rankOption, @Nonnull OptionKey optionKey) {

        this(hierarchy, rankOption, 0, optionKey);

    }

    /**
     * @param rankOption
     *         determines whether this key represents the lowest or highest in a hierarchy, or a specific rank
     * @param hierarchy
     *         the hierarchy to use
     * @param requestedRank
     *         which rank to look for - only required if {@code rankOption} is {@link RankOption#SPECIFIC_RANK}, for
     *         {@link RankOption#HIGHEST_RANK} or {@link RankOption#LOWEST_RANK} use the alternative constructor:
     *         {@link OptionCacheKey#OptionCacheKey(UserHierarchy, RankOption, OptionKey)}
     * @param optionKey
     *         an object representing a unique key for the option within its context
     */
    public OptionCacheKey(@Nonnull UserHierarchy hierarchy, @Nonnull RankOption rankOption, int requestedRank, @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(rankOption);
        checkNotNull(optionKey);
        checkArgument(requestedRank >= 0);
        this.rankOption = rankOption;
        this.hierarchy = hierarchy;
        this.requestedRankName = requestedRankName(requestedRank);
        this.optionKey = optionKey;
        this.userId = hierarchy.highestRankName();
    }

    @Nonnull
    private String requestedRankName(int requestedRank) {
        String rankName = null;
        switch (rankOption) {
            case HIGHEST_RANK:
                rankName = hierarchy.highestRankName();
                break;
            case LOWEST_RANK:
                rankName = hierarchy.lowestRankName();
                break;
            case SPECIFIC_RANK:
                rankName = hierarchy.rankName(requestedRank);
                break;
        }

        if (rankName == null) {
            throw new NullPointerException("rank name cannot be set to null");
        }
        return rankName;
    }

    /**
     * copy constructor which changes the RankOption to {@code rankOption}
     *
     * @param cacheKey
     *         the key to copy
     * @param rankOption
     *         the new rankOption to use
     */
    public OptionCacheKey(@Nonnull OptionCacheKey cacheKey, @Nonnull RankOption rankOption) {
        checkNotNull(cacheKey);
        checkNotNull(rankOption);
        this.rankOption = rankOption;
        this.hierarchy = cacheKey.getHierarchy();
        this.requestedRankName = cacheKey.getRequestedRankName();
        this.optionKey = cacheKey.getOptionKey();
        this.userId = cacheKey.getUserId();
    }

    public UserHierarchy getHierarchy() {
        return hierarchy;
    }

    public OptionKey getOptionKey() {
        return optionKey;
    }

    public String getRequestedRankName() {
        return requestedRankName;
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Constructs a copy with {@link #requestedRankName} changed to {@code rankName}.  The {@link #rankOption} may be forced to {@link
     * RankOption#SPECIFIC_RANK}
     * with {@code makeSpecific}
     *
     * @param cacheKey
     *         the key to copy
     * @param rankName
     *         the new rank name
     * @param rankOption
     *         the #rankOption to set
     */
    public OptionCacheKey(@Nonnull OptionCacheKey cacheKey, @Nonnull String rankName, RankOption rankOption) {
        checkNotNull(cacheKey);
        checkNotNull(rankName);
        this.requestedRankName = rankName;
        this.optionKey = cacheKey.getOptionKey();
        this.hierarchy = cacheKey.getHierarchy();
        this.rankOption = rankOption;
        this.userId = cacheKey.getUserId();
    }

    public RankOption getRankOption() {
        return rankOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptionCacheKey)) {
            return false;
        }

        OptionCacheKey that = (OptionCacheKey) o;

        if (!hierarchy.equals(that.hierarchy)) {
            return false;
        }
        if (!optionKey.equals(that.optionKey)) {
            return false;
        }
        if (rankOption != that.rankOption) {
            return false;
        }
        if (userId != that.userId) {
            return false;
        }

        //if a SPECIFIC, we need to compare the rank name as well
        if (rankOption.equals(RankOption.SPECIFIC_RANK)) {

            return requestedRankName.equals(that.requestedRankName);
        } else {
            return true;
        }

    }

    @Override
    public int hashCode() {
        int result = hierarchy.hashCode();

        result = 31 * result + optionKey.hashCode();
        result = 31 * result + rankOption.hashCode();
        result = 31 * result + userId.hashCode();

        // if a SPECIFIC, include the rank name
        if (rankOption.equals(RankOption.SPECIFIC_RANK)) {
            result = 31 * result + requestedRankName.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "OptionCacheKey{" +
                "hierarchy=" + hierarchy +
                ", requestedRankName='" + requestedRankName + '\'' +
                ", optionKey=" + optionKey +
                ", rankOption=" + rankOption +
                '}';
    }
}
