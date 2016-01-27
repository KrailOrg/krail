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
package uk.q3c.krail.core.user.notify;

import uk.q3c.krail.core.i18n.I18NKey;

/**
 * Provides a common entry point for all notifications to users, at error, warning and information level.
 *
 * @author David Sowerby
 */
public interface UserNotifier {

    /**
     * Sends a translated {@link ErrorNotificationMessage} via the Session Bus
     *
     * @param msg the message pattern
     * @param params parameters for the message pattern
     */
    void notifyError(I18NKey msg, Object... params);

    /**
     * Sends a translated {@link WarningNotificationMessage} via the Session Bus
     *
     * @param msg the message pattern
     * @param params parameters for the message pattern
     */
    void notifyWarning(I18NKey msg, Object... params);

    /**
     * Sends a translated {@link InformationNotificationMessage} via the Session Bus
     *
     * @param msg the message pattern
     * @param params parameters for the message pattern
     */
    void notifyInformation(I18NKey msg, Object... params);

}
