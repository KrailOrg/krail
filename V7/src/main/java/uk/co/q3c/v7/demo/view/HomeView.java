package uk.co.q3c.v7.demo.view;

import javax.inject.Inject;

import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

public class HomeView extends DemoViewBase {

	@Inject
	protected HomeView(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 1", "secure/view1");
		addNavButton("view 1 with parameters", "secure/view1/id=22");
		addNavButton("view 2", "public/view2");
		addNavButton("view 2 with parameters", "public/view2/id=22");
		addNavButton("invalid uri", "view3/id=22");
	}

	@Override
	public int getColourIndex() {
		return 0;
	}

}
