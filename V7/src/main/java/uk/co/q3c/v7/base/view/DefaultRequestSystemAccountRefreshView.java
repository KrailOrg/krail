package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.DefaultUserNavigationTree;

@UIScoped
public class DefaultRequestSystemAccountRefreshView extends StandardPageViewBase implements
		RequestSystemAccountRefreshView {

	@Inject
	protected DefaultRequestSystemAccountRefreshView(V7Navigator navigator, DefaultUserNavigationTree navtree) {
		super(navigator, navtree);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
