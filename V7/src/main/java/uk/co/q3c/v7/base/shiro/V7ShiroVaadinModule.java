package uk.co.q3c.v7.base.shiro;


import com.google.inject.AbstractModule;
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

}
