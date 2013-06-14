package uk.co.q3c.v7.base.ui;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;

import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

public class TestUI extends ScopedUI {

	@Inject
	private LoginStatusPanel panel1;

	@Inject
	private LoginStatusPanel panel2;

	@Inject
	protected TestUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super(navigator, errorHandler, converterFactory);

	}

	public LoginStatusPanel getPanel2() {
		return panel2;
	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		return new VerticalLayout(getViewDisplayPanel());
	}

	@Override
	protected String pageTitle() {
		return "TestUI";
	}

	public LoginStatusPanel getPanel1() {
		return panel1;
	}

}
