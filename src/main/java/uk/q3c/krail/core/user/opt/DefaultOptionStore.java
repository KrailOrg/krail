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

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.user.profile.UserHierarchyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A volatile, in-memory store for user options
 */
@Singleton
public class DefaultOptionStore implements OptionStore {

    private Map<String, Map<String, Map<String, Object>>> map = new ConcurrentHashMap<>();


    @Inject
    protected DefaultOptionStore() {
    }


    /**
     * Returns a concatenation of the supplied parameters to form a composite String key
     *
     * @param contextClass
     *         the object which uses the option
     * @param key
     *         the option specific key, for example SHOW_ALL_SECTIONS
     * @param qualifiers
     *         optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a consumer.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have consumer=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     *
     * @return a concatenation of the supplied parameters to form a composite String key
     */
    protected String compositeKey(Class<? extends OptionContext> contextClass, Enum<?> key, String... qualifiers) {
        Joiner joiner = Joiner.on("-")
                              .skipNulls();
        return joiner.join(contextClass.getSimpleName(), key.name(), qualifiers);
    }


    /**
     * Flushes the cache (if there is one - that will depend on the implementation)
     */
    @Override
    synchronized public void flushCache() {

    }


    /**
     * Sets the value for a specific key, for the current user, and hierarchy level.  The {@code context}, {@code key} &
     * {@code qualifiers}
     * form a composite key
     *
     * @param value
     *         the value to be stored.  This can be of any type supported by the implementation. That is usually
     *         determined by the underlying persistence layer.
     * @param hierarchy
     *         the hierarchy to use
     * @param hierarchyLevel
     *         the highest hierarchy level to assign the value to
     * @param optionKey
     * an object representing a unique key for the option
     *
     * @param <T>
     *         the type of the option value
     *         @throw {@link UserHierarchyException} if {@code hierarchyLevel} is out of range
     */
    @Override
    public synchronized <T> void setValue(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyLevel,
                                          @Nonnull OptionKey optionKey) {
        checkLevel(hierarchy, hierarchyLevel);
        if (!map.containsKey(hierarchy.persistenceName())) {
            map.put(hierarchy.persistenceName(), new HashMap<>());
        }
        Map<String, Map<String, Object>> hierarchyMap = map.get(hierarchy.persistenceName());
        String compositeKey = optionKey.compositeKey();
        if (!hierarchyMap.containsKey(compositeKey)) {
            hierarchyMap.put(compositeKey, new HashMap<>());
        }
        Map<String, Object> valueMap = hierarchyMap.get(compositeKey);
        valueMap.put(hierarchy.layerForCurrentUser(hierarchyLevel), value);
    }

    private void checkLevel(UserHierarchy hierarchy, int hierarchyLevel) {
        int layersAvailable = hierarchy.layersForCurrentUser()
                                       .size();
        if (hierarchyLevel >= (layersAvailable)) {
            throw new UserHierarchyException("The hierarchy level of " + hierarchyLevel + " requested is greater than" +
                    " the number of levels available, " + layersAvailable);
        }
    }


    /**
     * Gets a value for the specified key.  The {@code context}, {@code key} & {@code qualifiers} form a composite key
     *
     * @param defaultValue
     *         the value to be returned if no value found in persistence
     * @param hierarchy
     *         the hierarchy to use
     * @param hierarchyLevel
     *         the highest hierarchy level to return - usually level 0, (the user)
     * @param optionKey
     * an object representing a unique key for the option
     * @param <T>
     *         the type of the option value
     *
     * @return the value from the layer 'nearest' the hierarchyLevel (nearest means returning the first value found,
     * starting at hierarchyLevel down through the layers),
     */
    @Override
    @Nonnull
    public synchronized <T> T getValue(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, int hierarchyLevel,
                                       @Nonnull OptionKey optionKey) {
        checkLevel(hierarchy, hierarchyLevel);
        Map<String, Map<String, Object>> hierarchyMap = map.get(hierarchy.persistenceName());
        if (hierarchyMap == null) {
            return defaultValue;
        }
        Map<String, Object> valueMap = hierarchyMap.get(optionKey.compositeKey());
        if (valueMap == null) {
            return defaultValue;
        }
        List<String> layers = hierarchy.layersForCurrentUser();
        for (int i = hierarchyLevel; i < layers.size(); i++) {
            T value = (T) valueMap.get(layers.get(i));
            if (value != null) {
                return value;
            }
        }

        return defaultValue;
    }


    /**
     * Deletes a value for the current user, at a specific layer (specified by hierarchyLevel).  The {@code context},
     * {@code key} & {@code
     * qualifiers} form a composite key
     *
     * @param hierarchy
     *         the hierarchy to use
     * @param hierarchyLevel
     *         the index of the layer entry to delete
     * @param optionKey
     * an object representing a unique key for the option
     *
     * @return the previous value associated with composite key, or null if there was no mapping for key.
     */
    @Nullable
    @Override
    public Object deleteValue(@Nonnull UserHierarchy hierarchy, int hierarchyLevel, @Nonnull OptionKey optionKey) {
        checkLevel(hierarchy, hierarchyLevel);
        Map<String, Map<String, Object>> hierarchyMap = map.get(hierarchy.persistenceName());
        Map<String, Object> valueMap = hierarchyMap.get(optionKey.compositeKey());
        String layer = hierarchy.layerForCurrentUser(hierarchyLevel);
        return valueMap.remove(layer);
    }


}
