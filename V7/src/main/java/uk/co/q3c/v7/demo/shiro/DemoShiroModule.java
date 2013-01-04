package uk.co.q3c.v7.demo.shiro;

import uk.co.q3c.v7.base.shiro.DefaultUnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultUnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.UnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.UnauthorizedExceptionHandler;

import com.google.inject.AbstractModule;

public class DemoShiroModule extends AbstractModule {

	@Override
	protected void configure() {

		// TODO bind defaults in V7ShiroModule if none specified in application module
		// https://github.com/davidsowerby/v7/issues/62
		bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
		bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
	}

}
