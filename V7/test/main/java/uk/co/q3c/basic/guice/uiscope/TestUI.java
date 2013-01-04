package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.demo.ui.BasicUI;
import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

import com.vaadin.server.ErrorHandler;

public class TestUI extends BasicUI {

	@Inject
	private HeaderBar extraHeaderBar;

	@Inject
	protected TestUI(HeaderBar headerBar, FooterBar footerBar, String title, V7Navigator navigator,
			ErrorHandler errorHandler) {
		super(headerBar, footerBar, title, navigator, errorHandler);

	}

	public HeaderBar getExtraHeaderBar() {
		return extraHeaderBar;
	}

}
