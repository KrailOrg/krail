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

import com.google.common.base.Converter;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.opt.cache.OptionKeyException;
import uk.q3c.krail.core.user.profile.RankOption;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by David Sowerby on 20/02/15.
 */
public interface OptionDao {

    default void checkRankOption(OptionCacheKey cacheKey, RankOption expected) {
        if (!cacheKey.getRankOption()
                     .equals(expected)) {
            throw new OptionKeyException("OptionCacheKey should have RankOption of: " + expected);
        }
    }

    /**
     * Write {@code value} to persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the  {@code cacheKey} is valid for a write (it must be set up for a specific
     * rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *         specifies the hierarchy, rank and OptionKey to write to
     * @param value
     *         the value to write
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     * @see #write(OptionCacheKey, Converter, Object)
     */
    void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Object value);


    /**
     * Write method for use with implementations which convert all values to Strings for persistence.  The converter is used to make the transformation from
     * value to its String representation.
     * <p>
     * Write {@code value} to persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the  {@code cacheKey} is valid for a write (it must be set up for a specific
     * rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *         specifies the hierarchy, rank and OptionKey to write to
     * @param value
     *         the value to write
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     * @see #write(OptionCacheKey, Object)
     */
    <V> void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Converter<V, String> converter, @Nonnull V value);

    /**
     * Delete the {@code value} entry from persistence for the hierarchy, rank and OptionKey specified by the {@code
     * cacheKey}. Implementations should check that the {@code cacheKey} is valid for a delete (it must be set up for
     * a specific rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *         specifies the hierarchy, rank and OptionKey for the entry to delete
     *
     * @return the previous value for the entry, or null if there was no previous value
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     */
    @Nullable
    Object deleteValue(@Nonnull OptionCacheKey cacheKey);

    /**
     * Gets a value from persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the {@code cacheKey} is valid for a single value get (it must be set up for
     * a specific rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *         specifies the hierarchy, rank and OptionKey for the entry to delete
     *
     * @return an Optional wrapped value if there is one or an Optional.empty() if not
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     * @see #getValue(Converter, OptionCacheKey)
     */
    @Nonnull
    Optional<Object> getValue(@Nonnull OptionCacheKey cacheKey);


    /**
     * Gets a value from an implementation which stores all values as Strings.  The converter provides the conversion from String to type V
     * <p>
     * Gets a value from persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the {@code cacheKey} is valid for a single value get (it must be set up for
     * a specific rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *         specifies the hierarchy, rank and OptionKey for the entry to delete
     * @param <V>
     *         the data type to be returned
     *
     * @return an Optional wrapped value if there is one or an Optional.empty() if not
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     * @see #getValue(OptionCacheKey)
     */

    @Nonnull
    <V> Optional<V> getValue(@Nonnull Converter<String, V> converter, @Nonnull OptionCacheKey cacheKey);

    /**
     * Returns the highest ranked value available for the {@code cacheKey}
     *
     * @param cacheKey
     *         the key to look for
     *
     * @return the highest ranked value available for the {@code cacheKey}, or Optional.empty() if none found
     *
     * @throws OptionKeyException
     *         if cacheKey {@link RankOption} is not equal to {@link RankOption#HIGHEST_RANK}
     */
    @Nonnull
    Optional<Object> getHighestRankedValue(@Nonnull OptionCacheKey cacheKey);

    /**
     * Returns the lowest ranked value available for the {@code cacheKey}
     *
     * @param cacheKey
     *         they key to look for
     *
     * @return the lowest ranked value available for the {@code cacheKey}
     *
     * @throws OptionKeyException
     *         if cacheKey {@link RankOption} is not equal to {@link RankOption#LOWEST_RANK}
     */
    @Nonnull
    Optional<Object> getLowestRankedValue(@Nonnull OptionCacheKey cacheKey);


    String connectionUrl();
}
