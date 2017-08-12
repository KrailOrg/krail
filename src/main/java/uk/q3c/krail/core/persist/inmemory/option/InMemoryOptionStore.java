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


import uk.q3c.krail.core.persist.common.option.OptionEntity;

import java.util.List;
import java.util.Optional;

/**
 * Stores and loads option values from a (usually) persistent store.  A simple, in memory, version is provided
 * primarily for testing.
 * <p>
 * Created by David Sowerby on 04/12/14.
 */
public interface InMemoryOptionStore {

    /**
     * Gets the entity for {@code optionId}
     *
     * @param optionId the {@link OptionId} which identifies the Option
     * @return Optional wrapped {@link OptionEntity} if found, else Optional.empty()
     */

    Optional<OptionEntity> getEntity(OptionId optionId);

    /**
     * Delete the entry for {@code cacheKey}
     *
     * @param optionId the {@link OptionId} which identifies the Option
     * @return the previous value associated with {@code cacheKey}, or Optional.empty() if there was no mapping for key.
     */

    Optional<String> delete(OptionId optionId);


    /**
     * Some implementations may enable clearing the WHOLE option store.  Those that do not throw an {@link UnsupportedOperationException}
     */
    void clear();

    /**
     * The number of entries in the store
     */
    int size();

    /**
     * Returns the entire contents of the store as {@link OptionEntity} instances
     *
     * @return the entire contents of the store as {@link OptionEntity} instances
     */

    List<OptionEntity> asEntities();

    /**
     * Adds (puts) an optionId and value to the store
     *
     * @param optionId the {@link OptionId} which identifies the Option
     * @param value    the value to store
     */
    void add(OptionId optionId, String value);

    /**
     * Gets the string value associated with optionId, or an empty Optional if none found
     *
     * @param optionId the {@link OptionId} which identifies the Option
     * @return the string value associated with optionId, or an empty Optional if none found
     */
    Optional<String> getValue(OptionId optionId);

}

