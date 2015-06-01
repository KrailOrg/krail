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

import com.google.common.base.Joiner;
import uk.q3c.krail.i18n.I18NKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents the elements which go together to make up a unique {@link Option} key within its context
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
@Immutable
public class OptionKey<T extends Object> {


    private Class<? extends OptionContext> context;
    private T defaultValue;
    @Nullable
    private I18NKey descriptionKey;
    private I18NKey key;
    private String[] qualifiers;

    public OptionKey(@Nonnull T defaultValue, @Nonnull OptionContext context, @Nonnull I18NKey nameKey, @Nonnull I18NKey descriptionKey, @Nullable String...
            qualifiers) {
        this(defaultValue, context.getClass(), nameKey, descriptionKey, qualifiers);
    }

    /**
     * @param context
     *         the class which uses the option
     * @param nameKey
     *         used as part of the overall key, and also as an I18N label for displaying options values to users
     * @param descriptionKey
     *         not used as part of the overall key, it provides an I18N description (usually a tooltip) for the option when displayed to users.  If the key
     *         value (the I18N pattern)  contains parameter slots, the qualifiers are assumed to be the parameters, in the order supplied.
     * @param qualifiers
     *         optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a context.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have context=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     */
    public OptionKey(@Nonnull T defaultValue, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey nameKey, @Nullable I18NKey descriptionKey,
                     @Nullable String... qualifiers) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = descriptionKey;
        this.context = context;
        this.key = nameKey;
        this.qualifiers = qualifiers;
    }

    /**
     *
     * Same as {@link #OptionKey} but with description key and qualifiers = null
     *
     * @param context
     *         the class which uses the option
     * @param nameKey
     *         used as part of the overall key, and also as an I18N label for displaying options values to users

     */
    public OptionKey(@Nonnull T defaultValue, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey nameKey) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = null;
        this.context = context;
        this.key = nameKey;
        this.qualifiers = null;
    }

    /**
     *
     * Same as {@link #OptionKey} but with description key and qualifiers = null
     *
     * @param context
     *         the class which uses the option
     * @param nameKey
     *         used as part of the overall key, and also as an I18N label for displaying options values to users

     */
    public OptionKey(@Nonnull T defaultValue, @Nonnull OptionContext context, @Nonnull I18NKey nameKey) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = null;
        this.context = context.getClass();
        this.key = nameKey;
        this.qualifiers = null;
    }

    /**
     * Same as {@link #OptionKey} but with description key  = null
     *
     * @param context
     *         the class which uses the option
     * @param nameKey
     *         used as part of the overall key, and also as an I18N label for displaying options values to users
     */
    public OptionKey(@Nonnull T defaultValue, @Nonnull Class<? extends OptionContext> context, @Nonnull I18NKey nameKey, @Nullable String... qualifiers) {
        this.defaultValue = defaultValue;
        checkNotNull(context);
        checkNotNull(nameKey);
        this.descriptionKey = null;
        this.context = context;
        this.key = nameKey;
        this.qualifiers = qualifiers;
    }

    /**
     * Copy constructor which adds a qualifier to the {@code baseKey}
     *
     * @param baseKey
     *         the key on which to base the copy
     * @param qualifiers
     *         the qualifiers to append to the base key
     */
    protected OptionKey(@Nonnull OptionKey baseKey, String... qualifiers) {

        this((T) baseKey.getDefaultValue(), baseKey.getContext(), baseKey.getKey(), baseKey.getDescriptionKey(), qualifiers);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    public I18NKey getDescriptionKey() {
        return descriptionKey;
    }

    @Nonnull
    public Class<? extends OptionContext> getContext() {
        return context;
    }

    @Nonnull
    public I18NKey getKey() {
        return key;
    }

    /**
     * returns a copy of this key with qualifiers added (functionally the same as using the copy constructor but looks neater when called)
     *
     * @param qualifiers
     *         the qualifiers to append to this key
     *
     * @return a new instance with the qualifiers appended
     */
    public OptionKey<T> qualifiedWith(String... qualifiers) {
        List<String> allQualifiers = new ArrayList<>();

        if (this.getQualifiers() != null) {
            allQualifiers.addAll(Arrays.asList(this.getQualifiers()));
        }

        if (qualifiers != null) {
            allQualifiers.addAll(Arrays.asList(qualifiers));
        }

        return new OptionKey(this, allQualifiers.toArray(new String[1]));
    }

    @Nullable
    public String[] getQualifiers() {
        return qualifiers;
    }

    /**
     * Returns a concatenation of the supplied parameters to form a composite String key
     *
     * @return a concatenation of the supplied parameters to form a composite String key
     */
    @Nonnull
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptionKey)) {
            return false;
        }

        OptionKey optionKey = (OptionKey) o;

        if (!context.equals(optionKey.context)) {
            return false;
        }
        if (!key.equals(optionKey.key)) {
            return false;
        }
        return Arrays.equals(qualifiers, optionKey.qualifiers);

    }

    @Override
    public int hashCode() {
        int result = context.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + (qualifiers != null ? Arrays.hashCode(qualifiers) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OptionKey{" +
                "context=" + context +
                ", key=" + key +
                ", qualifiers=" + Arrays.toString(qualifiers) +
                '}';
    }
}
