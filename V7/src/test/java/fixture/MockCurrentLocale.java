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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.v7.base.user.status.UserStatusListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.LocaleChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * For testing all that is usually needed is to return a current locale and fire listeners on a change.  This class
 * gets
 * used by som any things it makes test setup a pain if the full version is used
 */
public class MockCurrentLocale implements CurrentLocale, UserStatusListener {
    private static Logger log = LoggerFactory.getLogger(MockCurrentLocale.class);
    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    private Locale locale;

    @Inject
    public MockCurrentLocale() {
        super();
        readFromEnvironment();
    }

    /**
     * Sets up the locale from the environment (typically browser locale and user option settings)
     */
    @Override
    public void readFromEnvironment() {
        setLocale(Locale.UK, false);
    }

    @Override
    public void setLocale(Locale locale, boolean fireListeners) {

        if (locale != this.locale) {
            this.locale = locale;
            Locale.setDefault(locale);
            log.debug("CurrentLocale set to {}", locale);
            if (fireListeners) {
                fireListeners(locale);
            }
        }

    }

    private void fireListeners(Locale locale) {
        for (LocaleChangeListener listener : listeners) {
            listener.localeChanged(locale);
        }
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        setLocale(locale, true);
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


    @Override
    public void userStatusChanged() {

    }
}
