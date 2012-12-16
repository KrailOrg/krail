package uk.co.q3c.basic.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.HeaderBar;

import com.vaadin.ui.Button;

public class DemoErrorView extends DemoViewBase implements ErrorView {

	@Inject
	protected DemoErrorView(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		Button button = addNavButton("take me home", "");
		button.addStyleName("big default");
		getViewLabel().addStyleName("warning");

	}

	@Override
	public void processParams(List<String> params) {
		String s = "This is the ErrorView and would say something like \""
				+ this.getScopedUI().getGuiceNavigator().getNavigationState() + " is not a valid uri\"";
		getViewLabel().setValue(s);
	}

	@Override
	public int getColourIndex() {
		return 3;
	}
}
