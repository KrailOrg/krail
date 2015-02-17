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


import java.util.Optional;

/**
 * Stores and loads option values from a (usually) persistent store.  A simple, in memory, version is provided
 * primarily for testing.
 * <p>
 * <p>
 * Created by David Sowerby on 04/12/14.
 */
public interface OptionStore {

    /**
     * Loads an {@code Optional<T>} option value from the store, or {@link Optional#empty()}} is no valid value found
     * (including a situation where the type stored cannot be cast to the type to be loaded)
     *
     * @param sampleValue
     *         this is used only for typing the return value, it is not a default value as with other parts of the
     *         {@link Option} API
     * @param layerId
     *         represents a layer in a hierarchy of options - these are prefixed with a numeral indicating the level
     *         of the layer (for example, a user id might be "0:dsowerby").  Layer 0 is always the user layer, and
     *         layer 99 is always the system layer and both are always defined.  There may or may not be other layers
     *         in between.  Option values may or may not exist in any layer.  The layer numbers indicate a priority,
     *         and the lower the number the higher the priority. An implementation ensures that the hierarchy is
     *         honoured so that an option value is returned for the highest priority available for that option. For
     *         example, if a user option exists, it will always override the same option defined at the system layer.
     * @param consumerClassName
     *         the class name of an implementation of {@link OptionContext} which uses a specific option.  For example
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
     * @param <T>
     *         a type determined by the sampleValue.  An implementation should assume that an object of any type can be
     *         passed.       *
     *
     *
     *
     * @return
     */

    <T> Optional<T> load(T sampleValue, String layerId, String consumerClassName, String key, String qualifiers);

    /**
     * Stores an {@code Optional<T>} option value in the store
     *
     * @param value
     *         this value to be stored.  This can be of any type supported by the implementation. That is usually determined by the underlying persistence layer.
     *         <p>
     *         Other parameters are the same as for {@link #load(Object, String, String, String, String)}
     *
     * @exception OptionTypeException if the value type is not supported
     *
     * @return
     */
    <T> void store(T value, String layerId, String consumerClassName, String key, String qualifiers);

    /**
     * Flushes the cache (if there is one - that will depend on the implementation)
     */
    void flushCache();
}
