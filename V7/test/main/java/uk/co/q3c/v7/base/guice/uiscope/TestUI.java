package uk.co.q3c.v7.base.guice.uiscope;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;
import uk.co.q3c.v7.demo.view.components.InfoBar;

import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;

public class TestUI extends BasicUI {

	@Inject
	private HeaderBar extraHeaderBar;

	@Inject
	protected TestUI(HeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super(headerBar, footerBar, infoBar, navigator, errorHandler, converterFactory);

	}

	public HeaderBar getExtraHeaderBar() {
		return extraHeaderBar;
	}

}
