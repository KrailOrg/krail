package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import uk.co.q3c.basic.BasicUI;
import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.HeaderBar;
import uk.co.q3c.basic.guice.navigate.ComponentContainerViewDisplay;
import uk.co.q3c.basic.guice.navigate.GuiceNavigator;
import uk.co.q3c.basic.guice.navigate.GuiceViewProvider;

public class TestUI extends BasicUI {

	@Inject
	private HeaderBar extraHeaderBar;

	@Inject
	protected TestUI(HeaderBar headerBar, FooterBar footerBar, String title, GuiceViewProvider viewProvider,
			GuiceNavigator navigator, ComponentContainerViewDisplay display) {
		super(headerBar, footerBar, title, viewProvider, navigator, display);

	}

	public HeaderBar getExtraHeaderBar() {
		return extraHeaderBar;
	}

}
