package uk.co.q3c.basic.demo;

import javax.inject.Inject;


public class View1 extends DemoViewBase {

	@Inject
	protected View1(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 2", "view2");
		addNavButton("view 2 with parameters", "view2/id=22");
		addNavButton("home", "");
		addNavButton("home with parameters", "/id=2/age=15");
		addNavButton("invalid uri", "view3/id=22");
	}

	@Override
	public int getColourIndex() {
		return 1;
	}

}
