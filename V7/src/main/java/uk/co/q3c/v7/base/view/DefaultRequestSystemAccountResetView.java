package uk.co.q3c.v7.base.view;

import java.util.List;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.google.inject.Inject;

@UIScoped
public class DefaultRequestSystemAccountResetView extends StandardPageViewBase implements RequestSystemAccountResetView {

	@Inject
	protected DefaultRequestSystemAccountResetView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
