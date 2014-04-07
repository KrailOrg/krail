package uk.co.q3c.v7.base.view;

import java.util.List;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.google.inject.Inject;

@UIScoped
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
