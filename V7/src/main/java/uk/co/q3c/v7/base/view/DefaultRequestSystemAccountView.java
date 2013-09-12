package uk.co.q3c.v7.base.view;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountView extends StandardPageViewBase implements RequestSystemAccountView {

	@Inject
	protected DefaultRequestSystemAccountView(V7Navigator navigator, UserNavigationTree navtree) {
		super(navigator, navtree);
	}

	@Override
	protected void processParams(Map<String, String> params) {
	}

}
