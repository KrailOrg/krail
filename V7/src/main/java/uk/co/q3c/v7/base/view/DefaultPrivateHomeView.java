package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultPrivateHomeView extends DefaultViewBase implements PrivateHomeView {

	@Inject
	protected DefaultPrivateHomeView(UserNavigationTree navtree) {
		super(navtree);
	}

}
