package uk.co.q3c.v7.demo;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class DemoModule extends AbstractModule {
	public DemoModule() {
		super();
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("title")).toInstance(
				"Guice, Vaadin and Shiro demonstration application");
		bind(String.class).annotatedWith(Names.named("vaadin version"))
				.toInstance("Vaadin 7.0.5");

	}
}
