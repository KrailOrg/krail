package uk.q3c.krail.core.view.component

import com.google.common.collect.ImmutableList
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.nhaarman.mockito_kotlin.whenever
import com.vaadin.server.ErrorHandler
import com.vaadin.server.UICreateEvent
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinService
import com.vaadin.ui.*
import org.amshove.kluent.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.sitemap.DefaultMasterSitemap
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue
import uk.q3c.krail.core.push.Broadcaster
import uk.q3c.krail.core.push.PushMessageRouter
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.ui.ApplicationTitle
import uk.q3c.krail.core.ui.DefaultUIModule
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.krail.core.ui.ScopedUIProvider
import uk.q3c.krail.core.view.DefaultViewFactory
import uk.q3c.krail.core.view.ViewBase
import uk.q3c.krail.core.view.ViewFactory
import uk.q3c.krail.functest.*
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.option.Option
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.util.UtilModule
import java.io.File


/**
 * Created by David Sowerby on 03 Feb 2018
 */
class DefaultFunctionalTestSupportBuilderTest {

    lateinit var injector: Injector
    lateinit var functionalTestSupportBuilder: FunctionalTestSupportBuilder
    lateinit var masterSitemap: MasterSitemap
    lateinit var uiProvider: ScopedUIProvider


    @Before
    fun setUp() {
        injector = Guice.createInjector(IdGeneratorModule(), UtilModule(), TestUIScopeModule(), DefaultUIModule().uiClass(TestUI::class.java))
        functionalTestSupportBuilder = injector.getInstance(FunctionalTestSupportBuilder::class.java)
        masterSitemap = injector.getInstance(MasterSitemapQueue::class.java).currentModel
        uiProvider = injector.getInstance(ScopedUIProvider::class.java)
    }


    @Test
    fun fullSitemapExtract() {
        // given
        val mockRequest: VaadinRequest = mock()
        val mockService: VaadinService = mock()
        whenever(mockRequest.service).thenReturn(mockService)
        uiProvider.createInstance(UICreateEvent(mockRequest, TestUI::class.java))
        masterSitemap.addNode(MasterSitemapNode(1, "simple", SimpleView::class.java, LabelKey.Log_In, -1, PageAccessControl.PUBLIC, ImmutableList.of()))
        masterSitemap.addNode(MasterSitemapNode(2, "simple/another", AnotherSimpleView::class.java, LabelKey.Active_Source, -1, PageAccessControl.PUBLIC, ImmutableList.of()))

        // when
        val fts = functionalTestSupportBuilder.generate()

        // then
        fts.routeMap.map["simple"].shouldNotBeNull()
        fts.routeMap.map["simple"]?.route?.shouldBeEqualTo("simple")

        fts.routeMap.map["simple/another"].shouldNotBeNull()
        fts.routeMap.map["simple/another"]?.route?.shouldBeEqualTo("simple/another")

        fts.uis.keys.size.shouldBe(1)
        fts.uis.keys.first().uiId.name.shouldEqual("TestUI")

        fts.uis.values.forEach({ v ->
            v.nodes()
                    .shouldContain(ComponentIdEntry(name = "TestUI", id = "TestUI", type = "TestUI", baseComponent = false))
                    .shouldContain(ComponentIdEntry(name = "label", id = "TestUI-label", type = "Label", baseComponent = true))
        })

        val routeEntry: RouteIdEntry = fts.routeMap.map["simple"] ?: throw AssertionError("failed to find route")
        val customEntry = ComponentIdEntry(name = "custom", type = "TestCustomComponent", id = "SimpleView-custom", baseComponent = false)
        val customLabel = ComponentIdEntry(name = "labelInCustom", type = "Label", id = "SimpleView-custom-labelInCustom", baseComponent = true)
        fts.routeMap.viewFor("simple").viewId.id.shouldBeEqualTo("SimpleView")
        fts.routeMap.viewFor("simple/another").viewId.id.shouldBeEqualTo("AnotherSimpleView")
        fts.routeMap.uiFor("simple").uiId.id.shouldBeEqualTo("TestUI")
        fts.routeMap.uiFor("simple/another").uiId.id.shouldBeEqualTo("TestUI")
//        routeEntry.idGraph.successors(customEntry).shouldContain(customLabel)


        // when we generate view objects
        val pageObjectGenerator = KotlinPageObjectGenerator()
        pageObjectGenerator.generate(fts, File("/tmp/PageObjects.kt"), "uk.q3c.krail.functest")

        fts.routeMap.toJson(File("/tmp/routeMap.json"))
        val routeMap2 = routeMapFromJson(File("/tmp/routeMap.json"))

        assertThat(fts.routeMap).isEqualTo(routeMap2)

    }
}

