package uk.co.q3c.v7.base.shiro;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.web.mgt.WebSecurityManager;

import com.google.inject.binder.AnnotatedBindingBuilder;

public class DefaultShiroWebModule extends ShiroWebModule {

	public DefaultShiroWebModule(ServletContext sc) {
		super(sc);
	}

	@Override
	protected void configureShiroWeb() {
		// bind the authentication realm
		bindShiroRealm();
		bindCredentialsMatcher();
	}
	
	/**
	 * Override this to provide your own Realm
	 * default: <code>bindRealm().to(DefaultRealm.class);</code>
	 */
	protected void bindShiroRealm() {
		bindRealm().to(DefaultRealm.class);
		//this is just an implementatin detail, other realms may not need LoginAttemptLog
		bindLoginAttemptLog();
	}

	private void bindLoginAttemptLog() {
		bind(LoginAttemptLog.class).to(DefaultLoginAttemptLog.class);
	}

	/**
	 * Override this to provide your own CredentialsMatcher
	 * default: <code>bind(CredentialsMatcher.class).to(AlwaysPasswordCredentialsMatcher.class);</code>
	 */
	protected void bindCredentialsMatcher() {
		bind(CredentialsMatcher.class).to(AlwaysPasswordCredentialsMatcher.class);
	}

	@Override
	protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
		try {
			bind.toConstructor(V7SecurityManager.class.getConstructor(Collection.class)).asEagerSingleton();
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException(
					"This really shouldn't happen.  Either something has changed in Shiro, or there's a bug in ShiroModule.",
					e);
		}
	}

}
