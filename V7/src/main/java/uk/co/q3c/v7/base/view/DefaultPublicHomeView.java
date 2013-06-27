package uk.co.q3c.v7.base.view;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public class DefaultPublicHomeView implements PublicHomeView {

	private final Label label;

	public DefaultPublicHomeView() {
		super();
		this.label = new Label("This is home");
	}

	@Override
	public void enter(V7ViewChangeEvent event) {

	}

	@Override
	public Component getUiComponent() {
		return label;
	}

}
