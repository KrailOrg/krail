package uk.co.q3c.v7.base.view;

import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;

public class DefaultRootView extends StandardPageViewBase implements RootView {

	@Inject
	protected DefaultRootView(V7Navigator navigator) {
		super(navigator);
	}

	@Override
	protected void processParams(Map<String, String> params) {
	}

}