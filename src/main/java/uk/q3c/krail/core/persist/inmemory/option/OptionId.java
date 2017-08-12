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

package uk.q3c.krail.core.persist.inmemory.option;

import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.option.OptionKey;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import java.io.Serializable;

import static com.google.common.base.Preconditions.*;

/**
 * Uniquely identifies an  {@link Option} associated with a UserHierarchy entry - in other words, this is the key for a key-value pair, and is the key for a
 * specific option at a specific user hierarchy rank.<br><br>
 * <p>
 * Note that {@link #context} is duplicated, as it is also part of the {@link OptionKey#compositeKey()}.  This is because the fields of this class are likely
 * to become fields in persistence, and including the context here enables a single query to bring back all the options for a context.<br><br>
 * <p>
 * <p>
 * <p>
 * Created by David Sowerby on 17 Jan 2016
 */
public class OptionId implements Serializable {


    private String context;
    private String optionKey;
    private String rankName;
    private String userHierarchyName;

    /**
     * For persistence
     */
    protected OptionId() {

    }

    /**
     * Create an OptionId from an {@link OptionCacheKey}.  {@code cacheKey} must be set to {@link RankOption#SPECIFIC_RANK}
     *
     * @param optionCacheKey the cahce key from which to draw the Id - must be set to {@link RankOption#SPECIFIC_RANK}
     */
    public OptionId(OptionCacheKey optionCacheKey) {
        checkArgument(optionCacheKey.getRankOption() == RankOption.SPECIFIC_RANK, "RankOption must be SPECIFIC");
        OptionKey optKey = optionCacheKey.getOptionKey();
        UserHierarchy hierarchy = optionCacheKey.getHierarchy();
        this.context = optKey.getContext()
                             .getName();
        this.userHierarchyName = hierarchy.persistenceName();
        this.rankName = optionCacheKey.getRequestedRankName();
        this.optionKey = optKey.compositeKey();
    }

    /**
     * Construct using rank index, as opposed to rank name
     *
     * @param userHierarchy the user hierarchy to be used
     * @param rank          index of the rank to identify - this is not checked, so must be within the range provided by the hierarchy
     * @param optionKey     the optionKey identifying the option
     */
    public OptionId(UserHierarchy userHierarchy, int rank, OptionKey optionKey) {
        this(userHierarchy, userHierarchy.rankName(rank), optionKey);
    }

    /**
     * Construct using rank name, as opposed to rank index
     *
     * @param userHierarchy the user hierarchy to be used
     * @param rankName      name of the rank to identify - this is not checked, so must be one provided by the hierarchy
     * @param optionKey     the optionKey identifying the option
     */
    public OptionId(UserHierarchy userHierarchy, String rankName, OptionKey optionKey) {
        checkNotNull(userHierarchy);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        this.context = optionKey.getContext()
                                .getName();
        this.userHierarchyName = userHierarchy.persistenceName();
        this.rankName = rankName;
        this.optionKey = optionKey.compositeKey();
    }

    public String getContext() {
        return context;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public String getRankName() {
        return rankName;
    }

    public String getUserHierarchyName() {
        return userHierarchyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionId optionId = (OptionId) o;

        if (context != null ? !context.equals(optionId.context) : optionId.context != null) return false;
        if (optionKey != null ? !optionKey.equals(optionId.optionKey) : optionId.optionKey != null) return false;
        if (rankName != null ? !rankName.equals(optionId.rankName) : optionId.rankName != null) return false;
        return userHierarchyName != null ? userHierarchyName.equals(optionId.userHierarchyName) : optionId.userHierarchyName == null;

    }

    @Override
    public int hashCode() {
        int result = context != null ? context.hashCode() : 0;
        result = 31 * result + (optionKey != null ? optionKey.hashCode() : 0);
        result = 31 * result + (rankName != null ? rankName.hashCode() : 0);
        return 31 * result + (userHierarchyName != null ? userHierarchyName.hashCode() : 0);
    }
}
