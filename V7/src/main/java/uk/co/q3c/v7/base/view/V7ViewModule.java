package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.navigate.V7View;

import com.google.inject.AbstractModule;

public class V7ViewModule extends AbstractModule {

	@Override
	protected void configure() {
		// the fallback in case a View is not defined
		bind(V7View.class).to(ErrorView.class);
	}

}
