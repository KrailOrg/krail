package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;

import com.vaadin.ui.Label;

public class DefaultRequestSystemAccountView extends VerticalViewBase implements
		RequestSystemAccountView {

	private final Label infoLabel;

	@Inject
	protected DefaultRequestSystemAccountView(V7Navigator navigator) {
		super(navigator);
		infoLabel = new Label("Account request");
		addComponent(infoLabel);
	}

	@Override
	protected void processParams(List<String> params) {

	}

}
