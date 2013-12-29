package uk.co.q3c.v7.testapp;

import com.google.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;

// @Theme("uiTest")
public class TestAppUI extends ScopedUI {

	@Inject
	protected TestAppUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
			LoginStatusHandler loginStatusHandler) {
		super(navigator, errorHandler, converterFactory, loginStatusHandler);
	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		// return null;
		throw new RuntimeException("not yet implemented");
	}

	@Override
	protected String pageTitle() {
		// return null;
		throw new RuntimeException("not yet implemented");
	}

}
