package uk.co.q3c.basic.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.URIDecoder;

import com.vaadin.ui.Button;

public class ErrorView extends DemoViewBase {

	@Inject
	protected ErrorView(URIDecoder uriDecoder, FooterBar footerBar) {
		super(uriDecoder, footerBar);
		Button button = addNavButton("take me home", "");
		button.addStyleName("big default");
		getViewLabel().addStyleName("warning");

	}

	@Override
	public void processParams(List<String> params) {
		String s = "This is the ErrorView and would say something like \"" + this.getUI().getNavigator().getState()
				+ " is not a valid uri\"";
		getViewLabel().setValue(s);
	}
}
