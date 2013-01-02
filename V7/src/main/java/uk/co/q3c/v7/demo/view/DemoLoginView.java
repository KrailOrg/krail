package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.base.navigate.VerticalViewBase;
import uk.co.q3c.v7.base.view.LoginView;

import com.vaadin.ui.Label;

public class DemoLoginView extends VerticalViewBase implements LoginView {
	private final Label label;

	protected DemoLoginView() {
		super();
		label = new Label("this would be the login");
		this.addComponent(label);
	}

}
