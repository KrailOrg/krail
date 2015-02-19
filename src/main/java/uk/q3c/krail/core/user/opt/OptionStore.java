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


import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stores and loads option values from a (usually) persistent store.  A simple, in memory, version is provided
 * primarily for testing.
 * <p>
 * <p>
 * Created by David Sowerby on 04/12/14.
 */
public interface OptionStore {



    /**
     * Flushes the cache (if there is one - that will depend on the implementation)
     */
    void flushCache();


    <T> void setValue(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyLevel, @Nonnull OptionKey
            optionKey);

    @Nonnull
    <T> T getValue(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, int hierarchyLevel, @Nonnull OptionKey
            optionKey);

    @Nullable
    Object deleteValue(@Nonnull UserHierarchy hierarchy, int hierarchyLevel, @Nonnull OptionKey optionKey);
}
