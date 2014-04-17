package uk.co.q3c.v7.base.view;

import java.util.List;

import com.google.inject.Inject;

public class DefaultSystemAccountView extends StandardPageViewBase implements SystemAccountView {

	@Inject
	protected DefaultSystemAccountView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
