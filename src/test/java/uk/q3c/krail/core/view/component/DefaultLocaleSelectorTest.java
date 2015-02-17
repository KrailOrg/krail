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

package uk.q3c.krail.core.view.component;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import fixture.MockCurrentLocale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.testutil.MockOption;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class})
public class DefaultLocaleSelectorTest {

    @Mock
    VaadinService vaadinService;

    CurrentLocale currentLocale = new MockCurrentLocale();

    @Mock
    UserNotifier userNotifier;


    Option option = new MockOption();

    private DefaultLocaleSelector selector;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
        VaadinService.setCurrent(vaadinService);
        Set<Locale> supportedLocales = new HashSet<>();
        supportedLocales.add(Locale.UK);
        supportedLocales.add(Locale.GERMANY);

        LocaleContainer container = new LocaleContainer(supportedLocales, option);
        selector = new DefaultLocaleSelector(currentLocale, container, userNotifier);
    }

    @Test
    public void build() {

        // given

        // when

        // then
        assertThat(selector.selectedLocale()).isEqualTo(Locale.UK);
    }

    @Test
    public void localeChanged() {

        // given
        selector.setRespondToLocaleChange(true);
        // when
        selector.localeChanged(Locale.GERMANY);
        // then
        assertThat(selector.selectedLocale()).isEqualTo(Locale.GERMANY);
        // given
        selector.setRespondToLocaleChange(false);
        // when
        selector.localeChanged(Locale.UK);
        // then
        assertThat(selector.selectedLocale()).isEqualTo(Locale.GERMANY);
    }

}
