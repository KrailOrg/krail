package uk.co.q3c.v7.testapp;

import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.ui.V7UIModule;

import com.google.inject.multibindings.MapBinder;
import com.vaadin.ui.UI;

public class TestAppUIModule extends V7UIModule {
	@Override
	protected void bindUIProvider() {
		bind(ScopedUIProvider.class).to(TestAppUIProvider.class);
	}

	@Override
	protected void addUIBindings(MapBinder<String, UI> mapbinder) {
		mapbinder.addBinding(TestAppUI.class.getName()).to(TestAppUI.class);
	}

}
