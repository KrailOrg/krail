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

package uk.q3c.krail.core.guice.uiscope

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import com.vaadin.server.ClientConnector
import com.vaadin.server.UICreateEvent
import com.vaadin.server.UIProvider
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinSession
import com.vaadin.ui.UI
import com.vaadin.util.CurrentInstance
import net.engio.mbassy.bus.common.PubSubSupport
import org.apache.commons.lang3.SerializationUtils
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authz.Permission
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.subject.Subject
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import uk.q3c.krail.config.ConfigurationFileModule
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.eventbus.UIBus
import uk.q3c.krail.core.eventbus.VaadinEventBusModule
import uk.q3c.krail.core.form.ConverterModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.TestKrailI18NModule
import uk.q3c.krail.core.navigate.NavigationModule
import uk.q3c.krail.core.navigate.sitemap.SitemapModule
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.core.navigate.sitemap.SitemapSourceType
import uk.q3c.krail.core.push.PushModule
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.shiro.DefaultVaadinSessionProvider
import uk.q3c.krail.core.shiro.KrailSecurityManager
import uk.q3c.krail.core.shiro.SUBJECT_ATTRIBUTE
import uk.q3c.krail.core.shiro.ShiroVaadinModule
import uk.q3c.krail.core.shiro.VaadinSessionProvider
import uk.q3c.krail.core.ui.BasicUI
import uk.q3c.krail.core.ui.BasicUIProvider
import uk.q3c.krail.core.ui.DataTypeModule
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.krail.core.ui.ScopedUIProvider
import uk.q3c.krail.core.user.UserModule
import uk.q3c.krail.core.vaadin.JavaMockVaadinSession
import uk.q3c.krail.core.view.ViewModule
import uk.q3c.krail.core.view.component.DefaultComponentModule
import uk.q3c.krail.eventbus.BusMessage
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.eventbus.mbassador.EventBusModule
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.option.bind.OptionModule
import uk.q3c.krail.persist.InMemory
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.service.AbstractService
import uk.q3c.krail.testutil.ui.TestUIModule
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule
import uk.q3c.util.guice.InjectorLocator
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.guice.SerializationSupportModule
import uk.q3c.util.serial.tracer.SerializationTracer
import java.util.*

class UIScopeTest {
    var connectCount: Int = 0
    protected var mockedRequest = mock(VaadinRequest::class.java)
    protected var mockedSession = mock(VaadinSession::class.java)
    lateinit var provider: UIProvider
    lateinit var vaadinSessionProvider: VaadinSessionProvider
    lateinit var injector: Injector
    lateinit var ui: ScopedUI
    lateinit var mockVaadinSession: JavaMockVaadinSession
    val headlessToken = "eyJzdWIiOiJkYXZpZCIsImtub3duQXMiOiJkYXZpZCIsInJlYWxtTmFtZSI6ImRlZmF1bHRSZWFsbSJ9.QKkeO1w4HwGXLRuTxofDlEp7PsH6N8nYyhak7P0SKnn-OuvG8OTuuFne0bhAmMuN3dY3iOHNvHXzP4uMxr6sQA"

    protected val uiProvider: ScopedUIProvider
        get() = provider as ScopedUIProvider


    @Before
    fun setup() {
        mockVaadinSession = JavaMockVaadinSession.setup()
        VaadinSession.getCurrent().setAttribute(SUBJECT_ATTRIBUTE, headlessToken)
        Locale.setDefault(Locale.UK)
        vaadinSessionProvider = mock(VaadinSessionProvider::class.java)
        `when`(vaadinSessionProvider.get()).thenReturn(VaadinSession.getCurrent())
    }

    @After
    fun teardown() {
        JavaMockVaadinSession.clear()
    }

