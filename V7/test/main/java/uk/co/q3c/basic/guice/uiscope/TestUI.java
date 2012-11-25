package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import uk.co.q3c.basic.BasicUI;
import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.HeaderBar;

import com.vaadin.navigator.ViewProvider;

public class TestUI extends BasicUI {

	@Inject
	private HeaderBar extraHeaderBar;

	@Inject
	protected TestUI(HeaderBar headerBar, FooterBar footerBar, String title, ViewProvider viewProvider) {
		super(headerBar, footerBar, title, viewProvider);

	}

	public HeaderBar getExtraHeaderBar() {
		return extraHeaderBar;
	}

}
