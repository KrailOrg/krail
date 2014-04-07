package uk.co.q3c.v7.base.view;

import java.util.List;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.google.inject.Inject;

@UIScoped
public class DefaultRequestSystemAccountUnlockView extends StandardPageViewBase implements
		RequestSystemAccountUnlockView {

	@Inject
	protected DefaultRequestSystemAccountUnlockView() {
		super();
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
