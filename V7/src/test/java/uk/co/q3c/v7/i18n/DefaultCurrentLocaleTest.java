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
package uk.co.q3c.v7.i18n;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.WebBrowser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.co.q3c.v7.base.ui.BrowserProvider;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.status.DefaultUserStatus;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultCurrentLocaleTest implements LocaleChangeListener {

    boolean listenerFired = false;

    DefaultCurrentLocale currentLocale;

    @Mock
    Annotation annotation;
    @Mock
    DefaultUserStatus userStatus;
    @Mock
    DefaultUserOption userOption;

    Locale defaultLocale;
    @Mock
    private WebBrowser browser;
    @Mock
    private BrowserProvider browserProvider;
    private Set<Locale> supportedLocales;


    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        when(browserProvider.get()).thenReturn(browser);
        listenerFired = false;
        supportedLocales = new HashSet<>();
        supportedLocales.add(Locale.UK);
        supportedLocales.add(Locale.GERMANY);
        supportedLocales.add(Locale.FRANCE);
        defaultLocale = Locale.UK;

    }

    @Test
    public void initialise_user_not_logged_in() {
        //given
        when(browser.getLocale()).thenReturn(Locale.GERMANY);
        when(userStatus.isAuthenticated()).thenReturn(false);
        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        currentLocale.readFromEnvironment();
        //then
        assertThat(currentLocale.getLocale()).isEqualTo(Locale.GERMANY);
    }

    @Test
    public void initialise_user_already_logged_in() {
        //given
        when(browser.getLocale()).thenReturn(Locale.GERMANY);
        when(userStatus.isAuthenticated()).thenReturn(true);
        setUserOption(Locale.UK);


        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);

        //then
        assertThat(currentLocale.getLocale()).isEqualTo(Locale.UK);
    }

    private void setUserOption(Locale userPref) {
        when(userOption.getOptionAsString(eq(DefaultCurrentLocale.class.getSimpleName()), eq("preferredLocale"),
                anyString())).thenReturn(userPref.toLanguageTag());
    }

    @Test
    public void initialise_browser_locale_not_supported_user_not_logged_in() {

        //given
        when(browser.getLocale()).thenReturn(Locale.CHINA);
        when(userStatus.isAuthenticated()).thenReturn(false);

        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);

        //then
        assertThat(currentLocale.getLocale()).isEqualTo(defaultLocale);
    }

    @Test
    public void initialise_browser_locale_not_supported_user_logged_in() {
        //        given
        when(browser.getLocale()).thenReturn(Locale.CHINA);
        when(userStatus.isAuthenticated()).thenReturn(true);
        setUserOption(Locale.UK);

        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        //then
        assertThat(currentLocale.getLocale()).isEqualTo(Locale.UK);
    }

    @Test
    public void initialise_user_option_invalid_browser_invalid() {
        //        given
        when(browser.getLocale()).thenReturn(Locale.CHINA);
        when(userStatus.isAuthenticated()).thenReturn(true);
        setUserOption(Locale.CHINA);

        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        //then
        assertThat(currentLocale.getLocale()).isEqualTo(defaultLocale);
    }

    @Test
    public void user_logs_out() {
        //given
        when(browser.getLocale()).thenReturn(Locale.GERMANY);
        when(userStatus.isAuthenticated()).thenReturn(true);
        setUserOption(Locale.FRANCE);

        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        currentLocale.readFromEnvironment();

        //then
        assertThat(currentLocale.getLocale()).isEqualTo(Locale.FRANCE);

        //given
        listenerFired = false;
        currentLocale.addListener(this);
        when(userStatus.isAuthenticated()).thenReturn(false);
        //when user logs out
        currentLocale.userStatusChanged();

        //then nothing should happen
        assertThat(currentLocale.getLocale()).isEqualTo(Locale.FRANCE);
        assertThat(listenerFired).isFalse();

    }

    @Test(expected = UnsupportedLocaleException.class)
    public void set_Locale_not_valid() {
        //given
        when(browser.getLocale()).thenReturn(Locale.GERMANY);
        when(userStatus.isAuthenticated()).thenReturn(false);
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        //when
        currentLocale.setLocale(Locale.CHINA);
        //then

    }

    @Test
    public void setLocaleValid() {

        // given
        when(browser.getLocale()).thenReturn(Locale.GERMANY);
        when(userStatus.isAuthenticated()).thenReturn(false);
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        currentLocale.readFromEnvironment();
        currentLocale.removeAllListeners();
        currentLocale.addListener(this);
        listenerFired = false;
        // when
        currentLocale.setLocale(Locale.UK);
        // then
        assertThat(listenerFired).isTrue();

    }

    @Test
    public void setLocaleNoFire() {
        // given
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        currentLocale.removeAllListeners();
        currentLocale.addListener(this);
        listenerFired = false;
        // when
        currentLocale.setLocale(Locale.FRANCE, false);
        // then
        assertThat(listenerFired).isFalse();
        assertThat(Locale.getDefault()).isEqualTo(Locale.FRANCE);
    }

    @Test
    public void setLocaleFire() {
        // given
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        currentLocale.removeAllListeners();
        currentLocale.addListener(this);
        listenerFired = false;
        currentLocale.setLocale(Locale.UK);
        // when
        currentLocale.setLocale(Locale.FRANCE, true);
        // then
        assertThat(listenerFired).isTrue();
        assertThat(Locale.getDefault()).isEqualTo(Locale.FRANCE);
    }

    @Test
    public void changeButNoChange() {

        // given
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        currentLocale.removeAllListeners();
        currentLocale.addListener(this);
        currentLocale.setLocale(Locale.GERMANY);
        listenerFired = false;
        // when
        currentLocale.setLocale(Locale.GERMANY);
        // then
        assertThat(listenerFired).isFalse();

    }

    @Test(expected = UnsupportedLocaleException.class)
    public void invalid_setup_default_locale_not_in_supported_locales() {
        //given
        defaultLocale = Locale.CANADA;
        //when
        currentLocale = new DefaultCurrentLocale(browserProvider, supportedLocales, defaultLocale, userStatus,
                userOption);
        //then
    }

    @Override
    public void localeChanged(Locale toLocale) {
        listenerFired = true;
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(Locale.class).annotatedWith(DefaultLocale.class)
                                  .toInstance(Locale.UK);
            }

        };
    }

}
