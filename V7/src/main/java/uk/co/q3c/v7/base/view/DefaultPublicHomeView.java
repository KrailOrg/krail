package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.DefaultUserNavigationTree;

public class DefaultPublicHomeView extends StandardPageViewBase implements PublicHomeView {

	@Inject
	protected DefaultPublicHomeView(V7Navigator navigator, DefaultUserNavigationTree navtree) {
		super(navigator, navtree);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
