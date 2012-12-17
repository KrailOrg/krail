package uk.co.q3c.basic.view;

import uk.co.q3c.basic.URIFragmentHandler;
import uk.co.q3c.basic.guice.navigate.GuiceView;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * This becomes the site map. Simply map a virtual page (see @link {@link URIFragmentHandler}) to the {@link GuiceView}
 * class which will represent it
 * 
 * @author david
 * 
 */
public class ViewModule extends AbstractModule {

	@Override
	protected void configure() {
		MapBinder<String, GuiceView> mapbinder = MapBinder.newMapBinder(binder(), String.class, GuiceView.class);
		mapbinder.addBinding("").to(HomeView.class);
		mapbinder.addBinding("view1").to(View1.class);
		mapbinder.addBinding("view2").to(View2.class);

		// will be used if a mapping is not found
		bind(ErrorView.class).to(DemoErrorView.class);

	}

}
