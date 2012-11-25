package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.vaadin.ui.Panel;

@UIScoped
public class FooterBar extends Panel {

	@Inject
	protected FooterBar() {
		super();
		setCaption("footer bar");
		setHeight("100px");
		setWidth("100%");
	}

}
