package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.demo.ui.BasicUI;
import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.DefaultHeaderBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;
import uk.co.q3c.v7.demo.view.components.InfoBar;

import com.vaadin.server.ErrorHandler;

public class TestUI extends BasicUI {

	@Inject
	private HeaderBar extraHeaderBar;

	@Inject
	protected TestUI(DefaultHeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler) {
		super(headerBar, footerBar, infoBar, navigator, errorHandler);

	}

	public HeaderBar getExtraHeaderBar() {
		return extraHeaderBar;
	}

}
