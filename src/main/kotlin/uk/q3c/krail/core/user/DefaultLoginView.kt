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

package uk.q3c.krail.core.user

import com.google.inject.Inject
import com.vaadin.event.ShortcutAction
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.PasswordField
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.apache.commons.lang3.StringUtils
import org.apache.shiro.authc.UsernamePasswordToken
import org.slf4j.LoggerFactory
import uk.q3c.krail.core.eventbus.SessionBus
import uk.q3c.krail.core.guice.SerializationSupport
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Value
import uk.q3c.krail.core.shiro.SubjectProvider
import uk.q3c.krail.core.view.Grid3x3ViewBase
import uk.q3c.krail.core.view.component.AssignComponentId
import uk.q3c.krail.core.view.component.LoginFormException
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.eventbus.SubscribeTo
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate

@Listener
@SubscribeTo(SessionBus::class)
class DefaultLoginView @Inject constructor(
        private val subjectProvider: SubjectProvider,
        translate: Translate,
        serializationSupport: SerializationSupport) :

        Grid3x3ViewBase(translate, serializationSupport), LoginView, ClickListener {

    private val log = LoggerFactory.getLogger(this.javaClass.name)
    @AssignComponentId(assign = false, drilldown = false)
    @LoginCaption(caption = LoginLabelKey.Log_In, description = LoginDescriptionKey.Please_log_in)
    private lateinit var centrePanel: Panel
    @Value(LabelKey.Authentication)
    lateinit var label: Label
    @LoginCaption(caption = LoginLabelKey.Password, description = LoginDescriptionKey.Enter_Your_Password)
    override lateinit var password: PasswordField
    override lateinit var statusMessage: Label
    @LoginCaption(caption = LoginLabelKey.Submit, description = LoginDescriptionKey.Submit_Your_Login_Details)
    override lateinit var submit: Button
        private set
    @LoginCaption(caption = LoginLabelKey.User_Name, description = LoginDescriptionKey.Enter_your_user_name)
    override lateinit var username: TextField

    init {
        nameKey = LoginLabelKey.Log_In
        descriptionKey = LoginDescriptionKey.Log_In
    }

    public override fun doBuild(event: ViewChangeBusMessage) {
        super.doBuild(event)
        centrePanel = Panel()
        centrePanel.addStyleName(ValoTheme.PANEL_WELL)
        centrePanel.setSizeUndefined()
        val vl = VerticalLayout()
        centrePanel.content = vl
        vl.isSpacing = true
        vl.setSizeUndefined()
        label = Label()
        username = TextField()
        password = PasswordField()

        val demoInfoLabel = Label("for this demo, enter any user name, and a password of 'password'")
        val demoInfoLabel2 = Label("In a real application your Shiro Realm implementation defines how to authenticate")

        submit = Button()
        submit.addClickListener(this)
        submit.setClickShortcut(ShortcutAction.KeyCode.ENTER)
        submit.addStyleName(ValoTheme.BUTTON_PRIMARY)

        statusMessage = Label("Please enter your username and password")

        vl.addComponent(label)
        vl.addComponent(demoInfoLabel)
        vl.addComponent(demoInfoLabel2)
        vl.addComponent(username)
        vl.addComponent(password)
        vl.addComponent(submit)
        vl.addComponent(statusMessage)

        setMiddleCentre(centrePanel)


    }

    @Handler
    fun handleLoginFailed(msg: UserLoginFailed) {
        log.debug("UserLoginFailed received with ${msg.description}")
        statusMessage.value = translate.from(msg.description)
    }

    override fun buttonClick(event: ClickEvent) {
        val username = this.username.value
        val password = this.password.value
        if (StringUtils.isEmpty(username)) {
            throw LoginFormException(LabelKey.Username_Cannot_be_Empty)
        }
        if (StringUtils.isEmpty(password)) {
            throw LoginFormException(LabelKey.Password_Cannot_be_Empty)
        }
        val token = UsernamePasswordToken(username, password)
        subjectProvider.login(this, token)

    }

    override fun setUsername(username: String) {
        this.username.value = username
    }


    override fun setStatusMessage(message: String) {
        statusMessage.value = message
    }

    override fun setStatusMessage(messageKey: I18NKey) {
        statusMessage.value = translate.from(messageKey)
    }


    override fun setPassword(password: String) {
        this.password.value = password
    }


}
