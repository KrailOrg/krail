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

import uk.q3c.krail.core.user.opt.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.opt.cache.OptionKeyException;
import uk.q3c.krail.core.user.profile.RankOption;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Provides data access to persistence for {@link OptionDao}
 * <br>
 * * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDao} are wrapped in Optional.
 * <p>
 * <p>
 * Created by David Sowerby on 20/02/15.
 */
public interface OptionDao {

    default void checkRankOption(OptionCacheKey<?> cacheKey, RankOption expected) {
        if (cacheKey.getRankOption() != expected) {
            throw new OptionKeyException("OptionCacheKey should have RankOption of: " + expected);
        }
    }

    /**
     * Write {@code value} to persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the  {@code cacheKey} is valid for a write (it must be set up for a specific
     * rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey specifies the hierarchy, rank and OptionKey to write to
     * @param value    the value to write
     * @param <V>      the value type
     * @return the saved entity
     * @throws OptionKeyException if the cacheKey is not valid for this action
     */
    <V> Object write(@Nonnull OptionCacheKey<V> cacheKey, @Nonnull Optional<V> value);


    /**
     * Delete the {@code value} entry from persistence for the hierarchy, rank and OptionKey specified by the {@code
     * cacheKey}. Implementations should check that the {@code cacheKey} is valid for a delete (it must be set up for
     * a specific rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey specifies the hierarchy, rank and OptionKey for the entry to delete
     * @return the previous value for the entry, or Optional.empty() if there was no previous value
     * @throws OptionKeyException if the cacheKey is not valid for this action
     */
    @Nonnull
    <V> Optional<?> deleteValue(@Nonnull OptionCacheKey<V> cacheKey);


    /**
     * Gets a value from persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the {@code cacheKey} is valid for a single value get (it must be set up for
     * a specific rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey specifies the hierarchy, rank and OptionKey for the entry to delete
     * @return an Optional wrapped value if there is one or an Optional.empty() if not
     * @throws OptionKeyException if the cacheKey is not valid for this action
     */
    @Nonnull
    <V> Optional<?> getValue(@Nonnull OptionCacheKey<V> cacheKey);


    /**
     * Returns the highest ranked value available for the {@code cacheKey}
     *
     * @param cacheKey the key to look for
     * @return the highest ranked value available for the {@code cacheKey}, or Optional.empty() if none found
     * @throws OptionKeyException if cacheKey {@link RankOption} is not equal to {@link RankOption#HIGHEST_RANK}
     */
    @Nonnull
    <V> Optional<?> getHighestRankedValue(@Nonnull OptionCacheKey<V> cacheKey);

    /**
     * Returns the lowest ranked value available for the {@code cacheKey}
     *
     * @param cacheKey they key to look for
     * @return the lowest ranked value available for the {@code cacheKey}
     * @throws OptionKeyException if cacheKey {@link RankOption} is not equal to {@link RankOption#LOWEST_RANK}
     */
    @Nonnull
    <V> Optional<?> getLowestRankedValue(@Nonnull OptionCacheKey<V> cacheKey);


    /**
     * Returns the connection url
     *
     * @return the connection url
     */
    String connectionUrl();

    /**
     * Clears all values from persistence
     *
     * @return the number of items deleted
     */
    int clear();

    /**
     * Returns the number of items present
     *
     * @return the number of items present
     */
    long count();
}
