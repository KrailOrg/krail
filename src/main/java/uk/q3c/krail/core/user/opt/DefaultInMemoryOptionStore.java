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
import uk.q3c.krail.core.data.OptionStringConverter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A volatile, in-memory store for user options
 */
@Singleton
@ThreadSafe
public class DefaultInMemoryOptionStore implements InMemoryOptionStore {

    private Map<String, Map<String, Map<String, Optional<?>>>> map = new ConcurrentHashMap<>();
    private OptionStringConverter optionStringConverter;


    @Inject
    public DefaultInMemoryOptionStore(OptionStringConverter optionStringConverter) {
        this.optionStringConverter = optionStringConverter;
    }


    @Override
    public synchronized <T extends Optional<?>> void setValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey, @Nonnull
    T value) {
        checkNotNull(hierarchyName);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        checkNotNull(value);


        // create a map for the hierarchy if it is not there
        if (!map.containsKey(hierarchyName)) {
            map.put(hierarchyName, new ConcurrentHashMap<>());
        }

        Map<String, Map<String, Optional<?>>> optionMap = map.get(hierarchyName);

        if (optionMap == null) {
            optionMap = new ConcurrentHashMap<>();
            map.put(hierarchyName, optionMap);
        }

        String compositeKey = optionKey.compositeKey();

        //if there is no levelValueMap for this option, create it
        if (!optionMap.containsKey(compositeKey)) {
            optionMap.put(compositeKey, new ConcurrentHashMap<>());
        }

        Map<String, Optional<?>> levelValueMap = optionMap.get(compositeKey);

        //put the value
        levelValueMap.put(rankName, value);
    }


    @Override
    @Nonnull
    public synchronized Optional<?> getValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchyName);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        Map<String, Map<String, Optional<?>>> optionMap = map.get(hierarchyName);
        if (optionMap == null) {
            return Optional.empty();
        }

        Map<String, Optional<?>> valueMap = optionMap.get(optionKey.compositeKey());
        Optional<?> value = valueMap.get(rankName);
        if (value == null) {
            return Optional.empty();
        } else {
            return value;
        }

    }


    @Nonnull
    @Override
    public Map<String, Optional<?>> valueMapForOptionKey(@Nonnull String hierarchyName, @Nonnull List<String> rankNames, @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchyName);
        checkNotNull(rankNames);
        checkNotNull(optionKey);
        Map<String, Map<String, Optional<?>>> optionMap = map.get(hierarchyName);
        if (optionMap == null) {
            return new HashMap<>();
        }
        Map<String, Optional<?>> valueMap = optionMap.get(optionKey.compositeKey());


        Map<String, Optional<?>> resultMap = new HashMap<>();
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

    @Nonnull
    @Override
    public Optional<?> deleteValue(@Nonnull String hierarchyName, @Nonnull String rankName, @Nonnull OptionKey optionKey) {
        checkNotNull(hierarchyName);
        checkNotNull(rankName);
        checkNotNull(optionKey);
        Map<String, Map<String, Optional<?>>> optionMap = map.get(hierarchyName);
        if (optionMap == null) {
            return Optional.empty();
        }
        Map<String, Optional<?>> valueMap = optionMap.get(optionKey.compositeKey());
        if (valueMap == null) {
            return Optional.empty();
        }
        return valueMap.remove(rankName);

    }


    public void clear() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        int c = 0;
        for (Map.Entry<String, Map<String, Map<String, Optional<?>>>> entry : map.entrySet()) {
            c = c + entry.getValue()
                         .size();
        }
        return c;
    }

    @Override
    public List<OptionEntity> asEntities() {
        List<OptionEntity> list = new ArrayList<>();
        for (Map.Entry<String, Map<String, Map<String, Optional<?>>>> hierarchyEntry : map.entrySet()) {
            String hierarchyName = hierarchyEntry.getKey();
            for (Map.Entry<String, Map<String, Optional<?>>> rankEntry : hierarchyEntry.getValue()
                                                                                       .entrySet()) {
                String rankName = rankEntry.getKey();
                for (Map.Entry<String, Optional<?>> optionEntry : rankEntry.getValue()
                                                                           .entrySet()) {
                    OptionEntity entity = new OptionEntity();
                    entity.setOptionKey(optionEntry.getKey());
                    entity.setUserHierarchyName(hierarchyName);
                    entity.setRankName(rankName);
                    Optional<?> value = optionEntry.getValue();
                    entity.setValue(optionStringConverter.convertValueToString(value.get()));
                    list.add(entity);
                }
            }
        }
        return list;
    }
}
