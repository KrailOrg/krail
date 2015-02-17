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

/**
 * Created by David Sowerby on 03/12/14.
 */
public interface Option {
    void configure(OptionContext consumer, Class<? extends Enum> keys);

    void configure(Class<? extends OptionContext> consumerClass, Class<? extends Enum> keys);

    /**
     * @param key
     *         the option specific key, for example SHOW_ALL_SECTIONS
     * @param qualifiers
     *         optional, these are usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a consumer.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have consumer=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     * @param <T>
     *         a type determined by the sampleValue.  An implementation should assume that an object of any type can be
     *         passed.       *
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param <T>
     *         a type determined by the defaultValue.
     *
     * @return
     */
    <T> T get(T defaultValue, Enum<?> key, String... qualifiers);

    <T> void set(T value, Enum<?> key, String... qualifiers);

    /**
     * Flushes the cache of the associated option store, for the current user
     */
    void flushCache();
}
