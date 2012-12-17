package uk.co.q3c.basic.demo;

import javax.inject.Inject;

import uk.co.q3c.basic.A;
import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.google.inject.name.Named;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ChameleonTheme;

@UIScoped
public class HeaderBar extends HorizontalLayout {
	private Label viewTag;

	private int colourIndex;
	private CssLayout hl;

	private static final String[] colours = new String[] { "#aeec31", "#34a782", "#ed7024", "#c8dd7d" };

	@Inject
	protected HeaderBar(@Named(A.title) String title, @Named(A.version) String version, MessageSource msgSource) {
		super();
		ThemeResource img = new ThemeResource("img/logosmall.png");
		Embedded logo = new Embedded(null, img);
		Label header = new Label(title + "  " + version);

		header.setDescription("the HeaderBar is @UIScoped and could therefore be injected in to any component in this UI and be the same instance");

		header.addStyleName(ChameleonTheme.LABEL_H2);

		this.addComponent(logo);
		viewTag = new Label("Now showing", ContentMode.HTML);

		hl = new CssLayout() {
			@Override
			protected String getCss(Component c) {
				if (c == viewTag) {
					String s = "background: " + colours[colourIndex];
					return s;
				}
				return null;
			}
		};
		hl.addComponent(header);
		hl.addComponent(viewTag);

		// hl.setExpandRatio(header, 1);
		// hl.setExpandRatio(viewTag, 1);
		// hl.setExpandRatio(blank, 1);
		this.addComponent(hl);
		this.setExpandRatio(hl, 1);
		setWidth("100%");
	}

	public void setViewTag(int colourIndex, String tag) {

		this.colourIndex = colourIndex;
		viewTag.setValue("Now showing view: " + tag);
		hl.markAsDirtyRecursive();
	}

	public String getViewTag() {
		return viewTag.getValue();
	}

}
