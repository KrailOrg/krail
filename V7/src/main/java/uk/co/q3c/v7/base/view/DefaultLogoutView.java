package uk.co.q3c.v7.base.view;

import java.util.List;

import uk.co.q3c.v7.base.navigate.V7ViewChangeEvent;
import uk.co.q3c.v7.base.navigate.VerticalViewBase;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

public class DefaultLogoutView extends VerticalViewBase implements LogoutView {

	@Override
	public void enter(V7ViewChangeEvent event) {

	}

	@Override
	public Component getUiComponent() {
		Panel p = new Panel("Logged out");
		p.setSizeFull();
		addComponent(p);
		return this;

	}

	@Override
	protected void processParams(List<String> params) {

	}

}
