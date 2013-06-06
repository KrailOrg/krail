package uk.co.q3c.v7.demo.view;

import javax.inject.Inject;

import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

public class View1 extends DemoViewBase {

	@Inject
	protected View1(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 2", view2);
		addNavButton("view 2 with parameters", view2 + "/id=22");
		addNavButton("home", home);
		addNavButton("home with parameters", home + "/id=2/age=15");
		addNavButton("invalid uri", "view3/id=22");
	}

}
