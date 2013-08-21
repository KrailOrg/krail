package uk.co.q3c.v7.base.shiro;

import java.util.UUID;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;

/**
 * A {@link SessionManager} implementation that uses the {@link VaadinSession} for the current user to persist and
 * locate the Shiro {@link Session}. This
 * tightly ties the Shiro security Session lifecycle to that of the
 * VaadinSession allowing expiration, persistence, and clustering to be handled
 * only in the Vaadin configuration rather than be duplicated in both the Vaadin
 * and Shiro configuration.
 * 
 * @author mpilone
 * 
 */
public class VaadinSessionManager implements SessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(VaadinSessionManager.class);

    /**
     * The session attribute name prefix used for storing the Shiro Session in the
     * VaadinSession.
     */
    private final static String SESSION_ATTRIBUTE_PREFIX = VaadinSessionManager.class.getName() + ".session.";

    /**
     * The session factory used to create new sessions. In the future, it may make
     * more sense to simply implement a {@link Session} that is a lightweight
     * wrapper on the {@link VaadinSession} rather than storing a {@link SimpleSession} in the {@link VaadinSession}.
     * However by using a
     * SimpleSession, the security information is contained in a neat bucket
     * inside the overall VaadinSession.
     */
    private SessionFactory sessionFactory;

    /**
     * Constructs the VaadinSessionManager.
     */
    public VaadinSessionManager() {
        sessionFactory = new SimpleSessionFactory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.shiro.session.mgt.SessionManager#start(org.apache.shiro.session
     * .mgt.SessionContext)
     */
    @Override
    public Session start(SessionContext context) {

        LOG.debug("starting Shiro session");

        // Retrieve the VaadinSession for the current user.
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        // Assuming security is used within a Vaadin application, there should
        // always be a VaadinSession available.
        if (vaadinSession == null) {
            throw new IllegalStateException("Unable to locate VaadinSession to store Shiro Session.");
        }

        // Create a new security session using the session factory.
        SimpleSession shiroSession = (SimpleSession) sessionFactory.createSession(context);

        // Assign a unique ID to the session now because this session manager
        // doesn't use a SessionDAO for persistence as it delegates to any
        // VaadinSession configured persistence.
        shiroSession.setId(UUID.randomUUID().toString());

        // Put the security session in the VaadinSession. We use the session's ID as
        // part of the key just to be safe so we can double check that the security
        // session matches when it is requested in getSession.
        vaadinSession.setAttribute(SESSION_ATTRIBUTE_PREFIX + shiroSession.getId(), shiroSession);

        return shiroSession;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.shiro.session.mgt.SessionManager#getSession(org.apache.shiro
     * .session.mgt.SessionKey)
     */
    @Override
    public Session getSession(SessionKey key) throws SessionException {

        LOG.debug("getting Shiro session");

        // Retrieve the VaadinSession for the current user.
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        String attributeName = SESSION_ATTRIBUTE_PREFIX + key.getSessionId();

        if (vaadinSession != null) {
            // If we have a valid VaadinSession, try to get the Shiro Session.
            SimpleSession shiroSession = (SimpleSession) vaadinSession.getAttribute(attributeName);
            if (shiroSession != null) {
                LOG.debug("Shiro session found");
                // Make sure the Shiro Session hasn't been stopped or expired (i.e. the
                // user logged out).
                if (shiroSession.isValid()) {
                    LOG.warn("Shiro session valid");
                    return shiroSession;
                }
                else {
                    LOG.debug("Shiro session invalid");
                    // This is an invalid or expired session so we'll clean it up.
                    vaadinSession.setAttribute(attributeName, null);
                }
            }
            else {
                LOG.debug("Shiro session not found");
            }
        }
        LOG.debug("Vaadin session not found");
        return null;
    }
}