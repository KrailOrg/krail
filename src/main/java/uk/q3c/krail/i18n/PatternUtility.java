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

package uk.q3c.krail.i18n;

import uk.q3c.krail.core.user.opt.Option;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Set;

/**
 * Implementations provide some general purpose utilities for managing I18N Patterns
 * <p>
 * Created by David Sowerby on 14/12/14.
 */
public interface PatternUtility {


    /**
     * Export I18N Pattern key value pairs from {@code source} to {@code targetDao} for all bundles and locales specified.  Unlike {@link #export(PatternDao,
     * Set, Set)}, which uses {@link PatternSource}, this method takes input directly from the source {@link PatternDao} without assessing Locale candidates.
     * This means that unless {@code autoStub} is true, some keys may not be exported (only keys with a value are exported)
     *
     * @param source
     *         the {@link PatternDao} to uses as a source
     * @param target
     *         the PatternDao to send the output to
     * @param bundles
     *         the I18NKey classes to export (each key class is equivalent to a bundle)
     * @param locales
     *         the Locales to export
     * @param autoStub
     * if true, and a value is not found, a stub value is exported.  The value is determined by stubWithKeyName and stubValue
     * @param stubWithKeyName if true, and autoStub is true and a value is not found, the key name is exported as the value.
     * @param stubValue if autoStub is true and a value is not found, and stubWithKeyName is false, this value is exported
     *
     * @return a count of the keys exported
     */
    long export(@Nonnull PatternDao source, @Nonnull PatternDao target, @Nonnull Set<Class<? extends I18NKey>> bundles, @Nonnull Set<Locale> locales, boolean
            autoStub, boolean stubWithKeyName, @Nullable String stubValue);

    /**
     * Export I18N Pattern key value pairs from {@link PatternSource} to {@code targetDao} for all bundles and locales specified.  Because this method employs
     * {@link PatternSource}, the output will be a combination of all sources currently defined by the {@link I18NModule} and the {@link Option} values used by
     * {@link PatternSource#retrievePattern}
     *
     * @param target
     *         the PatternDao to send the output to
     * @param bundles
     *         the I18NKey classes to export (each key class is equivalent to a bundle)
     * @param locales
     *         the Locales to export
     * @param target
     *         the PatternDao to send the output to
     * @param bundles
     *         the I18NKey classes to export (each key class is equivalent to a bundle)
     * @param locales
     *         the Locales to export
     *
     * @return a count of the keys exported
     */
    long export(@Nonnull PatternDao target, @Nonnull Set<Class<? extends I18NKey>> bundles, @Nonnull Set<Locale> locales);

    /**
     * Exports all the core Krail I18NKeys for all supported Locales (as defined by the {@link I18NModule}
     *
     * @param target
     *         the PatternDao to export to
     *
     * @return a count of all the keys exported
     */
    long exportCoreKeys(@Nonnull PatternDao target);
}
