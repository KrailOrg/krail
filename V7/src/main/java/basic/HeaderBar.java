package basic;

import javax.inject.Inject;

import com.google.inject.name.Named;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class HeaderBar extends Panel {
	private static final long serialVersionUID = -6497285338281303606L;

	@Inject
	protected HeaderBar(@Named("title") String title, @Named("version") String version, MessageSource msgSource) {
		super();

		setCaption("header bar " + title + "  " + version);
		VerticalLayout topBarLayout = new VerticalLayout();
		topBarLayout.addComponent(new Label(msgSource.msg()));
		setContent(topBarLayout);
		setHeight("100px");
		setWidth("100%");
	}

}
