package uk.co.q3c.v7.base.guice;

import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.demo.view.ErrorView;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;

public class BaseModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BaseServlet.class);

		bind(V7View.class).to(ErrorView.class); // the fallback

		bind(String.class).annotatedWith(Names.named(A.title)).toInstance("Basic Guice Vaadin Application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Sample app Vaadin 7 Beta 11");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

		bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);

		bind(V7Navigator.class).to(DefaultV7Navigator.class);

	}
}