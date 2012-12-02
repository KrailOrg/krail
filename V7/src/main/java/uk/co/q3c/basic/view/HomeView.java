package uk.co.q3c.basic.view;

import javax.inject.Inject;

import uk.co.q3c.basic.FooterBar;
import uk.co.q3c.basic.URIDecoder;

public class HomeView extends DemoViewBase {

	@Inject
	protected HomeView(URIDecoder uriDecoder, FooterBar footerBar) {
		super(uriDecoder, footerBar);
		addNavButton("view 1", "view1");
		addNavButton("view 1 with parameters", "view1/id=22");
		addNavButton("view 2", "view2");
		addNavButton("view 2 with parameters", "view2/id=22");
		addNavButton("invalid uri", "view3/id=22");
	}

}
