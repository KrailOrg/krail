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

import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.user.UserStatusChangeSource;

/**
 * The main purpose of this implementation is to act as a router for changes in a user's login status, and is expected
 * to be {@link VaadinSessionScoped}. In addition to registered listeners, the {@link Navigator} is also advised of
 * the status change (the navigator should not be added as a listener - it is called explicitly, as it has to be the
 * last one to be called so that navigation components are up to date when the call is made)
 *
 * @author David Sowerby
 * @date 18 Apr 2014
 */
public interface UserStatus {

    /**
     * Respond to a change of user status (logged in / logged out)
     *
     * @param source
     *         the source of the changed status, needed because some listeners may want to respond differently depending on the source
     */
    public void statusChanged(UserStatusChangeSource source);

    /**
     * Remove a listener
     *
     * @param listener the listener to remove
     */
    public abstract void removeListener(UserStatusListener listener);

    /**
     * Adds a listener
     *
     * @param listener
     *         the listener to add
     */
    public abstract void addListener(UserStatusListener listener);

    /**
     * Returns true if the current user is authenticated
     *
     * @return true if the current user is authenticated
     */
    boolean isAuthenticated();
}
