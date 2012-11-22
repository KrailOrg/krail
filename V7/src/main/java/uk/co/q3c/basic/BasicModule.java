package uk.co.q3c.basic;

import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.UI;

public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BasicServlet.class);

		bind(String.class).annotatedWith(Names.named(A.title)).toInstance("Basic Guice Vaadin Application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("<b>Sample app Vaadin 7 Beta 9</b>");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

		bind(ViewProvider.class).to(GuiceViewProvider.class);
		bind(URIDecoder.class).to(StrictURIDecoder.class);

		// this is needed for testing, even though not required otherwise. Don't know why.
		// If you change this, change the provides method as well
		bind(UI.class).to(BasicUI.class);
	}

	/**
	 * Needed for the application but not for testing. If you change the class this returns, change the binding as well.
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	@Provides
	private Class<? extends UI> provideUIClass() {
		return BasicUI.class;
	}

}