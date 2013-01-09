package uk.co.q3c.basic;

import uk.co.q3c.v7.base.ui.BasicUI;

import com.google.inject.servlet.ServletModule;
import com.vaadin.ui.UI;

public class TestModule extends ServletModule {
	@Override
	protected void configureServlets() {

		bind(UI.class).to(BasicUI.class);

	}
}
