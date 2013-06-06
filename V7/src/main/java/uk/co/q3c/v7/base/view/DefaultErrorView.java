package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import com.vaadin.ui.Label;

public class DefaultErrorView extends VerticalViewBase implements ErrorView {

	private Label viewLabel;
	
	@Inject
	protected DefaultErrorView() {

		//TODO: document how to replace this implementation with teh user one
		doLayout();

	}

	protected void doLayout() {
		viewLabel = new Label();
		viewLabel.addStyleName("warning");
		addComponent(viewLabel);
	}
	
	@Override
	public void processParams(List<String> params) {
		String s = "This is the ErrorView and would say something like \""
				+ this.getScopedUI().getV7Navigator().getNavigationState() + " is not a valid uri\"";
		viewLabel.setValue(s);
	}

}
