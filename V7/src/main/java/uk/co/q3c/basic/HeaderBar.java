package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.google.inject.name.Named;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@UIScoped
public class HeaderBar extends Panel {

	@Inject
	protected HeaderBar(@Named(A.title) String title,
			@Named(A.version) String version, MessageSource msgSource) {
		super();

		setCaption("header bar");
		VerticalLayout layout = new VerticalLayout();
		Label explain = new Label(title + "  " + version);
		explain.setPrimaryStyleName("h2");
		layout.addComponent(explain);
		setWidth("100%");
		setContent(layout);
	}

}
