package uk.co.q3c.v7.base.view;

import java.util.List;

import com.google.inject.Inject;

public class DefaultRequestSystemAccountResetView extends StandardPageViewBase implements RequestSystemAccountResetView {

	@Inject
	protected DefaultRequestSystemAccountResetView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
