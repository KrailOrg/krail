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
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.DefaultUserHierarchy;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static uk.q3c.krail.core.user.profile.RankOption.*;

/**
 * Default implementation for {@link Option}, and uses {@link OptionCache}, which is configured to use some form of
 * persistence. All calls reference an implementation of {@link UserHierarchy}, either directly as a method
 * parameter, or by defaulting to {@link #defaultHierarchy}.  The get() and set() default to using the highest rank
 * from {@link UserHierarchy}.  For getting or setting values at a specific hierarchyRank use the getSpecific() and
 * setSpecific() methods. The delete() method is always specific
 * <p>
 * <p>
 * Created by David Sowerby on 03/12/14.
 */
public class DefaultOption implements Option {
    private static Logger log = LoggerFactory.getLogger(DefaultOption.class);
    private UserHierarchy defaultHierarchy;
    private OptionCache optionCache;

    @Inject
    protected DefaultOption(@Nonnull OptionCache optionCache, @Nonnull @DefaultUserHierarchy UserHierarchy
            defaultHierarchy) {
        this.defaultHierarchy = defaultHierarchy;
        this.optionCache = optionCache;
    }






    @Override
    public <T> void set(T value, @Nonnull OptionKey optionKey) {
        set(value, defaultHierarchy, 0, optionKey);
    }

    @Override
    public synchronized <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);
        optionCache.write(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, optionKey), Optional.of(value));
    }

    @Override
    public <T> void set(T value, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey optionKey) {
        set(value, hierarchy, 0, optionKey);
    }

    @Override
    public <T> void set(T value, int hierarchyRank, @Nonnull OptionKey optionKey) {
        set(value, defaultHierarchy, hierarchyRank, optionKey);
    }

    @Nonnull
    @Override
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull OptionKey optionKey) {
        return get(defaultValue, defaultHierarchy, optionKey);
    }


    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey optionKey) {
        checkNotNull(defaultValue);
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
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
    public synchronized <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull OptionKey optionKey) {
        return getLowestRanked(defaultValue, defaultHierarchy, optionKey);
    }

    @Nonnull
    @Override
    public synchronized <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey optionKey) {
        checkNotNull(defaultValue);
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        Optional<T> optionalValue = optionCache.get(Optional.of(defaultValue), new OptionCacheKey(hierarchy, LOWEST_RANK, 0, optionKey));
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
    public synchronized <T> T getSpecificRanked(@Nonnull T defaultValue, int hierarchyRank, @Nonnull OptionKey optionKey) {

        return getSpecificRanked(defaultValue, defaultHierarchy, hierarchyRank, optionKey);
    }
    @Nonnull
    @Override
    public synchronized <T> T getSpecificRanked(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey optionKey) {
        checkNotNull(defaultValue);
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
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
    public Object delete(@Nonnull UserHierarchy hierarchy, int hierarchyRank, OptionKey optionKey) {
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);

        return optionCache.delete(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, optionKey));
    }


}
