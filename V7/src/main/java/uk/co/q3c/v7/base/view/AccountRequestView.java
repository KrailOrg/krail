package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;


import com.vaadin.ui.Label;

public class AccountRequestView extends VerticalViewBase {

	private final Label infoLabel;

	@Inject
	protected AccountRequestView() {
		super();
		infoLabel = new Label("Account request");
		addComponent(infoLabel);
	}

	@Override
	protected void processParams(List<String> params) {

	}

}
