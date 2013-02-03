package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.components.FooterBar;
import uk.co.q3c.v7.base.view.components.HeaderBar;
import uk.co.q3c.v7.demo.view.DemoViewBase;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ChameleonTheme;

public class DefaultErrorView extends DemoViewBase implements ErrorView {

	@Inject
	protected DefaultErrorView(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		Button button = addNavButton("take me home", home);
		button.addStyleName(ChameleonTheme.BUTTON_TALL);
		getViewLabel().addStyleName("warning");

	}

	@Override
	public void processParams(List<String> params) {
		String s = "This is the ErrorView and would say something like \""
				+ this.getScopedUI().getV7Navigator().getNavigationState() + " is not a valid uri\"";
		getViewLabel().setValue(s);
	}

	@Override
	public int getColourIndex() {
		return 3;
	}
}
