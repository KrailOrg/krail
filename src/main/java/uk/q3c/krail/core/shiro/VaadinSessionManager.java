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
package uk.q3c.krail.core.shiro;

import com.google.inject.Inject;
import com.vaadin.server.VaadinSession;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * A {@link SessionManager} implementation that uses the {@link VaadinSession} for the current user to common and locate the Shiro {@link Session}. This
 * tightly ties the Shiro security Session lifecycle to that of the VaadinSession allowing expiration, persistence, and clustering to be handled only in the
 * Vaadin configuration rather than be duplicated in both the Vaadin and Shiro configuration.
 *
 * @author mpilone
 */
public class VaadinSessionManager implements SessionManager {
    /**
     * The session attribute name prefix used for storing the Shiro Session in the VaadinSession.
     */
    public final static String SESSION_ATTRIBUTE_PREFIX = VaadinSessionManager.class.getName() + ".session.";
    private static Logger log = LoggerFactory.getLogger(VaadinSessionManager.class);
    /**
     * The session factory used to create new sessions. In the future, it may make more sense to simply implement a
     * {@link Session} that is a lightweight wrapper on the {@link VaadinSession} rather than storing a
     * {@link SimpleSession} in the {@link VaadinSession}. However by using a SimpleSession, the security information
     * is
     * contained in a neat bucket inside the overall VaadinSession.
     */
    private final SessionFactory sessionFactory;
    private final VaadinSessionProvider sessionProvider;

    /**
     * Constructs the VaadinSessionManager.
     */

    @Inject
    protected VaadinSessionManager(VaadinSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        sessionFactory = new SimpleSessionFactory();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.shiro.session.mgt.SessionManager#start(org.apache.shiro.session .mgt.SessionContext)
     */
    @Override
    public Session start(SessionContext context) {
        log.debug("starting VaadinSessionManager");
        // Retrieve the VaadinSession for the current user.
        VaadinSession vaadinSession = sessionProvider.get();

        // Create a new security session using the session factory.
        SimpleSession shiroSession = (SimpleSession) sessionFactory.createSession(context);

        // Assign a unique ID to the session now because this session manager
        // doesn't use a SessionDAO for persistence as it delegates to any
        // VaadinSession configured persistence.
        shiroSession.setId(UUID.randomUUID()
                               .toString());

        // Put the security session in the VaadinSession. We use the session's ID as
        // part of the key just to be safe so we can double check that the security
        // session matches when it is requested in getSession.
        vaadinSession.setAttribute(SESSION_ATTRIBUTE_PREFIX + shiroSession.getId(), shiroSession);

        return shiroSession;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.shiro.session.mgt.SessionManager#getSession(org.apache.shiro .session.mgt.SessionKey)
     */
    @Override
    public Session getSession(SessionKey key) throws SessionException {

        // Retrieve the VaadinSession for the current user.
        VaadinSession vaadinSession = sessionProvider.get();

        String attributeName = SESSION_ATTRIBUTE_PREFIX + key.getSessionId();

        if (vaadinSession != null) {
            // If we have a valid VaadinSession, try to get the Shiro Session.
            SimpleSession shiroSession = (SimpleSession) vaadinSession.getAttribute(attributeName);

            if (shiroSession != null) {

                // Make sure the Shiro Session hasn't been stopped or expired (i.e. the
                // user logged out).
                if (shiroSession.isValid()) {
                    return shiroSession;
                } else {
                    // This is an invalid or expired session so we'll clean it up.
                    vaadinSession.setAttribute(attributeName, null);
                }
            }
        }

        return null;
    }
}