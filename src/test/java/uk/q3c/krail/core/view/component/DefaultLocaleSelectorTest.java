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

package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.CurrentLocale;
import uk.q3c.krail.core.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.testutil.i18n.TestI18NModule;
import uk.q3c.krail.testutil.option.MockOption;
import uk.q3c.krail.testutil.option.TestOptionModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModule;
import uk.q3c.krail.util.ResourceUtils;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class, TestOptionModule.class, TestPersistenceModule.class, EventBusModule.class, UIScopeModule.class, TestI18NModule
        .class, UtilModule.class, UtilsModule.class})
public class DefaultLocaleSelectorTest {

    @Mock
    VaadinService vaadinService;

    @Inject
    CurrentLocale currentLocale;

    @Mock
    UserNotifier userNotifier;

    @Inject
    MockOption option;

    @Inject
    ResourceUtils resourceUtils;


    private DefaultLocaleSelector selector;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
        VaadinService.setCurrent(vaadinService);
        Set<Locale> supportedLocales = new HashSet<>();
        supportedLocales.add(Locale.UK);
        supportedLocales.add(Locale.GERMANY);

        LocaleContainer container = new LocaleContainer(supportedLocales, option, resourceUtils);
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
        // when source is not the selector itself, so it should be processed
        selector.localeChanged(new LocaleChangeBusMessage(this, Locale.GERMANY));
        // then
        assertThat(selector.selectedLocale()).isEqualTo(Locale.GERMANY);
        // given
        // when change is from the selector itself, ignore it
        selector.localeChanged(new LocaleChangeBusMessage(selector, Locale.UK));
        // then
        assertThat(selector.selectedLocale()).isEqualTo(Locale.GERMANY);
    }

}
