package uk.co.q3c.v7.base.view;

import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;

@UIScoped
public class DefaultPublicHomeView extends StandardPageViewBase implements PublicHomeView {

	@Inject
	protected DefaultPublicHomeView(V7Navigator navigator) {
		super(navigator);
	}

	@Override
	protected void processParams(Map<String, String> params) {
	}

}
