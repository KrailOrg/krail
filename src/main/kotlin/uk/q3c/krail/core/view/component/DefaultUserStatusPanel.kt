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
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.themes.ValoTheme
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.apache.shiro.subject.Subject
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.eventbus.SessionBus
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.UserStatusKey
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.core.shiro.SubjectProvider
import uk.q3c.krail.core.user.LoginLabelKey
import uk.q3c.krail.core.user.UserHasLoggedIn
import uk.q3c.krail.core.user.UserHasLoggedOut
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.krail.eventbus.SubscribeTo
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.LocaleChangeBusMessage
import uk.q3c.krail.i18n.Translate

/**
 * Represents the "logged in" status of the current [Subject].
 *
 * converted to Kotlin  11 Mar 2018
 * @author David Sowerby 16 Jan 2013
 */
@Listener
@SubscribeTo(SessionBus::class)
@AssignComponentId
class DefaultUserStatusPanel @Inject
constructor(private val navigator: Navigator,
            private val subjectProvider: SubjectProvider,
            private val translate: Translate,
            private val currentLocale: CurrentLocale,
            private val iconFactory: IconFactory)

    : Panel(), UserStatusPanel, ClickListener, UserStatusChangeSource {


    override val userId: String
        get() = usernameLabel.value
    override val actionLabel: String
        get() = login_logout_Button.caption
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override val usernameLabel: Label
    override val login_logout_Button: Button
    private var loggedIn = false
    private var username: String = translate.from(LabelKey.Guest)

    init {
        setSizeFull()
        addStyleName(ValoTheme.PANEL_BORDERLESS)
        usernameLabel = Label()
        login_logout_Button = Button()
        login_logout_Button.addClickListener(this)
        val hl = HorizontalLayout()
        hl.isSpacing = true
        hl.addComponent(usernameLabel)
        hl.addComponent(login_logout_Button)
        this.content = hl
        configureDisplay()

    }

    fun configureDisplay() {
        log.debug("configuring display with Locale=${currentLocale.locale}")
        if (loggedIn) {
            login_logout_Button.icon = iconFactory.iconFor(UserStatusKey.Log_Out)
            login_logout_Button.caption = translate.from(LoginLabelKey.Log_Out).toLowerCase()
            usernameLabel.value = username
        } else {
            login_logout_Button.icon = iconFactory.iconFor(UserStatusKey.Log_In)
            login_logout_Button.caption = translate.from(LoginLabelKey.Log_In).toLowerCase()
            usernameLabel.value = translate.from(LabelKey.Guest)
        }
    }


    @Handler
    fun handleUserHasLoggedIn(event: UserHasLoggedIn) {
        log.debug("user has logged in")
        loggedIn = true
        username = event.knownAs
        configureDisplay()
    }

    @Handler
    fun handleUserHasLoggedOut(@Suppress("UNUSED_PARAMETER") event: UserHasLoggedOut) {
        log.debug("user has logged out")
        loggedIn = false
        configureDisplay()
    }


    override fun buttonClick(event: ClickEvent) {
        if (loggedIn) {
            subjectProvider.logout(this)
        } else {
            navigator.navigateTo(StandardPageKey.Log_In)
        }

    }

    @Handler
    fun localeChanged(busMessage: LocaleChangeBusMessage) {
        log.debug("locale change to {}", busMessage.newLocale)
        configureDisplay()
    }

}


/**
 * Represents the "logged in" status of the current [Subject].
 *
 * converted to Kotlin  11 Mar 2018
 * @author David Sowerby 16 Jan 2013
 */
@Listener
@SubscribeTo(SessionBus::class)
@AssignComponentId
class DefaultUserStatusComponents @Inject
constructor(private val navigator: Navigator,
            private val subjectProvider: SubjectProvider,
            private val iconFactory: IconFactory,
            private val translate: Translate,
            private val currentLocale: CurrentLocale)

    : UserStatusComponents, ClickListener, UserStatusChangeSource {


    override val userId: String
        get() = usernameLabel.value
    override val actionLabel: String
        get() = login_logout_Button.caption
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override val usernameLabel: Label
    override val login_logout_Button: Button
    private var loggedIn = false
    private var username: String = translate.from(LabelKey.Guest)

    init {
        usernameLabel = Label()
        login_logout_Button = Button()
        login_logout_Button.addClickListener(this)
        val hl = HorizontalLayout()
        hl.isSpacing = true
        hl.addComponent(usernameLabel)
        hl.addComponent(login_logout_Button)
        configureDisplay()

    }

    fun configureDisplay() {
        log.debug("configuring display with Locale=${currentLocale.locale}")
        if (loggedIn) {
            login_logout_Button.icon = iconFactory.iconFor(UserStatusKey.Log_Out)
            login_logout_Button.caption = translate.from(LoginLabelKey.Log_Out).toLowerCase()
            usernameLabel.value = username
        } else {
            login_logout_Button.icon = iconFactory.iconFor(UserStatusKey.Log_In)
            login_logout_Button.caption = translate.from(LoginLabelKey.Log_In).toLowerCase()
            usernameLabel.value = translate.from(LabelKey.Guest)
        }
    }


    @Handler
    fun handleUserHasLoggedIn(event: UserHasLoggedIn) {
        log.debug("user has logged in")
        loggedIn = true
        username = event.knownAs
        configureDisplay()
    }

    @Handler
    fun handleUserHasLoggedOut(@Suppress("UNUSED_PARAMETER") event: UserHasLoggedOut) {
        log.debug("user has logged out")
        loggedIn = false
        configureDisplay()
    }


    override fun buttonClick(event: ClickEvent) {
        if (loggedIn) {
            subjectProvider.logout(this)
        } else {
            navigator.navigateTo(StandardPageKey.Log_In)
        }

    }

    @Handler
    fun localeChanged(busMessage: LocaleChangeBusMessage) {
        log.debug("locale change to {}", busMessage.newLocale)
        configureDisplay()
    }

}
