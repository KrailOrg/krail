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
package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.vaadin.server.WebBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.ui.BrowserProvider;
import uk.q3c.krail.core.user.UserStatusChangeSource;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.core.user.status.UserStatus;
import uk.q3c.krail.core.user.status.UserStatusListener;
import uk.q3c.util.MessageFormat;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * When a CurrentLocale is instantiated, or its {@link #readFromEnvironment()} method is called,it sets the current
 * locale according to the following priorities:
 * <ol>
 * <li>If a user is authenticated, the {@link Option} for preferred locale is used, if valid</li>
 * <li>If a user is not logged in, or the user option was invalid, the browser locale is used</li>
 * <li>If the browser locale is not accessible, or is not a supported locale (as defined in {@link I18NModule} or its
 * sub-class), the {@link #defaultLocale} is used.</li>
 * </ol>
 * When a user logs in after initialisation, the {@link Option} value for preferred locale is used, and the locale
 * changed
 * if required.
 * When a user logs out, no change to locale is made, as the user may still have public pages they can view.
 * <p>
 * Scope for this class is set in {@link I18NModule} or its sub-class - this enables the developer to choose
 * between {@link UIScoped} or {@link VaadinSessionScoped}, depending on whether they want their users to set the
 * language for each browser tab or each browser instance, respectively.  By default it is set to {@link
 * VaadinSessionScoped}
 * <p>
 * {@link #defaultLocale} and {@link #supportedLocales} are set in {@link I18NModule} or its sub-class.  An {@link
 * UnsupportedLocaleException} will be thrown if an attempt is made to set a locale which is not in {@link
 * #supportedLocales}, or if {@link #defaultLocale} the is not in {@link #supportedLocales}.
 *
 * @author David Sowerby
 * @date 5 May 2014
 */

public class DefaultCurrentLocale implements CurrentLocale, UserStatusListener, OptionContext {

    private static Logger log = LoggerFactory.getLogger(DefaultCurrentLocale.class);
    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    private BrowserProvider browserProvider;
    private Locale defaultLocale;
    private Locale locale;
    private Option option;
    private Set<Locale> supportedLocales;
    private UserStatus userStatus;

    @Inject
    protected DefaultCurrentLocale(BrowserProvider browserProvider, @SupportedLocales Set<Locale> supportedLocales,
                                   @DefaultLocale Locale defaultLocale, UserStatus userStatus, Option option) {
        super();
        this.browserProvider = browserProvider;
        this.supportedLocales = supportedLocales;
        this.defaultLocale = defaultLocale;
        this.userStatus = userStatus;
        this.option = option;
        option.init(this);
        userStatus.addListener(this);
        locale = defaultLocale;
        if (!supportedLocales.contains(defaultLocale)) {
            String msg = MessageFormat.format("The default locale ({0}) you have specified must also be defined as a " +
                    "" + "supported locale in your Guice I18N module", defaultLocale);
            throw new UnsupportedLocaleException(msg);
        }
        //        readFromEnvironment();
    }

    /**
     * , see the Javadoc for this class
     */
    @Override
    public void readFromEnvironment() {

        if (setLocaleFromOption(true)) {
            return;
        }
        if (setLocaleFromBrowser(true)) {
            return;
        }
        setLocale(defaultLocale, true);
    }

    /**
     * Sets locale to the browser locale, if available.  Browser locale will not be available if the browser is not
     * active ( this usually only happens in testing or background tasks)
     *
     * @param fireListeners
     *         if true, fires change listeners if a change is made
     *
     * @return true if the browser was accessible and its locale is supported, false if no suitable locale has been set
     */
    private boolean setLocaleFromBrowser(boolean fireListeners) {
        WebBrowser webBrowser = browserProvider.get();
        if (webBrowser != null) {
            Locale browserLocale = webBrowser.getLocale();
            if (supportedLocales.contains(browserLocale)) {
                setLocale(browserLocale, fireListeners);
                return true;
            }
        }
        return false;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Explicitly set the locale
     */
    @Override
    public void setLocale(Locale locale) {
        setLocale(locale, true);
    }

    /**
     * Sets the locale and optionally fires listeners.  Typically, a call to this method is from a component which only
     * allows the selection of a supported locale.  However, if an attempt is made to set a locale which is not defined
     * in {@link #supportedLocales}, an UnsupportedLocaleException is thrown
     *
     * @param locale
     *         the locale to set
     * @param fireListeners
     *         if true, fire registered listeners
     */
    @Override
    public void setLocale(Locale locale, boolean fireListeners) {
        if (supportedLocales.contains(locale)) {

            if (locale != this.locale) {
                this.locale = locale;
                //                Locale.setDefault(locale);
                log.debug("CurrentLocale set to {}", locale);
                if (fireListeners) {
                    log.debug("firing listeners");
                    fireListeners(locale);
                }
            }
        } else {
            throw new UnsupportedLocaleException(locale);
        }

    }

    private void fireListeners(Locale locale) {
        for (LocaleChangeListener listener : listeners) {
            listener.localeChanged(locale);
        }
    }

    @Override
    public void addListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();

    }

    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }

    /**
     * User has just logged in, look for their preferred Locale from user options.
     * @param source
     */
    @Override
    public void userHasLoggedIn(UserStatusChangeSource source) {
        setLocaleFromOption(true);
    }

    /**
     * Sets the locale from the value held in {@link Option}, if available.  {@link Option} will not be available if
     * the user
     * is not authenticated.  It is possible that a user option is not supported (unlikely, but support for a language
     * could be withdrawn after the user has chosen it), in which case the locale is set to the first supported locale
     *
     * @param fireListeners
     *
     * @return true if the user options was valid, otherwise false
     */
    private boolean setLocaleFromOption(boolean fireListeners) {
        if (userStatus.isAuthenticated()) {
            Locale selectedLocale = option.get(defaultLocale, LabelKey.Preferred_Locale);
            if (supportedLocales.contains(selectedLocale)) {
                setLocale(selectedLocale, fireListeners);
                return true;
            }
        }
        return false;
    }

    /**
     * User has just logged out, but they may still have public pages to view, and they would
     * probably want to view those in the same language as they had selected while logged in
     * @param source
     */
    @Override
    public void userHasLoggedOut(UserStatusChangeSource source) {

    }
}
