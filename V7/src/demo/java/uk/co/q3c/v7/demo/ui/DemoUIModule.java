package uk.co.q3c.v7.demo.ui;

import uk.co.q3c.v7.base.data.BaseConverterFactory;
import uk.co.q3c.v7.base.ui.BasicUIModule;
import uk.co.q3c.v7.demo.data.DemoConverterFactory;

import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class DemoUIModule extends BasicUIModule {

	@Override
	protected void bindUIProvider() {
		bind(UIProvider.class).to(DemoUIProvider.class);
	}

	@Override
	protected void addUIBindings(MapBinder<String, UI> mapbinder) {
		mapbinder.addBinding(DemoUI.class.getName()).to(DemoUI.class);
		mapbinder.addBinding(SideBarUI.class.getName()).to(SideBarUI.class);
	}

	@Override
	protected void bindConverterFactory() {
		bind(ConverterFactory.class).to(DemoConverterFactory.class);
	}
}
