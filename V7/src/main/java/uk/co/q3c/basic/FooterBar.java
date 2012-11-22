package uk.co.q3c.basic;

import javax.inject.Inject;

import com.vaadin.ui.Panel;

public class FooterBar extends Panel {
	private static final long serialVersionUID = -190551310795495995L;

	@Inject
	protected FooterBar() {
		super();
		setCaption("footer bar");
		setHeight("100px");
		setWidth("100%");
	}

}
