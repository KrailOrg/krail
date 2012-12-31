package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.base.navigate.ErrorView;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.server.UIProvider;

/**
 * This becomes the site map. Simply map a virtual page (see @link {@link URIFragmentHandler}) to the {@link V7View}
 * class which will represent it
 * 
 * @author david
 * 
 */
public class ViewModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UIProvider.class).to(DemoUIProvider.class);

		MapBinder<String, V7View> mapbinder = MapBinder.newMapBinder(binder(), String.class, V7View.class);
		mapbinder.addBinding("").to(HomeView.class);
		mapbinder.addBinding("view1").to(View1.class);
		mapbinder.addBinding("view2").to(View2.class);

		// will be used if a mapping is not found
		bind(ErrorView.class).to(DemoErrorView.class);

	}

}
