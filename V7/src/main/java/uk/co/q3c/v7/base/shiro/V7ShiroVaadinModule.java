package uk.co.q3c.v7.base.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;

public class V7ShiroVaadinModule extends AbstractModule {

	public V7ShiroVaadinModule() {
		super();
	}

	@Override
	protected void configure() {
		bindErrorHandler();
		bindUnathenticatedHandler();
		bindUnauthorisedHandler();
		bindLoginExceptionsHandler();
	}

	/**
	 * error handler for the VaadinSession, needed to handle Shiro exceptions
	 */
	protected void bindErrorHandler() {
		bind(ErrorHandler.class).to(V7ErrorHandler.class);
	}

	/**
	 * the {@link DefaultErrorHandler} calls this handler in response to an attempted unauthorised action. If you have
	 * defined your own ErrorHandler you may of course do something different
	 */
	protected void bindUnauthorisedHandler() {
		bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
	}

	/**
	 * the {@link DefaultErrorHandler} calls this handler in response to an attempted unauthenticated action. If you
	 * have defined your own ErrorHandler you may of course do something different
	 */
	protected void bindUnathenticatedHandler() {
		bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
	}

	/**
	 * The login process may raise a number of {@link ShiroException}s. This handler is called to manage those
	 * exceptions gracefully.
	 */
	protected void bindLoginExceptionsHandler() {
		bind(LoginExceptionHandler.class).to(DefaultLoginExceptionHandler.class);
	}

	@Provides
	V7SecurityManager providesSecurityManager() {
		return (V7SecurityManager) SecurityUtils.getSecurityManager();
	}

}
