package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountView extends DefaultViewBase implements RequestSystemAccountView {

	@Inject
	protected DefaultRequestSystemAccountView(UserNavigationTree navtree) {
		super(navtree);
	}

}
