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
package uk.q3c.krail.core.user.notify

import uk.q3c.krail.i18n.EnumResourceBundle
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable

/**
 * Provides a common entry point for all notifications to users, at error, warning and information level.
 *
 * @author David Sowerby
 */
interface UserNotifier : Serializable {

    /**
     * Sends a translated [ErrorNotificationMessage] via the Session Bus
     *
     * @param msg the message pattern
     * @param params parameters for the message pattern
     */
    fun notifyError(msg: I18NKey, vararg params: Any)

    /**
     * Sends a translated [WarningNotificationMessage] via the Session Bus
     *
     * @param msg the message pattern
     * @param params parameters for the message pattern
     */
    fun notifyWarning(msg: I18NKey, vararg params: Any)

    /**
     * Sends a translated [InformationNotificationMessage] via the Session Bus
     *
     * @param msg the message pattern
     * @param params parameters for the message pattern
     */
    fun notifyInformation(msg: I18NKey, vararg params: Any)

}


enum class UserNotificationLabelKey : I18NKey {
    A_system_error_occurred
}


class UserNotificationLabels : EnumResourceBundle<UserNotificationLabelKey>() {

    override fun loadMap() {
        put(UserNotificationLabelKey.A_system_error_occurred, "A System error occurred and has been logged\n\nPlease close this window and go back a step.: \n\n{0}")
    }
}