/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.i18n;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScoped;

import java.util.Locale;

/**
 * Provides a reference to the currently selected Locale. {@link LocaleChangeListener}s can be added to listen for
 * locale changes. It is expected that implementations will generally be {@link VaadinSessionScoped} as the selection
 * of
 * locale is a choice usually available to an individual user.
 * <p/>
 *
 * @author David Sowerby 3 Mar 2013
 */

public interface CurrentLocale {

    public abstract void removeListener(LocaleChangeListener listener);

    public abstract void addListener(LocaleChangeListener listener);

    /**
     * Sets up the locale from the environment (typically browser locale and user option settings)
     */
    void readFromEnvironment();

    public abstract Locale getLocale();

    /**
     * Equivalent to {@link #setLocale(Locale, true)}
     *
     * @param locale
     */
    public abstract void setLocale(Locale locale);

    public abstract void setLocale(Locale locale, boolean fireListeners);

    public abstract void removeAllListeners();

}
