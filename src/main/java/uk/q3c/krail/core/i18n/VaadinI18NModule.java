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

import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.EnumResourceBundle;
import uk.q3c.krail.i18n.bind.I18NModule;
import uk.q3c.krail.i18n.persist.clazz.ClassPatternSource;

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

public class VaadinI18NModule extends I18NModule {


    @Override
    protected void bindCurrentLocale() {
        bind(CurrentLocale.class).to(VaadinCurrentLocale.class);
    }
}

