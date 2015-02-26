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

import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.opt.cache.OptionKeyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by David Sowerby on 20/02/15.
 */
public interface OptionDao {

    /**
     * Returns a map of rankName - values for the {@code cacheKey} - ordered the same way as {@code rankNames}
     * (generally the order is the hierarchy rank order, but is determined by the caller).   Implementations should
     * check that the  {@code cacheKey} is valid for this call (it must be set up for a highest rank or lowest rank) and
     * throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *         identifies the hierarchy and option key
     * @param rankNames
     *         The names of the ranks values are required for.  These would generally be as they apply to the current
     *         user, but the selection of rank names is determined by the caller
     *
     * @return a list of values for the user hierarchy and option key provided in the cacheKey, ordered by hierarchy
     * rank.  An empty list if none found
     *
     * @throws OptionKeyException
     *         if the cacheKey is not valid for this action
     */
    @Nonnull
    LinkedHashMap<String, Object> getValuesForRanks(@Nonnull OptionCacheKey cacheKey, List<String> rankNames);

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
     */
    void write(@Nonnull OptionCacheKey cacheKey, @Nonnull Object value);


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
    Object delete(@Nonnull OptionCacheKey cacheKey);

    /**
     * Gets a value from persistence for the hierarchy, rank and OptionKey specified by the {@code cacheKey}.
     * Implementations should check that the {@code cacheKey} is valid for a single value get (it must be set up for
     * a specific rank) and throw a {@link OptionKeyException} if it is not valid
     *
     * @param cacheKey
     *
     * @return an Optional wrapped value if there is one or an Optional.empty() if not
     */
    Optional<Object> get(@Nonnull OptionCacheKey cacheKey);
}
