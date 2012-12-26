package uk.co.q3c.basic.demo;

import javax.inject.Inject;


public class HomeView extends DemoViewBase {

	@Inject
	protected HomeView(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 1", "view1");
		addNavButton("view 1 with parameters", "view1/id=22");
		addNavButton("view 2", "view2");
		addNavButton("view 2 with parameters", "view2/id=22");
		addNavButton("invalid uri", "view3/id=22");
	}

	@Override
	public int getColourIndex() {
		return 0;
	}

}
