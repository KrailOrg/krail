package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.component.UserNavigationTree;

public class DefaultPublicHomeView extends DefaultViewBase implements PublicHomeView {

	@Inject
	protected DefaultPublicHomeView(UserNavigationTree navtree) {
		super(navtree);
	}

}
