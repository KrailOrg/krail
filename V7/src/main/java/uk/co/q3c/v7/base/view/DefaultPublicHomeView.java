package uk.co.q3c.v7.base.view;

import java.util.List;

import uk.co.q3c.v7.base.view.component.DefaultUserNavigationTree;

import com.google.inject.Inject;

public class DefaultPublicHomeView extends StandardPageViewBase implements PublicHomeView {

	@Inject
	protected DefaultPublicHomeView(DefaultUserNavigationTree navtree) {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
