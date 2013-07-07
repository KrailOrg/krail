package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountRefreshView extends DefaultViewBase implements RequestSystemAccountRefreshView {

	@Inject
	protected DefaultRequestSystemAccountRefreshView(V7Navigator navigator, UserNavigationTree navtree) {
		super(navigator, navtree);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
