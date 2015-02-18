/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.user.opt;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A volatile, in-memory store for user options
 */
@Singleton
@ThreadSafe
public class InMemoryOptionStore implements OptionStore {

    private Map<String, Map<String, Map<String, Object>>> map = new ConcurrentHashMap<>();


    @Inject
    protected InMemoryOptionStore() {
    }


    @Override
    public synchronized <T> void setValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey
            optionKey, @Nonnull T value) {
        checkNotNull(hierarchyName);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        checkNotNull(value);


        // create a map for the hierarchy if it is not there
        if (!map.containsKey(hierarchyName)) {
            map.put(hierarchyName, new ConcurrentHashMap<>());
        }

        Map<String, Map<String, Object>> optionMap = map.get(hierarchyName);

        if (optionMap == null) {
            optionMap = new ConcurrentHashMap<>();
            map.put(hierarchyName, optionMap);
        }

        String compositeKey = optionKey.compositeKey();

        //if there is no levelValueMap for this option, create it
        if (!optionMap.containsKey(compositeKey)) {
            optionMap.put(compositeKey, new ConcurrentHashMap<>());
        }

        Map<String, Object> levelValueMap = optionMap.get(compositeKey);

        //put the value
        levelValueMap.put(rankName, value);
    }


    @Override
    @Nullable
    public synchronized Object getValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey
            optionKey) {
        checkNotNull(hierarchyName);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        Map<String, Map<String, Object>> optionMap = map.get(hierarchyName);
        if (optionMap == null) {
            return null;
        }

        Map<String, Object> valueMap = optionMap.get(optionKey.compositeKey());
        return valueMap.get(rankName);

    }


    @Nonnull
    @Override
    public Map<String, Object> valueMapForOptionKey(@Nonnull String hierarchyName, @Nonnull List<String> rankNames,
                                                    @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchyName);
        checkNotNull(rankNames);
        checkNotNull(optionKey);
        Map<String, Map<String, Object>> optionMap = map.get(hierarchyName);
        if (optionMap == null) {
            return new HashMap<>();
        }
        Map<String, Object> valueMap = optionMap.get(optionKey.compositeKey());


        Map<String, Object> resultMap = new HashMap<>();
        if (valueMap == null) {
            return resultMap;
        }

        rankNames.forEach(rankName -> {
            if (valueMap.containsKey(rankName)) {
                resultMap.put(rankName, valueMap.get(rankName));
            }
        });

        return resultMap;

    }

    @Nullable
    @Override
    public Object deleteValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchyName);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        Map<String, Map<String, Object>> optionMap = map.get(hierarchyName);
        if (optionMap == null) {
            return null;
        }
        Map<String, Object> valueMap = optionMap.get(optionKey.compositeKey());
        if (valueMap == null) {
            return null;
        }
        return valueMap.remove(rankName);

    }


}
