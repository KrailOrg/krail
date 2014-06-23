package uk.co.q3c.v7.base.ui;

import com.google.inject.Inject;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.view.component.DefaultUserStatusPanel;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NProcessor;
import uk.co.q3c.v7.i18n.Translate;

public class TestUI extends ScopedUI {

	@Inject
	private DefaultUserStatusPanel panel1;

	@Inject
	private DefaultUserStatusPanel panel2;

	@Inject
	public TestUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
			Broadcaster broadcaster, PushMessageRouter pushMessageRouter, ApplicationTitle applicationTitle,
			Translate translate, CurrentLocale currentLocale, I18NProcessor translator) {
		super(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitle, translate,
				currentLocale, translator);

	}

	public DefaultUserStatusPanel getPanel2() {
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

	public DefaultUserStatusPanel getPanel1() {
		return panel1;
	}

	@Override
	protected void processBroadcastMessage(String group, String message) {
		// TODO Auto-generated method stub

	}

}
