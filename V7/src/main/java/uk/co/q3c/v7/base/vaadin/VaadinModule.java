package uk.co.q3c.v7.base.vaadin;

import uk.co.q3c.v7.base.shiro.DefaultVaadinSessionProvider;
import uk.co.q3c.v7.base.shiro.VaadinSessionProvider;

import com.google.inject.AbstractModule;

public class VaadinModule extends AbstractModule {

	@Override
	protected void configure() {
		bindVaadinSessionProvider();
	}

	/**
	 * Override this to use a different implementation for a VaadinSessionProvider
	 */
	protected void bindVaadinSessionProvider() {
		bind(VaadinSessionProvider.class).to(DefaultVaadinSessionProvider.class);
	}
}
