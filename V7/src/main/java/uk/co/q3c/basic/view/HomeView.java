package uk.co.q3c.basic.view;

import javax.inject.Inject;

import uk.co.q3c.basic.FooterBar;

public class HomeView extends DemoViewBase {

	@Inject
	protected HomeView(FooterBar footerBar) {
		super(footerBar);
		addNavButton("view 1", "view1");
		addNavButton("view 1 with parameters", "view1/id=22");
		addNavButton("view 2", "view2");
		addNavButton("view 2 with parameters", "view2/id=22");
		addNavButton("invalid uri", "view3/id=22");
	}

}
