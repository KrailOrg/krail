package uk.co.q3c.v7.base.view;

import java.util.List;

import com.google.inject.Inject;

public class DefaultPrivateHomeView extends StandardPageViewBase implements PrivateHomeView {

	@Inject
	protected DefaultPrivateHomeView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
