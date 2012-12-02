package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.google.inject.name.Named;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

@UIScoped
public class HeaderBar extends Panel {

	@Inject
	protected HeaderBar(@Named(A.title) String title, @Named(A.version) String version, MessageSource msgSource) {
		super();

		setCaption(title + "  " + version);
		VerticalLayout layout = new VerticalLayout();
		Label explain = new Label(
				"the HeaderBar is @UIScoped and could therefore be injected in to any component in this UI and be the same instance");
		explain.addStyleName(ChameleonTheme.LABEL_H3);
		layout.addComponent(explain);
		setWidth("100%");
		setContent(layout);
	}

}
