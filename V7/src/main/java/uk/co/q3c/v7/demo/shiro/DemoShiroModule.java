package uk.co.q3c.v7.demo.shiro;

import uk.co.q3c.v7.base.shiro.AuthenticationExceptionHandler;
import uk.co.q3c.v7.base.shiro.AuthorizationExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultAuthenticationExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultAuthorizationExceptionHandler;

import com.google.inject.AbstractModule;

public class DemoShiroModule extends AbstractModule {

	@Override
	protected void configure() {
		// bind handlers for authorisation and authentication failures
		bind(AuthenticationExceptionHandler.class).to(DefaultAuthenticationExceptionHandler.class);
		bind(AuthorizationExceptionHandler.class).to(DefaultAuthorizationExceptionHandler.class);
	}

}
