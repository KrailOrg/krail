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


import uk.q3c.krail.core.user.profile.UserHierarchyException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores and loads option values from a (usually) persistent store.  A simple, in memory, version is provided
 * primarily for testing.
 * <p>
 * Created by David Sowerby on 04/12/14.
 */
public interface OptionStore {

    /**
     * Assign {@value} to the hierarchy, hierarchy rank name and {@link OptionKey} specified
     *
     * @param hierarchyName
     *         the persistent name of the hierarchy
     * @param rankName
     *         Name of the rank to have its value
     * @param optionKey
     *         Unique identifier for the option, in its context
     * @param value
     *         the value to assign
     * @param <T>
     *         the type of the value
     *
     * @throws UserHierarchyException
     *         if {@code cacheKey.hierarchyRank} is out of range
     */
    <T extends Optional<?>> void setValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey, @Nonnull
    T value);

    /**
     * Gets the {@value} for the hierarchy, hierarchy rank name and {@link OptionKey} specified
     *
     * @param hierarchyName
     *         the persistent name of the hierarchy
     * @param rankName
     *         Name of the rank to have its value
     * @param optionKey
     *         Unique identifier for the option, in its context
     *
     * @return the value if found, otherwise null
     */
    @Nonnull
    Optional<?> getValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey);

    /**
     * Delete the entry for the hierarchy, hierarchy rank name and {@link OptionKey} specified
     *
     * @param hierarchyName
     *         the persistent name of the hierarchy
     * @param rankName
     *         Name of the rank to have its value
     * @param optionKey
     *         Unique identifier for the option, in its context
     *
     * @return the previous value associated with {@code cacheKey}, or null if there was no mapping for key.
     */
    @Nonnull
    Optional<?> deleteValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey);


    /**
     * returns a map of values for a hierarchy,for the rank names and {@link OptionKey} specified.  Only ranks with
     * values are returned, so it is possible for an empty map to be returned.
     *
     * @param hierarchyName
     *         the persistent name of the hierarchy
     * @param rankNames
     *         list of names from the hierarchy that values are required for
     * @param optionKey
     *         Unique identifier for the option, in its context
     *
     * @return returns a map of values for a hierarchy, for a given {@link OptionKey}, an empty map if there are no
     * values assigned
     */
    @Nonnull
    Map<String, Optional<?>> valueMapForOptionKey(@Nonnull String hierarchyName, @Nonnull List<String> rankNames, @Nonnull
    OptionKey optionKey);

    /**
     * Some implementations may enable clearing the WHOLE option store.  Those that do not throw an {@link UnsupportedOperationException}
     */
    void clear();

    /**
     * The number of entries in the store
     */
    int size();
}

