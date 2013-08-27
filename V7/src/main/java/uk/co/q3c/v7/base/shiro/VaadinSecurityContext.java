package uk.co.q3c.v7.base.shiro;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.vaadin.server.VaadinSession;

/**
 * A {@link SecurityContext} implementation that uses the {@link VaadinSession}
 * to store the {@link Subject} for the current user. This allows the Subject to
 * be discovered even in a push environment where {@link SecurityUtils} can't be
 * used because the server side thread may be suspended and resumed at any time
 * on different threads.
 * 
 * @author mpilone
 */

@Singleton
public class VaadinSecurityContext implements Provider<Subject> {

	private static final Logger LOG = LoggerFactory
			.getLogger(VaadinSecurityContext.class);

	/**
	 * The security manager for the application.
	 */
	private V7SecurityManager securityManager;

	/**
	 * Sets the security manager for the application. To support push, normally
	 * a {@link DefaultSecurityManager} is used rather than a web specific one
	 * because the normal HTTP request/response cycle isn't used.
	 * 
	 * @param securityManager
	 *            the security manager to set
	 */
	@Inject
	public void setSecurityManager(V7SecurityManager securityManager) {
		LOG.debug("setting securityManager");
		this.securityManager = securityManager;
	}

	/**
	 * @see V7SecurityManager#getSubject()
	 */
	public Subject getSubject() {
		return securityManager.getSubject();
	}

	@Override
	public Subject get() {
		return getSubject();
	}

}