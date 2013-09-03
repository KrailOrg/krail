package com.example.v7demo;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * The UI class used in this demo for the V7 application base
 * 
 * @author David Sowerby
 * 
 */
@Theme("v7demo")
public class DemoUI extends ScopedUI {

	private VerticalLayout layout;

	@Inject
	protected DemoUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super(navigator, errorHandler, converterFactory);
	}

	@Override
	protected String pageTitle() {
		return "V7 Demo";
	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		if (layout == null) {
			Banner banner = new Banner();
			layout = new VerticalLayout(banner, getViewDisplayPanel());
			layout.setSizeFull();
		}
		return layout;
	}

}