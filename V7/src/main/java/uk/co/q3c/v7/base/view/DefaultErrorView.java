package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultErrorView extends DefaultViewBase implements ErrorView {

	@Inject
	protected DefaultErrorView(V7Navigator navigator, UserNavigationTree navtree) {
		super(navigator, navtree);

	}

	@Override
	public void processParams(List<String> params) {
		String s = "\"" + getNavigator().getNavigationState() + "\" is not a valid URI";
		getLabel().setValue(s);
	}

	@Override
	protected void buildUI() {
		super.buildUI();
		getLabel().addStyleName("warning");
	}

}
