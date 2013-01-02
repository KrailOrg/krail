package uk.co.q3c.v7.demo.view;

import javax.inject.Inject;

import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

public class View2 extends DemoViewBase {

	@Inject
	protected View2(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 1", "secure/view1");
		addNavButton("view 1 with parameters", "secure/view1/id=22");
		addNavButton("home", "public/");
		addNavButton("home with parameters", "public/id=2/age=15");
		addNavButton("invalid uri", "view3/id=22");

	}

	@Override
	public int getColourIndex() {
		return 2;
	}

}
