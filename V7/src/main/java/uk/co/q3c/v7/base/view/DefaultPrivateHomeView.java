package uk.co.q3c.v7.base.view;

import java.util.List;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.google.inject.Inject;

@UIScoped
public class DefaultPrivateHomeView extends StandardPageViewBase implements PrivateHomeView {

	@Inject
	protected DefaultPrivateHomeView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
