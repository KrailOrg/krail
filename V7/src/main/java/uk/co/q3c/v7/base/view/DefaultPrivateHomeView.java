package uk.co.q3c.v7.base.view;

import java.util.List;

import com.google.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;

@UIScoped
public class DefaultPrivateHomeView extends StandardPageViewBase implements PrivateHomeView {

	@Inject
	protected DefaultPrivateHomeView(V7Navigator navigator) {
		super(navigator);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
