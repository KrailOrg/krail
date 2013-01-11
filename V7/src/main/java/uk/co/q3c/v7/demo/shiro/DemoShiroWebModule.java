package uk.co.q3c.v7.demo.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

import uk.co.q3c.v7.base.shiro.DefaultUnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.DefaultUnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.UnauthenticatedExceptionHandler;
import uk.co.q3c.v7.base.shiro.UnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.V7ErrorHandler;

import com.google.inject.name.Names;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;

public class DemoShiroWebModule extends ShiroWebModule {

	public DemoShiroWebModule(ServletContext sc) {
		super(sc);
	}

	@Override
	protected void configureShiroWeb() {
		// bind the authentication realm
		bindRealm().to(DemoRealm.class);
		bindErrorHandler();
		bindUnathenticatedHandler();
		bindUnauthorisedHandler();
	}

	/**
	 * error handler for the VaadinSession, needed to handle Shiro exceptions
	 */
	protected void bindErrorHandler() {
		bind(ErrorHandler.class).to(V7ErrorHandler.class);
		expose(ErrorHandler.class);
	}

	/**
	 * the {@link DefaultErrorHandler} calls this handler in response to an attempted unauthorised action. If you have
	 * defined your own ErrorHandler you may of course do something different
	 */
	protected void bindUnauthorisedHandler() {
		bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
		expose(UnauthorizedExceptionHandler.class);
	}

	/**
	 * the {@link DefaultErrorHandler} calls this handler in response to an attempted unauthenticated action. If you
	 * have defined your own ErrorHandler you may of course do something different
	 */
	protected void bindUnathenticatedHandler() {
		bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
		expose(UnauthenticatedExceptionHandler.class);
	}

	/**
	 * Sets the Shiro globalSessionTimeout property
	 */
	protected void bindTimeout() {
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
	}

}
