package org.vaadin.addons.guice;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

@Theme("runo")
public class AppUI extends ScopedUI {

	@Inject
	public AppUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
			LoginStatusHandler loginStatusHandler, Broadcaster broadcaster, PushMessageRouter pushMessageRouter) {
		super(navigator, errorHandler, converterFactory, loginStatusHandler, broadcaster, pushMessageRouter);

	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		return new VerticalLayout(getViewDisplayPanel());
	}

	@Override
	protected String pageTitle() {
		return "V72";
	}

}
