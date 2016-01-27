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

package uk.q3c.krail.core.user.opt;

import uk.q3c.krail.core.persist.InMemoryContainer;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An entity representing an {@link Option} for use with the {@link InMemoryContainer}
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
public class OptionEntity {

    private String context;
    private String optionKey;
    private String rankName;
    private String userHierarchyName;
    private String value;

    public OptionEntity() {
    }

    public OptionEntity(@Nonnull OptionCacheKey<?> optionCacheKey, @Nonnull String value) {
        checkNotNull(optionCacheKey);
        checkNotNull(value);
        OptionKey<?> optKey = optionCacheKey.getOptionKey();
        userHierarchyName = optionCacheKey.getHierarchy()
                                          .persistenceName();
        rankName = optionCacheKey.getRequestedRankName();
        optionKey = optKey.compositeKey();
        context = optKey.getContext()
                        .getName();
        this.value = value;

    }

    public String getContext() {
        return context;
    }

    public String getUserHierarchyName() {
        return userHierarchyName;
    }

    public void setUserHierarchyName(String userHierarchyName) {
        this.userHierarchyName = userHierarchyName;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    public void setValue(@Nonnull String value) {
        checkNotNull(value);
        this.value = value;
    }

}
