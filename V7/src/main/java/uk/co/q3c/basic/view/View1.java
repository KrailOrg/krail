package uk.co.q3c.basic.view;

import javax.inject.Inject;

import uk.co.q3c.basic.URIDecoder;

public class View1 extends DemoViewBase {

	@Inject
	protected View1(URIDecoder uriDecoder) {
		super(uriDecoder);
		addNavButton("view 2", "view2");
		addNavButton("view 2 with parameters", "view2/id=22");
		addNavButton("home", "");
		addNavButton("home with parameters", "/id=2/age=15");
		addNavButton("invalid uri", "view3/id=22");
	}

}