class TestUI @Inject
protected constructor(navigator: Navigator, errorHandler: ErrorHandler,
                      broadcaster: Broadcaster, pushMessageRouter: PushMessageRouter, applicationTitle: ApplicationTitle, translate: Translate, currentLocale: CurrentLocale, translator: I18NProcessor, option: Option) : ScopedUI(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator) {

    val label = Label("ui")

    override fun screenLayout(): AbstractOrderedLayout {
        return VerticalLayout(viewDisplayPanel)
    }


}

class IdGeneratorModule : AbstractModule() {
    val mockMasterSitemapQueue: MasterSitemapQueue = mock()
    val mockSitemapService: SitemapService = mock()
    val translate: Translate = mock()
    val mockCurrentLocale: CurrentLocale = mock()
    val masterSitemap: MasterSitemap = DefaultMasterSitemap(StrictURIFragmentHandler())
    val mockOption: Option = mock()
    val mockPushMessageRouter: PushMessageRouter = mock()
    val mockBroadcaster: Broadcaster = mock()
    val mockNavigator: Navigator = mock()
    val mockI18NProcessor: I18NProcessor = mock()
    val mockErrorHandler: ErrorHandler = mock()


    init {
        whenever(mockMasterSitemapQueue.currentModel).thenReturn(masterSitemap)
    }

    override fun configure() {
        bind(MasterSitemapQueue::class.java).toInstance(mockMasterSitemapQueue)
        bind(ViewFactory::class.java).to(DefaultViewFactory::class.java)
        bind(Translate::class.java).toInstance(translate)
        bind(CurrentLocale::class.java).toInstance(mockCurrentLocale)
        bind(SitemapService::class.java).toInstance(mockSitemapService)
        bind(FunctionalTestSupportBuilder::class.java).to(DefaultFunctionalTestSupportBuilder::class.java)
        bind(ComponentIdGenerator::class.java).to(DefaultComponentIdGenerator::class.java)
        bind(Option::class.java).toInstance(mockOption)
        bind(PushMessageRouter::class.java).toInstance(mockPushMessageRouter)
        bind(Broadcaster::class.java).toInstance(mockBroadcaster)
        bind(Navigator::class.java).toInstance(mockNavigator)
        bind(I18NProcessor::class.java).toInstance(mockI18NProcessor)
        bind(ErrorHandler::class.java).toInstance(mockErrorHandler)

    }

}

class SimpleView @Inject constructor(translate: Translate) : ViewBase(translate) {
    lateinit var label: Label
    lateinit var custom: TestCustomComponent
    override fun doBuild(busMessage: ViewChangeBusMessage?) {
        label = Label("boo")
        custom = TestCustomComponent()
    }
}

class AnotherSimpleView @Inject constructor(translate: Translate) : ViewBase(translate) {
    lateinit var button: Button
    override fun doBuild(busMessage: ViewChangeBusMessage?) {
        button = Button("boo button")
    }
}

class TestCustomComponent : Panel() {
    var labelInCustom = Label()
    var butonnInCustom = Button()
}