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

package uk.q3c.krail.core.ui

import com.google.inject.Inject
import com.google.inject.Provider
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Viewport
import com.vaadin.server.ErrorHandler
import com.vaadin.server.Responsive
import com.vaadin.ui.AbstractOrderedLayout
import com.vaadin.ui.Alignment
import com.vaadin.ui.VerticalLayout
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.ConfigurationException
import uk.q3c.krail.core.form.Form
import uk.q3c.krail.core.i18n.CommonLabelKey
import uk.q3c.krail.core.i18n.I18NProcessor
import uk.q3c.krail.core.monitor.PageLoadingMessage
import uk.q3c.krail.core.monitor.PageReadyMessage
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.push.Broadcaster
import uk.q3c.krail.core.push.KrailPushConfiguration
import uk.q3c.krail.core.push.PushMessageRouter
import uk.q3c.krail.core.user.notify.VaadinNotification
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.component.IconFactory
import uk.q3c.krail.core.view.component.NavigationBar
import uk.q3c.krail.core.view.component.UserStatusComponents
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.util.guice.SerializationSupport


/**
 * The UI class used in this test application for Krail
 *
 * @author David Sowerby
 */
//@Theme(ValoTheme.THEME_NAME)
@Theme("krail")
@Push
@Listener
//@PushStateNavigation // proper url paths
@Viewport("user-scalable=no,initial-scale=1.0")
open class SimpleUI @Inject
protected constructor(navigator: Navigator,
                      errorHandler: ErrorHandler,
                      broadcaster: Broadcaster,
                      pushMessageRouter: PushMessageRouter,
                      applicationTitle: ApplicationTitle,
                      translate: Translate,
                      currentLocale: CurrentLocale,
                      translator: I18NProcessor,
                      serializationSupport: SerializationSupport,
                      pushConfiguration: KrailPushConfiguration,
                      val iconFactory: IconFactory,
                      val vaadinNotification: VaadinNotification,
                      @field:Transient private val userStatusComponentsProvider: Provider<UserStatusComponents>,
                      @field:Transient private val messageBus: MessageBus)

    : ScopedUI(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator, serializationSupport, pushConfiguration) {
    val topBar = NavigationBar(translate)
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    override fun screenLayout(): AbstractOrderedLayout {
        topBar.build(navigator = krailNavigator, userStatusComponents = userStatusComponentsProvider.get(), iconFactory = iconFactory)
        val layout = VerticalLayout(topBar)
        return layout
    }

    override fun doLayout() {
        if (screenLayout == null) {
            screenLayout = screenLayout()
        }
        screenLayout.setSizeUndefined()
        screenLayout.setWidth("100%")
        content = screenLayout
        Responsive.makeResponsive(this)
    }

    override fun changeView(toView: KrailView) {
        log.debug("changing view to {}", toView.name)

        val newViewRoot = toView.rootComponent
                ?: throw ConfigurationException("The root component for " + toView.name + " cannot be null")

        if (toView is Form) {
            toView.translate()
        } else {
            translator.translate(toView)
        }
        if (view != null) {
            screenLayout.removeComponent(view.rootComponent)
        }

        screenLayout.addComponent(newViewRoot)
        screenLayout.setComponentAlignment(newViewRoot, Alignment.TOP_CENTER)
        this.view = toView
        val pageTitle = pageTitle()
        page.setTitle(pageTitle)
        log.debug("Page title set to '{}'", pageTitle)
    }

    init {
        messageBus.subscribe(this)
        this.viewDisplayPanelSizeFull = false
    }


    @Handler
    fun pageLoading(message: PageLoadingMessage) {
        if (message.uiKey === this.instanceKey) {
            topBar.titleKey = CommonLabelKey.Loading_
        }
    }

    @Handler
    fun pageReady(message: PageReadyMessage) {
        if (message.uiKey === this.instanceKey) {
            topBar.titleKey = view.nameKey
        }
    }

}




