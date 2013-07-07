package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountUnlockView extends DefaultViewBase implements RequestSystemAccountUnlockView {

	@Inject
	protected DefaultRequestSystemAccountUnlockView(UserNavigationTree navtree) {
		super(navtree);
	}

}
