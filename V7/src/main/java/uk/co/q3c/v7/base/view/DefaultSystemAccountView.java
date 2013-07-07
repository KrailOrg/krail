package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;


public class DefaultSystemAccountView extends DefaultViewBase implements SystemAccountView {

	@Inject

	protected DefaultSystemAccountView(UserNavigationTree navtree) {
		super(navtree);
	}

}