    @Test
    fun uiScope2() {

        // given

        val securityManager = KrailSecurityManager(cacheManagerOpt)
        //        securityManager.setVaadinSessionProvider(vaadinSessionProvider);

        SecurityUtils.setSecurityManager(securityManager)

        `when`(subject.isPermitted(anyString())).thenReturn(true)
        `when`<Boolean>(subject.isPermitted(any<Permission>(org.apache.shiro.authz.Permission::class.java))).thenReturn(true)
        `when`(mockedSession.hasLock()).thenReturn(true)


        // when

        val injector = Guice.createInjector(PushModule(), TestModule(), KrailApplicationConfigurationModule(), ViewModule(), UIScopeModule(), ConfigurationFileModule(),
                OptionModule().activeSource(InMemory::class.java), UserModule(), DefaultComponentModule(), TestKrailI18NModule(),
                DefaultShiroModule(), ShiroVaadinModule(), VaadinSessionScopeModule(), SitemapModule(), TestUIModule(),
                NavigationModule(), VaadinEventBusModule(), EventBusModule(), UtilModule(), ConverterModule(),
                DataTypeModule(), UtilsModule(), InMemoryModule().provideOptionDao(), ServletEnvironmentModule(), SerializationSupportModule())
        val injectorLocator = injector.getInstance(InjectorLocator::class.java)
        injectorLocator.put(injector)
        provider = injector.getInstance(UIProvider::class.java)
        createUI(BasicUI::class.java)
        val to1 = injector.getInstance(TestObject::class.java)
        createUI(BasicUI::class.java)
        val to2 = injector.getInstance(TestObject::class.java)
        // then

        val eventBusLiteral = object : TypeLiteral<PubSubSupport<BusMessage>>() {

        }
        val k = Key.get(eventBusLiteral, UIBus::class.java)
        injector.getInstance(k)

        assertThat(to1).isNotNull()
        assertThat(to2).isNotNull()
        assertThat(to1).isNotEqualTo(to2)

        val uiScope = UIScope.getCurrent()
        val output = SerializationUtils.serialize(uiScope)
        SerializationUtils.deserialize<UIScope>(output)
        val tracer = SerializationTracer()
        tracer.trace(uiScope)
        tracer.shouldNotHaveAnyDynamicFailures()
    }

    protected fun createUI(clazz: Class<out UI>): ScopedUI {
        val baseUri = "http://example.com"
        `when`(mockedRequest.getParameter("v-loc")).thenReturn("$baseUri/")
        `when`(mockedSession.createConnectorId(Matchers.any(ClientConnector::class.java))).thenAnswer(ConnectorIdAnswer())

        CurrentInstance.set(UI::class.java, null)
        CurrentInstance.set(UIKey::class.java, null)
        val event = mock(UICreateEvent::class.java)
        `when`(event.source).thenReturn(mockVaadinSession.vaadinService)

        val answer = Answer { clazz }
        `when`(event.uiClass).thenAnswer(answer)
        ui = uiProvider.createInstance(event) as ScopedUI
        ui.locale = Locale.UK
        CurrentInstance.set(UI::class.java, ui)

        ui.session = mockedSession

        return ui
    }

    internal class MockSitemapService @Inject
    protected constructor(translate: Translate, globalBusProvider: MessageBus, serializationSupport: SerializationSupport) : AbstractService(translate, globalBusProvider, serializationSupport), SitemapService {
        override val sourceTypes: MutableList<SitemapSourceType>
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        public override fun doStart() {

        }

        public override fun doStop() {

        }

        override fun getNameKey(): I18NKey {
            return LabelKey.Sitemap_Service
        }
    }

    internal class TestObject

    internal class MockSubjectProvider : Provider<Subject> {

        override fun get(): Subject {
            return subject
        }

    }

    internal class TestModule : AbstractModule() {

        override fun configure() {
            bind(UIProvider::class.java).to(BasicUIProvider::class.java)

            bind(TestObject::class.java).`in`(UIScoped::class.java)
            bind(VaadinSessionProvider::class.java).to(DefaultVaadinSessionProvider::class.java)
        }
    }

    inner class ConnectorIdAnswer : Answer<String> {

        override fun answer(invocation: InvocationOnMock): String {
            connectCount++
            return Integer.toString(connectCount)
        }

    }

    companion object {
        internal var cacheManagerOpt = Optional.empty<CacheManager>()
        internal var subject = mock(Subject::class.java)
    }

}
