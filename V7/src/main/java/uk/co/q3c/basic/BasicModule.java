package uk.co.q3c.basic;

import uk.co.q3c.basic.guice.navigate.DefaultGuiceNavigator;
import uk.co.q3c.basic.guice.navigate.DefaultGuiceViewProvider;
import uk.co.q3c.basic.guice.navigate.GuiceNavigator;
import uk.co.q3c.basic.guice.navigate.GuiceViewProvider;
import uk.co.q3c.basic.view.DemoErrorView;
import uk.co.q3c.basic.view.ErrorView;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;

public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BasicServlet.class);

		bind(String.class).annotatedWith(Names.named(A.title)).toInstance("Basic Guice Vaadin Application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Sample app Vaadin 7 Beta 10");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

		bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
		bind(ErrorView.class).to(DemoErrorView.class);

		bind(GuiceViewProvider.class).to(DefaultGuiceViewProvider.class);
		bind(GuiceNavigator.class).to(DefaultGuiceNavigator.class);
	}
}