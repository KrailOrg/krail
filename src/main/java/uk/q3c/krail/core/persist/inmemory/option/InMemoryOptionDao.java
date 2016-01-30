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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import uk.q3c.krail.core.data.OptionElementConverter;
import uk.q3c.krail.core.data.OptionListConverter;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.option.OptionException;
import uk.q3c.krail.core.option.OptionList;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheLoader;
import uk.q3c.krail.core.persist.cache.option.OptionCache;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.persist.common.option.OptionDao;
import uk.q3c.krail.core.user.profile.RankOption;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
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
    private OptionElementConverter optionElementConverter;

    @Inject
    public InMemoryOptionDao(InMemoryOptionStore optionStore, OptionElementConverter optionElementConverter) {
        this.optionStore = optionStore;
        this.optionElementConverter = optionElementConverter;
    }

    /**
     * Write the key value pair
     *
     * @param cacheKey specifies the hierarchy, rank and OptionKey to write to
     * @param value    the value to write
     * @param <V>      the value type
     */
    @Override
    public <V> void write(@Nonnull OptionCacheKey<V> cacheKey, @Nonnull Optional<V> value) {
        checkRankOption(cacheKey, RankOption.SPECIFIC_RANK);
        checkArgument(value.isPresent(), "Value cannot be empty");
        checkNotNull(value);
        String stringValue = optionElementConverter.convertValueToString(value.get());
        optionStore.add(new OptionId(cacheKey), stringValue);
    }

    @Nonnull
    @Override
    public <V> Optional<String> deleteValue(@Nonnull OptionCacheKey<V> cacheKey) {
        checkRankOption(cacheKey, RankOption.SPECIFIC_RANK);
        return optionStore.delete(new OptionId(cacheKey));
    }


    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <V> Optional<V> getValue(@Nonnull OptionCacheKey<V> cacheKey) {
        Optional<String> optionalStringValue;

        switch (cacheKey.getRankOption()) {
            case HIGHEST_RANK:
                optionalStringValue = getRankedValue(cacheKey, false);
                break;
            case LOWEST_RANK:
                optionalStringValue = getRankedValue(cacheKey, true);
                break;
            case SPECIFIC_RANK:
                optionalStringValue = getStringValue(cacheKey);
                break;
            default:
                throw new OptionException("Unrecognised rankOption");
        }

        if (optionalStringValue.isPresent()) {
            V defaultValue = cacheKey.getOptionKey()
                                     .getDefaultValue();
            if (defaultValue instanceof OptionList) {
                OptionList convertedValue = new OptionListConverter(optionElementConverter).convertToModel((OptionList) defaultValue,
                        optionalStringValue.get());
                return Optional.of((V) convertedValue);
            } else {
                Class<V> elementClass = (Class<V>) defaultValue.getClass();
                return Optional.of(optionElementConverter.convertStringToValue(elementClass, optionalStringValue.get()));
            }
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> getStringValue(@Nonnull OptionCacheKey<?> cacheKey) {
        return optionStore.getValue(new OptionId(cacheKey));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    protected <V> Optional<String> getRankedValue(@Nonnull OptionCacheKey<V> cacheKey, boolean lowest) {
        ImmutableList<String> ranks = cacheKey.getHierarchy()
                                              .ranksForCurrentUser();
        ImmutableList<String> ranksToUse = (lowest) ? ranks.reverse() : ranks;
        for (String rank : ranksToUse) {
            OptionCacheKey<V> specificKey = new OptionCacheKey<>(cacheKey, rank, RankOption.SPECIFIC_RANK);
            Optional<String> stringValue = getStringValue(specificKey);
            if (stringValue.isPresent()) return stringValue;
        }
        return Optional.empty();
    }


    @Override
    public String connectionUrl() {
        return "In Memory Store";
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
