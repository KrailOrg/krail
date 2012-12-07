package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.google.inject.name.Named;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ChameleonTheme;

@UIScoped
public class HeaderBar extends HorizontalLayout {

	@Inject
	protected HeaderBar(@Named(A.title) String title, @Named(A.version) String version, MessageSource msgSource) {
		super();
		ThemeResource img = new ThemeResource("img/logosmall.png");
		Embedded logo = new Embedded(null, img);
		Label header = new Label(title + "  " + version);

		header.setDescription("the HeaderBar is @UIScoped and could therefore be injected in to any component in this UI and be the same instance");

		header.addStyleName(ChameleonTheme.LABEL_H2);

		this.addComponent(logo);
		this.addComponent(header);

		setWidth("100%");
		this.setExpandRatio(header, 1);
	}

}
