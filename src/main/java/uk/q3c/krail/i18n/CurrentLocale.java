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
package uk.q3c.krail.i18n;

import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;

import java.util.Locale;

/**
 * Provides a reference to the currently selected Locale. Changes to Locale are published by the session scoped event bus. It is expected that implementations
 * will generally be {@link VaadinSessionScoped} as the selection of locale is a choice usually available to an individual user.
 * <p>
 *
 * @author David Sowerby 3 Mar 2013
 */

public interface CurrentLocale {


    /**
     * Sets up the locale from the environment (typically browser locale and user option settings)
     */
    void readFromEnvironment();

    Locale getLocale();

    /**
     * Equivalent to {@link #setLocale(Locale, boolean)} with fireListeners = true
     *
     * @param locale
     */
    void setLocale(Locale locale);

    void setLocale(Locale locale, boolean fireListeners);


}
