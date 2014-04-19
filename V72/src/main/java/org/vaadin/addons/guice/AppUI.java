package org.vaadin.addons.guice;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.ui.ApplicationTitle;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

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
			Broadcaster broadcaster, PushMessageRouter pushMessageRouter,
			@ApplicationTitle I18NKey<?> applicationTitleKey, Translate translate) {
		super(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitleKey, translate);

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
