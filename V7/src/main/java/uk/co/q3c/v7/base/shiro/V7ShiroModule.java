package uk.co.q3c.v7.base.shiro;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.vaadin.server.ErrorHandler;

public class V7ShiroModule extends AbstractModule {

	@Override
	protected void configure() {

		// error handler for the VaadinSession, needed to handle Shiro exceptions
		bind(ErrorHandler.class).to(V7ErrorHandler.class);

		// should this be in the applicaiton module?
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
	}

}
