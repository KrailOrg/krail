package uk.co.q3c.basic;

import uk.co.q3c.basic.view.GuiceViewProvider;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.vaadin.navigator.ViewProvider;

public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BasicServlet.class);

		bind(String.class).annotatedWith(Names.named(A.title)).toInstance("Basic Guice Vaadin Application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("<b>Sample app Vaadin 7 Beta 9</b>");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

		bind(ViewProvider.class).to(GuiceViewProvider.class);
		bind(URIDecoder.class).to(StrictURIDecoder.class);

	}

}