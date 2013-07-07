package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountResetView extends DefaultViewBase implements RequestSystemAccountResetView {

	@Inject
	protected DefaultRequestSystemAccountResetView(UserNavigationTree navtree) {
		super(navtree);
	}

}
