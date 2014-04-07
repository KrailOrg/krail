package org.vaadin.addons.guice.ui;

import org.vaadin.addons.guice.AppUI;

import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.ui.V7UIModule;

import com.google.inject.multibindings.MapBinder;
import com.vaadin.ui.UI;

public class AppUIModule extends V7UIModule {

	@Override
	protected void addUIBindings(MapBinder<String, UI> mapbinder) {
		mapbinder.addBinding(AppUI.class.getName()).to(AppUI.class);

	}

	@Override
	protected void bindUIProvider() {
		bind(ScopedUIProvider.class).to(AppUIProvider.class);

	}

}
