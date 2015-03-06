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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.UserStatusChangeSource;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The type Default user status.
 */
@VaadinSessionScoped
public class DefaultUserStatus implements UserStatus {

    private static Logger log = LoggerFactory.getLogger(DefaultUserStatus.class);
    private final List<UserStatusListener> listeners;
    private final List<Navigator> navigators;
    private SubjectProvider subjectProvider;

    /**
     * Instantiates a new Default user status.
     *
     * @param subjectProvider
     *         the subject provider
     */
    @Inject
    protected DefaultUserStatus(SubjectProvider subjectProvider) {
        super();
        this.subjectProvider = subjectProvider;
        // TODO use weak references?  listeners could disappear, as they are of narrower scope
        listeners = new LinkedList<>();
        navigators = new LinkedList<>();
    }

    /**
     * Holds Navigators separately to other listeners, as navigators need to be fired last
     *
     * @param listener the listener to add
     */
    @Override
    public void addListener(@Nonnull UserStatusListener listener) {
        checkNotNull(listener);
        if (listener instanceof Navigator) {
            log.debug("added a {} listener", listener.getClass()
                                                     .getSimpleName());
            navigators.add((Navigator) listener);
        } else {
            listeners.add(listener);
            log.debug("added a {} listener", listener.getClass()
                                                     .getSimpleName());
        }
    }


    @Override
    public void statusChanged(UserStatusChangeSource source) {
        fireListeners(isAuthenticated(), source);
    }

    /**
     * Fires the listeners
     *
     * @param loggedIn if true, invoke the listeners' userHasLoggedIn methods, otherwise invoke userHasLoggedOut
     * @param source the source
     */
    protected void fireListeners(boolean loggedIn, @Nonnull UserStatusChangeSource source) {
        checkNotNull(source);
        if (loggedIn) {
            log.debug("firing user status change listeners, user has logged in");
            listeners.forEach(listener -> listener.userHasLoggedIn(source));

            // do navigators last
            log.debug("user status change listeners have been fired, now fire the navigators");
            navigators.forEach(listener -> listener.userHasLoggedIn(source));
        } else {
            log.debug("firing user status change listeners, user has logged out");
            listeners.forEach(listener -> listener.userHasLoggedOut(source));

            // do navigators last
            log.debug("user status change listeners have been fired, now fire the navigators");
            navigators.forEach(listener -> listener.userHasLoggedOut(source));
        }
    }


    @Override
    public boolean isAuthenticated() {
        return subjectProvider.get()
                              .isAuthenticated();
    }


    @Override
    public void removeListener(@Nonnull UserStatusListener listener) {
        checkNotNull(listener);
        listeners.remove(listener);
    }
}