package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import uk.co.q3c.basic.BasicUI;
import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.HeaderBar;
import uk.co.q3c.basic.guice.navigate.GuiceNavigator;

public class TestUI extends BasicUI {

	@Inject
	private HeaderBar extraHeaderBar;

	@Inject
	protected TestUI(HeaderBar headerBar, FooterBar footerBar, String title, GuiceNavigator navigator) {
		super(headerBar, footerBar, title, navigator);

	}

	public HeaderBar getExtraHeaderBar() {
		return extraHeaderBar;
	}

}
