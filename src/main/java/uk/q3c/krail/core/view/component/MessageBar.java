/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view.component;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import uk.q3c.krail.core.user.notify.ErrorNotificationMessage;
import uk.q3c.krail.core.user.notify.InformationNotificationMessage;
import uk.q3c.krail.core.user.notify.WarningNotificationMessage;

/**
 * A component to display notification messages to the user, as an alternative to, or complement of, {@link Notification}
 */
public interface MessageBar extends Component {

    /**
     * Display the message
     *
     * @param message
     *         the message to display
     */
    void errorMessage(ErrorNotificationMessage message);

    /**
     * Display the message
     *
     * @param message
     *         the message to display
     */
    void warningMessage(WarningNotificationMessage message);

    /**
     * Display the message
     *
     * @param message
     *         the message to display
     */
    void informationMessage(InformationNotificationMessage message);
}
