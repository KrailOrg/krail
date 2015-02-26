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

import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.i18n.I18NKey;

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

    /**
     * Initialise to use the class of {@code context}
     *
     * @param context
     */
    void init(OptionContext context);

    /**
     * Initialise to use the {@code context}
     *
     * @param context
     */
    void init(Class<? extends OptionContext> context);


    //---------------------------------------------- get ----------------------------------------------------------


    /**
     * See {@link #get(Object, RankOption, UserHierarchy, int, Class, I18NKey, String...)}, but with an implementation
     * dependent default
     * hierarchy & context
     *
     * @return the highest rank value for the option
     */
    @Nonnull
    <T> T get(@Nonnull T defaultValue, @Nonnull I18NKey key, @Nullable String... qualifiers);


    @Nonnull
    <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull Class<? extends
            OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers);

    /**
     * See {@link #get(Object, RankOption, UserHierarchy, int, Class, I18NKey, String...)}, but with an implementation
     * dependent default
     * context
     *
     * @return the highest rank value for the option
     *
     * @see #get(Object, RankOption, UserHierarchy, int, Class, I18NKey, String...)
     */

    @Nonnull
    <T> T get(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull I18NKey key, @Nullable String...
            qualifiers);

    /**
     * See {@link #get(Object, RankOption, UserHierarchy, int, Class, I18NKey, String...)}, but with an implementation
     * dependent default hierarchy
     *
     * @return the highest rank value for the option
     */
    @Nonnull
    <T> T get(@Nonnull T defaultValue, @Nonnull Class<? extends OptionContext> contextClass, @Nonnull I18NKey key,
              @Nullable String... qualifiers);

    @Nonnull
    <T> T getLowestRanked(@Nonnull T defaultValue, @Nonnull I18NKey key, @Nullable String... qualifiers);

    @Nonnull
    <T> T getSpecificRank(@Nonnull T defaultValue, int hierarchyRank, @Nonnull I18NKey key, @Nullable String...
            qualifiers);

    <T> T get(@Nonnull T defaultValue, UserHierarchy hierarchy, @Nonnull Class<? extends OptionContext> contextClass, @Nonnull I18NKey key, @Nullable String... qualifiers);
    /**
     * Returns the highest rank value for the option identified by a composite key comprising the {@code context},
     * {@code key} & {@code qualifiers} for the {@code hierarchy}, for the current user.  If no value is found, {@code
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
     * @param context
     *         the class which is using the option
     * @param key
     *         the option specific key, for example SHOW_ALL_SECTIONS
     * @param qualifiers
     *         optional, these are usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a context.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have context=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     *
     * @return the highest rank value for the option or the {@code defaultValue} if none found
     */
    @Nonnull
    <T> T get(@Nonnull T defaultValue, @Nonnull RankOption rankOption, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull Class<? extends
            OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers);

    //---------------------------------------------- set ----------------------------------------------------------

    /**
     * See {@link #set(Object, UserHierarchy, int, Class, I18NKey, String...)} with an assumed hierarchyRank of 0 (the
     * highest rank), and an implementation dependent default hierarchy & context
     */
    <T> void set(T value, I18NKey key, String... qualifiers);

    /**
     * See {@link #set(Object, UserHierarchy, int, Class, I18NKey, String...)} with an assumed hierarchyRank of 0 (the
     * highest rank), and an implementation dependent default context
     */
    <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, @Nonnull I18NKey key, @Nullable String...
            qualifiers);

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
     * @param context
     *         the class which is using the option
     * @param key
     *         the option specific key, for example SHOW_ALL_SECTIONS
     * @param qualifiers
     *         optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a context.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have context=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     * @param <T>
     *         the type of the option value
     */
    <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull Class<? extends
            OptionContext> context, @Nonnull I18NKey key, @Nullable String... qualifiers);

    //---------------------------------------------- delete -------------------------------------------------------


    /**
     * Deletes the value for a composite key comprising the {@code context}, {@code key} & {@code qualifiers},
     * for the current user, in {@code hierarchy} at {@code hierarchyRank}.
     *
     * @param hierarchy
     *         the hierarchy to use
     * @param hierarchyRank
     *         the hierarchy rank to delete the value assignment from
     * @param context
     *         the class which is using the option
     * @param key
     *         the option specific key, for example SHOW_ALL_SECTIONS
     * @param qualifiers
     *         optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a context.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have context=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     *
     * @return the previously assigned value of this option, or null if it had no assignment
     */
    @Nullable
    Object delete(@Nonnull UserHierarchy hierarchy, int hierarchyRank, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey key, @Nullable String
            ... qualifiers);


}
