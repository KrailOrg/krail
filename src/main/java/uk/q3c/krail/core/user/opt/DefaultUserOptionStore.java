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
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A volatile, in-memory store for user options
 */
@Singleton
public class DefaultUserOptionStore implements UserOptionStore {

    private Map<String, Object> map = new ConcurrentHashMap<>();

    /**
     * Loads an {@code Optional<T>} option value from the store, or {@link Optional#absent()}} is no valid value found
     * (including a situation where the type stored cannot be cast to the type to be loaded)
     *
     * @param sampleValue
     *         this is used only for typing the return value, it is not a default value as with other parts of the
     *         UserOption API
     * @param layerId
     *         represents a layer in a hierarchy of options - these are prefixed with a numeral indicating the level
     *         of the layer (for example, a user id might be "0:dsowerby").  Layer 0 is always the user layer, and
     *         layer 99 is always the system layer and both are always available.  There may or may not be other
     *         layers in between.  An implementation ensures that the hierarchy is honoured so that the highest layer
     *         with a specific option value overrides any values for the same option at lower layers. For example, a
     *         user layer option will always override the same option defined at the system layer.
     * @param consumerClassName
     *         the class name of an implementation of UserOptionConsumer which uses a specific option.  For example
     *         OrderInputForm
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
     * @return
     */
    @Override
    synchronized public <T> Optional<T> load(T sampleValue, String layerId, String consumerClassName, String key,
                                             String qualifiers) {
        Object result = map.get(compositeKey(layerId, consumerClassName, key, qualifiers));
        try {
            return Optional.of((T) result);
        } catch (Exception e) {
            return Optional.absent();
        }

    }

    @Inject
    protected DefaultUserOptionStore() {
    }

    private String compositeKey(String layerId, String consumerClassName, String key, String qualifiers) {
        Joiner joiner = Joiner.on("-")
                              .skipNulls();
        return joiner.join(layerId, consumerClassName, key, qualifiers);
    }

    /**
     * Stores an {@code Optional<T>} option value in the store
     *
     * @param value
     *         this value to be stored.  This can be of any type supported by the implementation. That is usually
     *         determined by the underlying persistence layer.
     *         <p>
     *         Other parameters are the same as for {@link #load(Object, String, String, String, String)}
     * @param layerId
     * @param consumerClassName
     * @param key
     * @param qualifiers
     *
     * @return
     *
     * @throws UserOptionTypeException
     *         if the value type is not supported
     */
    @Override
    synchronized public <T> void store(T value, String layerId, String consumerClassName, String key, String
            qualifiers) {

        map.put(compositeKey(layerId, consumerClassName, key, qualifiers), value);

    }

    /**
     * Flushes the cache (if there is one - that will depend on the implementation)
     */
    @Override
    synchronized public void flushCache() {

    }
}
