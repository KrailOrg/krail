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

import com.vaadin.ui.Button
import com.vaadin.ui.Label
import com.vaadin.ui.PasswordField
import com.vaadin.ui.TextField
import uk.q3c.krail.core.user.status.UserStatusChangeSource
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.i18n.I18NKey

/**
 * Bind this to your implementation in your [ViewModule]
 *
 * @author David Sowerby 1 Jan 2013
 */
interface LoginView : KrailView, UserStatusChangeSource {


    val submit: Button

    /**
     * The message indicating login attempt pass or fail
     *
     * @return
     */
    val statusMessage: Label

    val username: TextField

    val password: PasswordField

    @Deprecated(message = "replaced", replaceWith = ReplaceWith("username(String)"))
    fun setUsername(username: String)

    @Deprecated(message = "replaced", replaceWith = ReplaceWith("password(String)"))
    fun setPassword(password: String)

    fun password(aPassword: String): LoginView {
        password.value = aPassword
        return this
    }

    fun username(aUsername: String): LoginView {
        username.value = aUsername
        return this
    }

    fun setStatusMessage(messageKey: I18NKey)

    fun setStatusMessage(message: String)
}
