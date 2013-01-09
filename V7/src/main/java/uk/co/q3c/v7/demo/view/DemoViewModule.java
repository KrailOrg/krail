package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.V7ViewModule;

import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

/**
 * This becomes the site map. Simply map a virtual page (see @link {@link URIFragmentHandler}) to the {@link V7View}
 * class which will represent it
 * 
 * @author david
 * 
 */
public class DemoViewModule extends V7ViewModule {

	@Override
	protected void configure() {
		super.configure();

		// some Strings for the demo
		bind(String.class).annotatedWith(Names.named(A.title)).toInstance(
				"Guice Vaadin and Shiro demonstration application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Vaadin 7 Beta 11");
		bind(String.class).annotatedWith(Names.named(A.baseUri)).toInstance("http://example.com");

	}

	@Override
	protected void bindViews(MapBinder<String, V7View> mapbinder) {
		mapbinder.addBinding("").to(HomeView.class);
		mapbinder.addBinding("secure/view1").to(View1.class);
		mapbinder.addBinding("public/view2").to(View2.class);
		mapbinder.addBinding("login").to(LoginView.class);
	}

}
