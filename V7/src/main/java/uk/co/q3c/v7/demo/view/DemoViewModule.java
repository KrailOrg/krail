package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.vaadin.server.UIProvider;

/**
 * This becomes the site map. Simply map a virtual page (see @link {@link URIFragmentHandler}) to the {@link V7View}
 * class which will represent it
 * 
 * @author david
 * 
 */
public class DemoViewModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UIProvider.class).to(DemoUIProvider.class);

		MapBinder<String, V7View> mapbinder = MapBinder.newMapBinder(binder(), String.class, V7View.class);
		mapbinder.addBinding("").to(HomeView.class);
		mapbinder.addBinding("view1").to(View1.class);
		mapbinder.addBinding("view2").to(View2.class);

		// will be used if a mapping is not found
		bind(ErrorView.class).to(DemoErrorView.class);

		// bind your choice of URI handler
		bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);

		// some Strings for the demo
		bind(String.class).annotatedWith(Names.named(A.title)).toInstance("Basic Guice Vaadin Application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Sample app Vaadin 7 Beta 11");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

	}

}
