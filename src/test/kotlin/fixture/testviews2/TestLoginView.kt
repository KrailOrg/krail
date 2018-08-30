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
package fixture.testviews2

import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.Label
import com.vaadin.ui.PasswordField
import com.vaadin.ui.TextField
import uk.q3c.krail.core.user.LoginView
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.I18NKey

class TestLoginView : LoginView {
    override fun buildView() {
    }

    override fun afterBuild() {
    }

    override fun beforeBuild(navigationStateExt: NavigationStateExt) {

    }

    override val submit: Button
        get() = Button()

    override val statusMessage: Label
        get() = Label()

    override val username: TextField
        get() = TextField()

    override val password: PasswordField
        get() = PasswordField()

    override fun beforeBuild(busMessage: ViewChangeBusMessage) {

    }

    override fun buildView(busMessage: ViewChangeBusMessage) {

    }

    override fun getRootComponent(): Component {
        return Label("not used")
    }

    override fun setUsername(username: String) {

    }

    override fun setPassword(password: String) {

    }

    override fun setStatusMessage(message: String) {

    }

    override fun setStatusMessage(messageKey: I18NKey) {}

    override fun init() {}

    override fun afterBuild(busMessage: AfterViewChangeBusMessage) {

    }

    override fun rebuild() {

    }

    override fun getNameKey(): I18NKey? {
        return null
    }

    override fun setNameKey(nameKey: I18NKey) {

    }

    override fun getDescriptionKey(): I18NKey? {
        return null
    }

    override fun setDescriptionKey(descriptionKey: I18NKey) {

    }

    override fun getName(): String? {
        return null
    }

    override fun getDescription(): String? {
        return null
    }

    override fun password(aPassword: String): LoginView {
        return this
    }

    override fun username(aUsername: String): LoginView {
        return this
    }

    override fun identity(): String? {
        return null
    }
}
