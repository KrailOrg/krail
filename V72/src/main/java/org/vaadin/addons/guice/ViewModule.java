package org.vaadin.addons.guice;

import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.LabelKey;

import com.google.inject.multibindings.MapBinder;

public class ViewModule extends DirectSitemapModule {

	protected MapBinder<String, V7View> mapbinder;

	//
	// @Override
	// protected void configure() {
	//
	// mapbinder = MapBinder.newMapBinder(binder(), String.class, V7View.class);
	//
	// addBinding("A", ViewA.class);
	// addBinding("B", ViewB.class);
	//
	// }
	//
	// protected void addBinding(String uriFragment, Class<? extends V7View> clazz) {
	// mapbinder.addBinding(uriFragment).to(clazz).in(UIScoped.class);
	// }

	@Override
	protected void define() {
		addEntry("A", ViewA.class, LabelKey.Yes, PageAccessControl.PUBLIC);
		addEntry("B", ViewB.class, LabelKey.No, PageAccessControl.PUBLIC);
		addEntry("widgetset", WidgetsetView.class, LabelKey.Message_Box, PageAccessControl.PUBLIC);

	}

}