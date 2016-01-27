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

package uk.q3c.krail.core.user.opt;

import uk.q3c.krail.core.i18n.I18NKey;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes an {@link Option} - used to support the presentation of  information about an option in the user interface, and provided by implementers of {@link
 * OptionContext}
 * <p>
 * <p>
 * Created by David Sowerby on 28/02/15.
 */
@Immutable
public class OptionDescriptor {

    private final I18NKey descriptionKey;
    private final OptionKey optionKey;
    private final boolean allQualifiers;

    public OptionDescriptor(@Nonnull OptionKey optionKey, @Nonnull I18NKey descriptionKey) {
        this(optionKey, descriptionKey, false);
    }

    /**
     * @param optionKey
     *         the option key which defines the option within its context
     * @param descriptionKey
     *         a description key to look up the I18N description for the option
     * @param allQualifiers
     *         if true the description applies to all optionKeys with the same context and key - in other words the qualifier element is ignored
     */
    public OptionDescriptor(@Nonnull OptionKey optionKey, @Nonnull I18NKey descriptionKey, boolean allQualifiers) {
        this.allQualifiers = allQualifiers;
        checkNotNull(optionKey);
        checkNotNull(descriptionKey);
        this.optionKey = optionKey;
        this.descriptionKey = descriptionKey;
    }

    @Nonnull
    public static OptionDescriptor descriptor(OptionKey optionKey, I18NKey descriptionKey, boolean allQualifiers) {
        return new OptionDescriptor(optionKey, descriptionKey, allQualifiers);
    }

    @Nonnull
    public static OptionDescriptor descriptor(OptionKey optionKey, I18NKey descriptionKey) {
        return new OptionDescriptor(optionKey, descriptionKey, false);
    }

    public boolean isAllQualifiers() {
        return allQualifiers;
    }

    @Nonnull
    public OptionKey getOptionKey() {
        return optionKey;
    }

    @Nonnull
    public I18NKey getDescriptionKey() {
        return descriptionKey;
    }

    @Nonnull
    public OptionDescriptor desc(OptionKey optionKey, I18NKey descriptionKey, boolean allQualifiers) {
        return new OptionDescriptor(optionKey, descriptionKey, allQualifiers);
    }

    @Nonnull
    public OptionDescriptor desc(OptionKey optionKey, I18NKey descriptionKey) {
        return new OptionDescriptor(optionKey, descriptionKey, false);
    }

}

