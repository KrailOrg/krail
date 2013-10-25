package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;

@UIScoped
public class DefaultSystemAccountView extends StandardPageViewBase implements SystemAccountView {

	@Inject
	protected DefaultSystemAccountView(V7Navigator navigator) {
		super(navigator);
	}

}
