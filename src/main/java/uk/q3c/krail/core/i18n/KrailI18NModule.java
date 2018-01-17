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
package uk.q3c.krail.core.i18n;

import com.google.inject.Singleton;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.view.component.DefaultLocaleContainer;
import uk.q3c.krail.core.view.component.DefaultLocaleIconGenerator;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.core.view.component.LocaleIconGenerator;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.EnumResourceBundle;
import uk.q3c.krail.i18n.bind.I18NModule;
import uk.q3c.krail.i18n.persist.PatternSource;
import uk.q3c.krail.i18n.persist.PatternUtility;
import uk.q3c.krail.i18n.persist.clazz.ClassPatternSource;
import uk.q3c.krail.i18n.persist.source.DefaultPatternSource;
import uk.q3c.krail.i18n.util.DefaultPatternUtility;

/**
 * Configures I18N for an application.
 * <p>
 * An I18N source is the equivalent of a persistence unit (the class based, EnumResourceBundle provision of I18N is considered to be a single source / PU).
 * <p>
 * A source is represented by an annotation, for example {@link ClassPatternSource} - which is provided by this module.  Other persistence providers (for
 * example krail-jpa) will provide bindings to their own {@link #sources}, which Guice merges into a single map.
 * <p>
 * An I18NKey implementation - for example, {@link LabelKey}, and its associated {@link EnumResourceBundle}s, are the equivalent to a Java Resource bundle
 */

public class KrailI18NModule extends I18NModule {

    @Override
    protected void configure() {
        super.configure();
        bindFieldScanner();
        bindProcessor();
        bindPatternUtility();
        bindLocaleContainer();
        bindLocaleIconGenerator();
    }

    /**
     * Provides captions and icons for supported locales
     */
    protected void bindLocaleContainer() {
        bind(LocaleContainer.class).to(DefaultLocaleContainer.class).in(Singleton.class);
    }

    /**
     * The LocaleIconGenerator provides country flags.  Override this method to provide your own implementation
     */
    protected void bindLocaleIconGenerator() {
        bind(LocaleIconGenerator.class).to(DefaultLocaleIconGenerator.class).in(Singleton.class);
    }

    @Override
    protected void bindPatternSource() {
        bind(PatternSource.class).to(DefaultPatternSource.class).in(VaadinSessionScoped.class);
    }

    /**
     * Override this method to provide your own implementation of {@link CurrentLocale} or to change the scope used.
     * Choose between {@link UIScoped} or {@link VaadinSessionScoped}, depending on whether you want users to set the
     * language for each browser tab or each browser instance, respectively.
     */
    @Override
    protected void bindCurrentLocale() {
        bind(CurrentLocale.class).to(VaadinCurrentLocale.class).in(VaadinSessionScoped.class);
    }

    /**
     * See javadoc for {@link I18NFieldScanner} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindFieldScanner() {
        bind(I18NFieldScanner.class).to(DefaultI18NFieldScanner.class);
    }

    /**
     * Override this method to provide your own implementation of {@link I18NProcessor}
     */
    protected void bindProcessor() {
        bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
    }

    /**
     * See javadoc for {@link PatternUtility} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindPatternUtility() {
        bind(PatternUtility.class).to(DefaultPatternUtility.class);
    }
}

