package uk.co.q3c.v7.demo.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

import uk.co.q3c.v7.base.shiro.AuthenticationExceptionHandler;
import uk.co.q3c.v7.base.shiro.AuthorizationExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultAuthenticationExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultAuthorizationExceptionHandler;

public class DemoShiroModule extends ShiroWebModule {

	public DemoShiroModule(ServletContext sc) {
		super(sc);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		// bind handlers for authorisation and authentication failures
		bind(AuthenticationExceptionHandler.class).to(DefaultAuthenticationExceptionHandler.class);
		bind(AuthorizationExceptionHandler.class).to(DefaultAuthorizationExceptionHandler.class);

		// bind the authentication realm
		bindRealm().to(DemoRealm.class);
		addFilterChain("/#public/**", ANON);
		addFilterChain("/#secure/**", AUTHC);

	}

}
