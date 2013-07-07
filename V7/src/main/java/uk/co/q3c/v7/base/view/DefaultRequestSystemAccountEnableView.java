package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultRequestSystemAccountEnableView extends DefaultViewBase implements RequestSystemAccountEnableView {
	@Inject
	protected DefaultRequestSystemAccountEnableView(UserNavigationTree navtree) {
		super(navtree);
	}

}
