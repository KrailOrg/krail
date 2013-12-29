package uk.co.q3c.v7.base.view;

import java.util.List;

import com.google.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.DefaultUserNavigationTree;

@UIScoped
public class DefaultPublicHomeView extends StandardPageViewBase implements PublicHomeView {

	@Inject
	protected DefaultPublicHomeView(V7Navigator navigator, DefaultUserNavigationTree navtree) {
		super(navigator);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
