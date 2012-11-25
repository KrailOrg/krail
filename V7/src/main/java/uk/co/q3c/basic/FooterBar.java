package uk.co.q3c.basic;

import javax.inject.Inject;

import com.vaadin.ui.Panel;

public class FooterBar extends Panel {

	@Inject
	protected FooterBar() {
		super();
		setCaption("footer bar");
		setHeight("100px");
		setWidth("100%");
	}

}
