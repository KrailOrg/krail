/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package fixture;

import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.DefaultPatternSource;
import uk.q3c.krail.i18n.I18NModule;
import uk.q3c.krail.i18n.PatternSource;

import java.util.Locale;

/**
 * Different from
 */
public class TestI18NModule extends I18NModule {

    MockCurrentLocale currentLocale = new MockCurrentLocale();

    @Override
    protected void bindCurrentLocale() {
        bind(CurrentLocale.class).toInstance(currentLocale);
    }

    /**
     * Override this to define more registered annotations, or registered value annotations, by calling
     * {@link #registerAnnotation(Class)} or {@link #registerValueAnnotation(Class)}, or make a copy of this module and
     * use the same structure (multiple instances of the {@link #registeredAnnotations} and
     * {@link #registerValueAnnotation(Class)} will be merged by Guice}.
     * <p/>
     * Here you should also define the locales your application supports, with calls to
     * {@link #addSupportedLocale(Locale)}.  Make sure this includes the {@link DefaultLocale}
     */
    @Override
    protected void define() {
        super.define();
        addSupportedLocale(Locale.GERMANY);
        addSupportedLocale(Locale.ITALY);
        addSupportedLocale(new Locale("de", "CH"));
    }

    /**
     * Don't use VaadinSessionScope during testing, or the cache has to be cleared between each test
     */
    @Override
    protected void bindPatternSource() {
        bind(PatternSource.class).to(DefaultPatternSource.class);
    }
}
