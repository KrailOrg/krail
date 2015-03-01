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
 * Implementations represent an Option which can be at any rank in a {@link UserHierarchy}.  All calls reference an
 * implementation of {@link UserHierarchy}, either directly as a method parameter, or by assuming a default value.
 * The get() and set() methods also assume that the hierarchyRank is the highest for that hierarchy.  For getting or
 * setting values at a specific hierarchyRank use the getSpecific() and setSpecific() methods.
 * <p>
 * Created by David Sowerby on 03/12/14.
 */
public interface Option {


    //---------------------------------------------- get (highest) ----------------------------------------------------------

    /**
     * Calls {@link #get(Object, UserHierarchy, OptionKey)} with a default hierarchy
     */
    @Nonnull
    <T> T get(@Nonnull T defaultValue, @Nonnull OptionKey optionKey);


    /**
     * Returns the highest rank value for the option {@code optionKey}, for the {@code hierarchy}, for the current user.  If no value is found, {@code
     * defaultValue} is returned
     *
     * @param <T>
     *         a type determined by the defaultValue.  An implementation should assume that an object of any type can
     *         be
     *         passed.
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param hierarchy
     *         the hierarchy to use
     * @param optionKey
     *         identifier for the option, in its context
     *
     * @return the highest rank value for the option or the {@code defaultValue} if none found
     */
    @Nonnull
    <T> T get(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey optionKey);

    //------------------------------------------- get lowest--------------------------------------------------------


    /**
     * Calls {@link #getLowestRanked(Object, UserHierarchy, OptionKey)} with a default hierarchy
     */
    @Nonnull
    <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull OptionKey optionKey);

    /**
     * Returns the lowest rank value for the option {@code optionKey}, for the {@code hierarchy}, for the current user.  If no value is found, {@code
     * defaultValue} is returned
     *
     * @param <T>
     *         a type determined by the defaultValue.  An implementation should assume that an object of any type can
     *         be
     *         passed.
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param hierarchy
     *         the hierarchy to use
     * @param optionKey
     *         identifier for the option, in its context
     *
     * @return the lowest rank value for the option or the {@code defaultValue} if none found
     */
    @Nonnull
    <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey optionKey);


    //------------------------------------------- get specific --------------------------------------------------------
    /**
     * Calls {@link #getSpecificRanked(Object, UserHierarchy, int, OptionKey)} with a default hierarchy
     */
    <T> T getSpecificRanked(@Nonnull T defaultValue, int hierarchyRank, @Nonnull OptionKey optionKey);


    /**
     * Returns the value assigned to a specific rank for the option {@code optionKey}, for the {@code hierarchy}, for the current user.  If no value is
     * found, {@code
     * defaultValue} is returned
     *
     * @param <T>
     *         a type determined by the defaultValue.  An implementation should assume that an object of any type can
     *         be
     *         passed.
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param hierarchy
     *         the hierarchy to use
     * @param optionKey
     *         identifier for the option, in its context
     *
     * @return the value for the option or the {@code defaultValue} if none found
     */
    @Nonnull
    <T> T getSpecificRanked(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey optionKey);


    //---------------------------------------------- set ----------------------------------------------------------

    /**
     * Calls {@link #set(Object, UserHierarchy, int, OptionKey)}  with a default hierarchy and a hierarchy rank of 0 (the highest rank)
     */
    <T> void set(T value, @Nonnull OptionKey optionKey);

    /**
     * Calls {@link #set(Object, UserHierarchy, int, OptionKey)}  with a hierarchy rank of 0 (the highest rank)
     */
    <T> void set(T value, @Nonnull UserHierarchy hierarchy, @Nonnull OptionKey optionKey);


    /**
     * Calls {@link #set(Object, UserHierarchy, int, OptionKey)}  with a default hierarchy
     */

    <T> void set(T value, int hierarchyRank, @Nonnull OptionKey optionKey);

    /**
     * Sets the value for a composite key comprising the {@code context}, {@code key} & {@code qualifiers},
     * for the current user, in {@code hierarchy} at {@code hierarchyRank}.
     *
     * @param value
     *         the value to be stored.  This can be of any type supported by the implementation. That is usually
     *         determined by the underlying persistence layer.
     * @param hierarchy
     *         the hierarchy to use
     * @param hierarchyRank
     *         the hierarchy rank to assign the value to
     * @param optionKey
     *         identifier for the option, in its context
     */

    <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey optionKey);


    //--------------------------------------------- delete --------------------------------------------------------

    /**
     * Deletes the value assigned to {@code optionKey} for the current user, in {@code hierarchy} at {@code hierarchyRank}.
     *
     * @param hierarchy
     *         the hierarchy to use
     * @param hierarchyRank
     *         the hierarchy rank to delete the value assignment from
     * @param optionKey
     *         identifier for the option, in its context
     *
     * @return the previously assigned value of this option, or null if it had no assignment
     */
    @Nullable
    Object delete(@Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull OptionKey optionKey);
}
