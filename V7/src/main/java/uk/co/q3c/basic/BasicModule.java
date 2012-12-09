package uk.co.q3c.basic;

import uk.co.q3c.basic.view.DefaultGuiceViewProvider;
import uk.co.q3c.basic.view.DemoErrorView;
import uk.co.q3c.basic.view.ErrorView;
import uk.co.q3c.basic.view.GuiceViewProvider;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.vaadin.navigator.NavigationStateManager;

public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BasicServlet.class);

		bind(String.class).annotatedWith(Names.named(A.title)).toInstance("Basic Guice Vaadin Application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Sample app Vaadin 7 Beta 10");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

		bind(GuiceViewProvider.class).to(DefaultGuiceViewProvider.class);
		bind(DefaultGuiceNavigator.class);
		bind(URIDecoder.class).to(StrictURIDecoder.class);
		bind(NavigationStateManager.class).to(DefaultNavigationStateManager.class);
		bind(ErrorView.class).to(DemoErrorView.class);

	}

}