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
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.util.KrailCodeException;

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
    private Class<? extends OptionContext> context;
    private UserHierarchy defaultHierarchy;
    private OptionCache optionCache;

    @Inject
    protected DefaultOption(@Nonnull OptionCache optionCache, @Nonnull @DefaultUserHierarchy UserHierarchy
            defaultHierarchy) {
        this.defaultHierarchy = defaultHierarchy;
        this.optionCache = optionCache;
    }

    @Nonnull
    public Class<? extends OptionContext> getContext() {
        return context;
    }


    @Override
    public void init(@Nonnull OptionContext context) {
        checkNotNull(context);
        this.context = context.getClass();
    }

    @Override
    public void init(@Nonnull Class<? extends OptionContext> context) {
        checkNotNull(context);
        this.context = context;
    }


    @Override
    public <T> void set(T value, I18NKey key, String... qualifiers) {
        checkInit();
        optionCache.write(new OptionCacheKey(defaultHierarchy, SPECIFIC_RANK, 0, new OptionKey(context, key, qualifiers)), Optional.of(value));
    }

    private void checkInit() {
        if (context == null) {
            String msg = getClass().getSimpleName() + " must be initialised.  Typically this is done by calling " +
                    "option.init in the constructor of the class implementing OptionContext, for example:\n\n option" +
                    ".init(this)\n\n ";
            throw new KrailCodeException(msg);
        }
    }

    @Override
    public synchronized <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, @Nonnull I18NKey key,
                                     @Nullable String... qualifiers) {
        set(value, hierarchy, 0, context, key, qualifiers);
    }


    @Override
    public synchronized <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers) {
        checkInit();
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(context);
        checkNotNull(key);
        optionCache.write(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, new OptionKey(context, key,
                qualifiers)), Optional.of(value));
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull I18NKey key, @Nullable String... qualifiers) {
        return get(defaultValue, HIGHEST_RANK, defaultHierarchy, 0, context, key, qualifiers);
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull RankOption rankOption, @Nonnull UserHierarchy
            hierarchy, int hierarchyRank, @Nonnull Class<? extends
            OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers) {
        checkInit();
        checkNotNull(defaultValue);
        checkNotNull(hierarchy);
        checkNotNull(context);
        checkNotNull(key);
        Optional<T> optionalValue = optionCache.get(Optional.of(defaultValue), new OptionCacheKey(hierarchy,
                rankOption, hierarchyRank, new OptionKey(context, key, qualifiers)));
        if (optionalValue.isPresent()) {
            return optionalValue.get();
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull Class<? extends
            OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers) {
        return get(defaultValue, LOWEST_RANK, defaultHierarchy, 0, context, key, qualifiers);
    }

    @Override
    @Nonnull
    public synchronized <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull I18NKey key, @Nullable String...
            qualifiers) {
        return get(defaultValue, LOWEST_RANK, defaultHierarchy, 0, context, key, qualifiers);
    }

    @Override
    @Nonnull
    public synchronized <T> T getSpecificRank(@Nonnull T defaultValue, int hierarchyRank, @Nonnull I18NKey key,
                                              @Nullable String... qualifiers) {
        return get(defaultValue, SPECIFIC_RANK, defaultHierarchy, hierarchyRank, context, key, qualifiers);
    }

    @Override
    public <T> T get(@Nonnull T defaultValue, UserHierarchy hierarchy, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers) {
        return get(defaultValue, HIGHEST_RANK, hierarchy, 0, context, key, qualifiers);
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull I18NKey key,
                                  @Nullable String... qualifiers) {
        return get(defaultValue, HIGHEST_RANK, hierarchy, 0, context, key, qualifiers);
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers) {
        return get(defaultValue, HIGHEST_RANK, defaultHierarchy, 0, context, key, qualifiers);
    }


    @Override
    @Nullable
    public Object delete(@Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull Class<? extends
            OptionContext> context, @Nonnull I18NKey key, @Nullable String
            ... qualifiers) {
        checkInit();
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(context);
        checkNotNull(key);

        return optionCache.delete(new OptionCacheKey(hierarchy, SPECIFIC_RANK, hierarchyRank, new OptionKey(context,
                key, qualifiers)));
    }


}
