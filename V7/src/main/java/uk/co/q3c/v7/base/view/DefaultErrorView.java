package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import com.vaadin.ui.Label;

public class DefaultErrorView extends VerticalViewBase implements ErrorView {

	private final Label viewLabel;

	@Inject
	protected DefaultErrorView() {
		super();
		viewLabel = new Label();
		getViewLabel().addStyleName("warning");

	}

	@Override
	public void processParams(List<String> params) {
		String s = "This is the ErrorView and would say something like \""
				+ this.getScopedUI().getV7Navigator().getNavigationState() + " is not a valid uri\"";
		getViewLabel().setValue(s);
	}

	public Label getViewLabel() {
		return viewLabel;
	}

}
