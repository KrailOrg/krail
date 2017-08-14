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

package uk.q3c.krail.core.option;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import uk.q3c.krail.i18n.I18NKey;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Represents the elements which go together to make up a unique {@link Option} key within its context
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
@Immutable
public class OptionKey<T> {


    private final Class<? extends OptionContext> context;
    private final T defaultValue;
    private final I18NKey descriptionKey;
    private final I18NKey key;
    private final List<String> qualifiers = new ArrayList<>();

    public OptionKey(T defaultValue, OptionContext context, I18NKey nameKey, I18NKey descriptionKey, String...
            qualifiers) {
        this(defaultValue, context.getClass(), nameKey, descriptionKey, qualifiers);
    }

    /**
     * @param context        the class which uses the option
     * @param nameKey        used as part of the overall key, and also as an I18N label for displaying options values to users
     * @param descriptionKey not used as part of the overall key, it provides an I18N description (usually a tooltip) for the option when displayed to users.
     *                       If the key
     *                       value (the I18N pattern)  contains parameter slots, the qualifiers are assumed to be the parameters, in the order supplied.
     * @param qualifiers     optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *                       the same option may be used several times within a context.  If for example you have an array of
     *                       dynamically generated buttons, which you want the user to be able to individually choose the colours
     *                       of, you may have context=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *                       <p>
     *                       where "2,3" is the grid position of the button
     */
    public OptionKey(T defaultValue, Class<? extends OptionContext> context, I18NKey nameKey, I18NKey descriptionKey,
                     String... qualifiers) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = descriptionKey;
        this.context = context;
        this.key = nameKey;
        addQualifiers(qualifiers);
    }


    /**
     * @param context        the class which uses the option
     * @param nameKey        used as part of the overall key, and also as an I18N label for displaying options values to users
     * @param descriptionKey not used as part of the overall key, it provides an I18N description (usually a tooltip) for the option when displayed to users.
     *                       If the key
     *                       value (the I18N pattern)  contains parameter slots, the qualifiers are assumed to be the parameters, in the order supplied.
     * @param qualifiers     optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *                       the same option may be used several times within a context.  If for example you have an array of
     *                       dynamically generated buttons, which you want the user to be able to individually choose the colours
     *                       of, you may have context=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *                       <p>
     *                       where "2,3" is the grid position of the button
     */
    public OptionKey(T defaultValue, Class<? extends OptionContext> context, I18NKey nameKey, I18NKey descriptionKey,
                     List<String> qualifiers) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = descriptionKey;
        this.context = context;
        this.key = nameKey;
        this.qualifiers.addAll(qualifiers);
    }

    /**
     * Same as {@link #OptionKey} but with description key and qualifiers = null
     *
     * @param context the class which uses the option
     * @param nameKey used as part of the overall key, and also as an I18N label for displaying options values to users
     */
    public OptionKey(T defaultValue, Class<? extends OptionContext> context, I18NKey nameKey) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = null;
        this.context = context;
        this.key = nameKey;
    }

    /**
     * Same as {@link #OptionKey} but with description key and qualifiers = null
     *
     * @param context the class which uses the option
     * @param nameKey used as part of the overall key, and also as an I18N label for displaying options values to users
     */
    public OptionKey(T defaultValue, OptionContext context, I18NKey nameKey) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = null;
        this.context = context.getClass();
        this.key = nameKey;
    }

    /**
     * Same as {@link #OptionKey} but with description key  = null
     *
     * @param context the class which uses the option
     * @param nameKey used as part of the overall key, and also as an I18N label for displaying options values to users
     */
    public OptionKey(T defaultValue, Class<? extends OptionContext> context, I18NKey nameKey, String... qualifiers) {
        checkNotNull(context);
        checkNotNull(nameKey);
        checkNotNull(defaultValue);
        this.defaultValue = defaultValue;
        this.descriptionKey = null;
        this.context = context;
        this.key = nameKey;
        addQualifiers(qualifiers);
    }

    /**
     * Copy constructor which adds a qualifier to the {@code baseKey}
     *
     * @param baseKey    the key on which to base the copy
     * @param qualifiers the qualifiers to append to the base key
     */
    protected OptionKey(OptionKey<T> baseKey, String... qualifiers) {
        this(baseKey.getDefaultValue(), baseKey.getContext(), baseKey.getKey(), baseKey.getDescriptionKey());
        this.qualifiers.addAll(baseKey.getQualifiers());
        this.qualifiers.addAll(Arrays.asList(qualifiers));
    }

    private void addQualifiers(String... qualifiers) {
        if (qualifiers != null) {
            this.qualifiers.addAll(Arrays.asList(qualifiers));
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }


    public I18NKey getDescriptionKey() {
        return descriptionKey;
    }

    public Class<? extends OptionContext> getContext() {
        return context;
    }

    public I18NKey getKey() {
        return key;
    }

    /**
     * returns a copy of this key with qualifiers added (functionally the same as using the copy constructor but looks neater when called)
     *
     * @param qualifiers the qualifiers to append to this key
     * @return a new instance with the qualifiers appended
     */
    public OptionKey<T> qualifiedWith(String... qualifiers) {
        return new OptionKey(this, qualifiers);
    }

    public ImmutableList<String> getQualifiers() {
        return ImmutableList.copyOf(qualifiers);
    }

    /**
     * Returns a concatenation of the supplied parameters to form a composite String key
     *
     * @return a concatenation of the supplied parameters to form a composite String key
     */
    public String compositeKey() {
        Joiner joiner = Joiner.on("-")
                              .skipNulls();
        Enum<?> e = (Enum<?>) key;

        ArrayList<String> params = new ArrayList<>();
        params.add(context.getSimpleName());
        params.add(e.name());
        if (qualifiers != null) {
            for (String qualifier : qualifiers) {
                params.add(qualifier);
            }
        }
        return joiner.join(params);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OptionKey)) {
            return false;
        }
        return this.compositeKey()
                   .equals(((OptionKey) other).compositeKey());
    }

    @Override
    public int hashCode() {
        return compositeKey().hashCode();
    }
}
