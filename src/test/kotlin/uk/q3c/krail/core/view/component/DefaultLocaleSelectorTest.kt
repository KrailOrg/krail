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

package uk.q3c.krail.core.view.component

import com.google.inject.Inject
import com.mycila.testing.junit.MycilaJunitRunner
import com.mycila.testing.plugin.guice.GuiceContext
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.server.VaadinService
import com.vaadin.ui.ComboBox
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.q3c.krail.core.eventbus.VaadinEventBusModule
import uk.q3c.krail.core.guice.ServletEnvironmentModule
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.TestKrailI18NModule
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.eventbus.mbassador.EventBusModule
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.option.mock.MockOption
import uk.q3c.krail.option.mock.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.util.ResourceUtils
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule
import uk.q3c.util.guice.SerializationSupportModule
import java.util.*


@RunWith(MycilaJunitRunner::class)
@GuiceContext(VaadinSessionScopeModule::class, SerializationSupportModule::class, ServletEnvironmentModule::class, EventBusModule::class, TestOptionModule::class, InMemoryModule::class, VaadinEventBusModule::class, UIScopeModule::class, TestKrailI18NModule::class, UtilModule::class, UtilsModule::class)
class DefaultLocaleSelectorTest {


    var vaadinService: VaadinService = mock()

    @Inject
    var currentLocale: CurrentLocale? = null

    var userNotifier: UserNotifier = mock()

    @Inject
    var option: MockOption? = null

    @Inject
    var resourceUtils: ResourceUtils? = null

    @Inject
    var iconGenerator: LocaleIconGenerator? = null


    private lateinit var selector: DefaultLocaleSelector

    @Before
    fun setup() {
        Locale.setDefault(Locale.UK)
        currentLocale!!.locale = Locale.UK
        VaadinService.setCurrent(vaadinService)
        val supportedLocales = HashSet<Locale>()
        supportedLocales.add(Locale.UK)
        supportedLocales.add(Locale.GERMANY)

        val container = DefaultLocaleContainer(supportedLocales, iconGenerator!!)
        selector = DefaultLocaleSelector(currentLocale, container, userNotifier)
    }

    @Test
    fun build() {

        // given
        // we've only constructed the object
        // when
        // we do nothing else
        // then
        selector.selectedLocale().shouldBeNull()

    }


    @Test
    fun afterViewHasBeenBuilt() {
        // given a comboBox (the message is not actually used)
        val combo = ComboBox<Locale>()
        selector.setCombo(combo)
        val message = AfterViewChangeBusMessage(NavigationState(), NavigationState())
        // when
        selector.afterViewChange(message)
        // then
        selector.selectedLocale().shouldEqual(currentLocale!!.locale)
        combo.dataProvider.shouldBeInstanceOf(ListDataProvider::class)
        combo.itemIconGenerator.shouldBeInstanceOf(DefaultLocaleIconGenerator::class)
        combo.itemCaptionGenerator.shouldBeInstanceOf(DefaultLocaleContainer::class)
    }

    @Test
    fun localeChanged_butComboNotSet() {

        // given
        // no combo

        // when source is not the selector itself, so it should be processed
        selector.localeChanged(LocaleChangeBusMessage(this, Locale.GERMANY))
        // then
        selector.selectedLocale().shouldBeNull()

    }

    @Test
    fun localeChanged_withComboSet() {

        // given
        val combo = ComboBox<Locale>()
        selector.setCombo(combo)

        // when source is not the selector itself, so it should be processed
        selector.localeChanged(LocaleChangeBusMessage(this, Locale.GERMANY))
        // then
        selector.selectedLocale().shouldEqual(Locale.GERMANY)
        // given
        // when change is from the selector itself, ignore it
        selector.localeChanged(LocaleChangeBusMessage(selector, Locale.UK))
        // then
        selector.selectedLocale().shouldEqual(Locale.GERMANY)
    }

}
