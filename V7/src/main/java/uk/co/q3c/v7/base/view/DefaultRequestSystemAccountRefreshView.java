package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountRefreshView extends DefaultViewBase implements RequestSystemAccountRefreshView {

	@Inject
	protected DefaultRequestSystemAccountRefreshView(UserNavigationTree navtree) {
		super(navtree);
	}

}
