package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.util.A;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Some application specific String definitions
 * 
 * @author david
 * 
 */
public class DemoModule extends AbstractModule {

	public DemoModule() {
		super();
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named(A.title)).toInstance(
				"Guice, Vaadin and Shiro demonstration application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Vaadin 7.0.5");

	}

}
