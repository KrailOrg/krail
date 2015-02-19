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

import com.google.inject.Inject;
import uk.q3c.krail.core.user.profile.DefaultUserHierarchy;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.util.KrailCodeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation for {@link Option}, and uses {@link OptionStore} for persistence.  Unless specific {@link
 * UserHierarchy} implementations are supplied to the methods, the default {@link UserHierarchy} is used
 * <p>
 * Created by David Sowerby on 03/12/14.
 */
public class DefaultOption implements Option {

    private Class<? extends OptionContext> context;
    private UserHierarchy defaultHierarchy;
    private OptionStore optionStore;
    @Inject
    protected DefaultOption(OptionStore optionStore, @DefaultUserHierarchy UserHierarchy defaultHierarchy) {
        this.optionStore = optionStore;
        this.defaultHierarchy = defaultHierarchy;
    }

    public Class<? extends OptionContext> getContext() {
        return context;
    }

    @Override
    public void init(@Nonnull OptionContext context) {
        this.context = context.getClass();
    }


    @Override
    public void flushCache() {
        optionStore.flushCache();
    }

    @Override
    public <T> void set(T value, Enum<?> key, String... qualifiers) {
        checkInit();
        optionStore.setValue(value, defaultHierarchy, 0, new OptionKey(context, key, qualifiers));
    }

    private void checkInit() {
        if (context == null) {
            String msg = getClass().getSimpleName() + " must be initialised.  Typically this is done by calling " +
                    "option.init in the constructor of the class implementing OptionContext, for example:\n\n option" +
                    ".init(this)\n\n ";
            throw new KrailCodeException(msg);
        }
    }

    @Override
    public synchronized <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, @Nonnull Enum<?> key,
                                     @Nullable String... qualifiers) {
        checkInit();
        optionStore.setValue(value, hierarchy, 0, new OptionKey(context, key, qualifiers));
    }

    /**
     * Sets the value for a specific key, for the current user, and hierarchy level.  The {@code context}, {@code key}
     * &
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
     *         the type of the option value
     */
    @Override
    public synchronized <T> void set(@Nonnull T value, @Nonnull UserHierarchy hierarchy, int hierarchyLevel, @Nonnull
    Enum<?> key, @Nullable String... qualifiers) {
        checkInit();
        optionStore.setValue(value, hierarchy, hierarchyLevel, new OptionKey(context, key, qualifiers));
    }


    @Override
    public void init(@Nonnull Class<? extends OptionContext> context) {
        this.context = context;
    }

    /**
     * Gets option value for the {@code key} and optional {@code qualifiers}, combined with the {@link OptionContext}
     * provided by the {@link #init(OptionContext)} method
     *
     * @param defaultValue
     *         the default value to be returned if no value is found in the store.  Also determines the type of the
     *         return value
     * @param key
     *         an enum key for the option
     * @param qualifiers
     *         optional qualifiers to make it possible to distinguish between otherwise identical options
     * @param <T>
     *         the data type of the option
     *
     * @return the value of the option, or the {@code defaultValue} if there is no value in the store for this option
     */
    @Override
    @Nonnull
    public <T> T get(@Nonnull T defaultValue, @Nonnull Enum<?> key, @Nullable String... qualifiers) {
        checkInit();

        return optionStore.getValue(defaultValue, defaultHierarchy, 0, new OptionKey(context, key, qualifiers));
    }

    /**
     * Calls {@link OptionStore#getValue(Object, UserHierarchy, int, OptionKey)} with {@code
     * hierarchyLevel} set to 0, and the {@code hierarchy} set to the {@link #defaultHierarchy}
     */
    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull Class<? extends OptionContext> contextClass,
                                  @Nonnull Enum<?> key, @Nullable String... qualifiers) {
        checkInit();
        return optionStore.getValue(defaultValue, defaultHierarchy, 0, new OptionKey(contextClass, key, qualifiers));
    }


    /**
     * Calls {@link OptionStore#getValue(Object, UserHierarchy, int, OptionKey)} with {@code
     * hierarchyLevel} set to 0
     */
    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, @Nonnull Enum<?> key,
                                  @Nullable String... qualifiers) {
        checkInit();
        return optionStore.getValue(defaultValue, hierarchy, 0, new OptionKey(context, key, qualifiers));
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
     *         the type of the option value
     *
     * @return the value from the layer 'nearest' the hierarchyLevel (nearest means returning the first value found,
     * starting at hierarchyLevel down through the layers),
     */
    @Override
    @Nonnull
    public synchronized <T> T get(@Nonnull T defaultValue, @Nonnull UserHierarchy hierarchy, int hierarchyLevel,
                                  @Nonnull Enum<?> key, @Nullable String... qualifiers) {
        checkInit();
        return optionStore.getValue(defaultValue, defaultHierarchy, hierarchyLevel, new OptionKey(context, key,
                qualifiers));
    }

    @Override
    @Nullable
    public Object delete(@Nonnull UserHierarchy hierarchy, @Nonnull Enum<?> key, @Nullable String... qualifiers) {
        checkInit();
        return optionStore.deleteValue(hierarchy, 0, new OptionKey(context, key, qualifiers));
    }

    @Override
    @Nullable
    public Object delete(@Nonnull Enum<?> key, @Nullable String... qualifiers) {
        checkInit();
        return optionStore.deleteValue(defaultHierarchy, 0, new OptionKey(context, key, qualifiers));
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
     * @return the previous value associated with composite key, or null if there was no mapping for key.
     */
    @Override
    @Nullable
    public Object delete(@Nonnull UserHierarchy hierarchy, int hierarchyLevel, @Nonnull Enum<?> key, @Nullable String
            ... qualifiers) {
        checkInit();
        return optionStore.deleteValue(defaultHierarchy, hierarchyLevel, new OptionKey(context, key, qualifiers));
    }
}
