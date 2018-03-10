/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.user.status;

import uk.q3c.krail.core.user.UserHasLoggedIn;
import uk.q3c.krail.core.user.UserHasLoggedOut;
import uk.q3c.krail.core.view.LoginView;
import uk.q3c.krail.eventbus.BusMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A bus message published when a user logs in or logs out.
 * <p>
 * Created by David Sowerby on 08/03/15.
 *
 * @deprecated Use {@link UserHasLoggedIn} and {@link UserHasLoggedOut}
 */
@Deprecated
public class UserStatusBusMessage implements BusMessage {

    private final UserStatusChangeSource source;
    private boolean authenticated;

    /**
     * Create the message
     *
     * @param source
     *         the source of the change, for example a {@link LoginView} implementation - this allows the message subscriber to respond differently depending
     *         on the source
     * @param authenticated
     *         true of the user has just logged in, false if they have just logged out
     */
    public UserStatusBusMessage(UserStatusChangeSource source, boolean authenticated) {
        this.authenticated = authenticated;
        checkNotNull(source);
        this.source = source;

    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public UserStatusChangeSource getSource() {
        return source;
    }
}
