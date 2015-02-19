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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents the elements which go together to make up a unique {@link Option} key
 * <p>
 * Created by David Sowerby on 19/02/15.
 */
@Immutable
public class OptionKey {


    private Class<? extends OptionContext> context;
    private Enum<?> key;
    private String[] qualifiers;

    /**
     * @param context
     *         the class which uses the option
     * @param key
     *         the option specific key, for example SHOW_ALL_SECTIONS
     * @param qualifiers
     *         optional, this is usually dynamically generated qualifier(s) to make a complete unique identity where
     *         the same option may be used several times within a consumer.  If for example you have an array of
     *         dynamically generated buttons, which you want the user to be able to individually choose the colours
     *         of, you may have consumer=com.example.FancyButtonForm, key=BUTTON_COLOUR, qualifiers="2,3"
     *         <p>
     *         where "2,3" is the grid position of the button
     */
    public OptionKey(@Nonnull Class<? extends OptionContext> context, @Nonnull Enum<?> key, @Nullable String...
            qualifiers) {
        checkNotNull(context);
        checkNotNull(key);
        this.context = context;
        this.key = key;
        this.qualifiers = qualifiers;
    }

    @Nonnull
    public Class<? extends OptionContext> getContext() {
        return context;
    }

    @Nonnull
    public Enum<?> getKey() {
        return key;
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
    protected String compositeKey() {
        Joiner joiner = Joiner.on("-")
                              .skipNulls();
        return joiner.join(context.getSimpleName(), key.name(), qualifiers);
    }
}
