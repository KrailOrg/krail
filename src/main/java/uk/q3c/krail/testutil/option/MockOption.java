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

package uk.q3c.krail.testutil.option;

import com.google.inject.Inject;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.option.OptionKey;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Note that this wil lnto necessarily convert data types correctly
 *
 * Created by David Sowerby on 27/02/15.
 */
public class MockOption implements Option {

    private UserHierarchy hierarchy = mock(UserHierarchy.class);
    private Map<OptionKey, Optional<Object>> optionMap;

    @Inject
    protected MockOption() {
        optionMap = new HashMap<>();
        when(hierarchy.lowestRank()).thenReturn(5);
    }

    @Override
    public <T> void set(@Nonnull OptionKey<T> optionKey, T value) {
        set(optionKey, 0, value);
    }

    @Override
    public synchronized <T> void set(@Nonnull OptionKey<T> optionKey, int hierarchyRank, @Nonnull T value) {
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);
        optionMap.put(optionKey, Optional.of(value));
    }

    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull OptionKey<T> optionKey) {
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        T defaultValue = optionKey.getDefaultValue();
        Optional<Object> optionalValue = optionMap.get(optionKey);
        if (optionalValue == null) {
            return defaultValue;
        }
        if (optionalValue.isPresent()) {
            return (T) optionalValue.get();
        } else {
            return defaultValue;
        }
    }


    @Nonnull
    @Override
    public synchronized <T> T getLowestRanked(@Nonnull OptionKey<T> optionKey) {
        return get(optionKey);
    }


    @Nonnull
    @Override
    public synchronized <T> T getSpecificRanked(int hierarchyRank, @Nonnull OptionKey<T> optionKey) {
        return get(optionKey);
    }

    @Override
    public UserHierarchy getHierarchy() {
        return hierarchy;
    }

    @Override
    @Nullable
    public <T> T delete(@Nonnull OptionKey<T> optionKey, int hierarchyRank) {
        checkNotNull(hierarchy);
        checkArgument(hierarchyRank >= 0);
        checkNotNull(optionKey);

        return (T) optionMap.remove(optionKey);
    }



}

