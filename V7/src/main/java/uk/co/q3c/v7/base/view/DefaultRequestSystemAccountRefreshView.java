package uk.co.q3c.v7.base.view;

import java.util.List;

import com.google.inject.Inject;

public class DefaultRequestSystemAccountRefreshView extends StandardPageViewBase implements
		RequestSystemAccountRefreshView {

	@Inject
	protected DefaultRequestSystemAccountRefreshView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
