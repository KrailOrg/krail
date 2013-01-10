package uk.co.q3c.v7.demo.ui;

import uk.co.q3c.v7.base.ui.V7UIModule;

import com.vaadin.server.UIProvider;

public class DemoUIModule extends V7UIModule {

	@Override
	protected void bindUIProvider() {
		bind(UIProvider.class).to(DemoUIProvider.class);
	}

}
