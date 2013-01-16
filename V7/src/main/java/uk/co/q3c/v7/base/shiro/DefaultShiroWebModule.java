package uk.co.q3c.v7.base.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.guice.web.ShiroWebModule;

public class DefaultShiroWebModule extends ShiroWebModule {

	public DefaultShiroWebModule(ServletContext sc) {
		super(sc);
	}

	@Override
	protected void configureShiroWeb() {
		// bind the authentication realm
		bindRealm().to(DefaultRealm.class);
		bindCredentials();
		bindLoginAttemptLog();
	}

	protected void bindLoginAttemptLog() {
		bind(LoginAttemptLog.class).to(DefaultLoginAttemptLog.class);
	}

	protected void bindCredentials() {
		bind(CredentialsMatcher.class).to(AlwaysPasswordCredentialsMatcher.class);
	}

}
