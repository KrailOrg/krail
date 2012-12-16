package uk.co.q3c.basic.view;

import javax.inject.Inject;

import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.HeaderBar;

public class View2 extends DemoViewBase {

	@Inject
	protected View2(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 1", "view1");
		addNavButton("view 1 with parameters", "view1/id=22");
		addNavButton("home", "");
		addNavButton("home with parameters", "/id=2/age=15");
		addNavButton("invalid uri", "view3/id=22");

	}

	@Override
	public int getColourIndex() {
		return 2;
	}

}
