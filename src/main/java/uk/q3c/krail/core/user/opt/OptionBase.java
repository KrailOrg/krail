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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static uk.q3c.krail.core.user.profile.RankOption.*;

/**
 * Base implementation for {@link Option}. Uses {@link OptionCache}, which is configured to use some form of
 * persistence. All calls reference an implementation of {@link UserHierarchy}, either directly as a method
 * parameter, or by defaulting to {@link #hierarchy}.  The get() and set() default to using the highest rank
 * from {@link UserHierarchy}.  For getting or setting values at a specific hierarchyRank use the getSpecific() and
 * setSpecific() methods. The delete() method is always specific
 * <p>
 * To create a hierarchy specific implementation, simply sub-class with the alternative hierarchy injected into it.
 * <p>
 * Created by David Sowerby on 03/12/14.
 */

public abstract class OptionBase implements Option {


    private static Logger log = LoggerFactory.getLogger(OptionBase.class);
    private UserHierarchy hierarchy;
    private OptionCache optionCache;

    protected OptionBase(@Nonnull OptionCache optionCache, @Nonnull UserHierarchy hierarchy) {
        this.hierarchy = hierarchy;
        this.optionCache = optionCache;
    }

    @Override
    public UserHierarchy getHierarchy() {
        return hierarchy;
    }

    @Override
    public <T> void set(T value, @Nonnull OptionKey<T> optionKey) {
        set(value, hierarchy, 0, optionKey);
    }

    @Override
    public synchronized <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);
        optionCache.write(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, optionKey), Optional.of(value));
    }

    @Override
    public <T> void set(T value, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey<T> optionKey) {
        set(value, hierarchy, 0, optionKey);
    }

    @Override
    public <T> void set(T value, int hierarchyRank, @Nonnull OptionKey<T> optionKey) {
        set(value, hierarchy, hierarchyRank, optionKey);
    }

    @Nonnull
    @Override
    public synchronized <T> T get(@Nonnull OptionKey<T> optionKey) {
        return get(hierarchy, optionKey);
    }


    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull UserHierarchy hierarchy, @Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        T defaultValue = optionKey.getDefaultValue();
        Optional<T> optionalValue = optionCache.get(Optional.of(defaultValue), new OptionCacheKey(hierarchy, HIGHEST_RANK, 0, optionKey));
        if (optionalValue == null) {
            return defaultValue;
        }
        if (optionalValue.isPresent()) {
            return optionalValue.get();
        } else {
            return defaultValue;
        }
    }


    @Nonnull
    @Override
    public synchronized <T> T getLowestRanked(@Nonnull OptionKey<T> optionKey) {
        return getLowestRanked(hierarchy, optionKey);
    }

    @Nonnull
    @Override
    public synchronized <T> T getLowestRanked(@Nonnull UserHierarchy hierarchy, @Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        Optional<T> optionalValue = optionCache.get(Optional.of(optionKey.getDefaultValue()), new OptionCacheKey(hierarchy, LOWEST_RANK, 0, optionKey));
        if (optionalValue == null) {
            return optionKey.getDefaultValue();
        }
        if (optionalValue.isPresent()) {
            return optionalValue.get();
        } else {
            return optionKey.getDefaultValue();
        }
    }


    @Nonnull
    @Override
    public synchronized <T> T getSpecificRanked(int hierarchyRank, @Nonnull OptionKey<T> optionKey) {

        return getSpecificRanked(hierarchy, hierarchyRank, optionKey);
    }

    @Nonnull
    @Override
    public synchronized <T> T getSpecificRanked(@Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        T defaultValue = optionKey.getDefaultValue();
        Optional<T> optionalValue = optionCache.get(Optional.of(defaultValue), new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, optionKey));

        if (optionalValue == null) {
            return defaultValue;
        }
        if (optionalValue.isPresent()) {
            return optionalValue.get();
        } else {
            return defaultValue;
        }
    }


    @Override
    @Nullable
    public <T> T delete(@Nonnull UserHierarchy hierarchy, int hierarchyRank, OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);

        return (T) optionCache.delete(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, optionKey));
    }

}
