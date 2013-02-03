package uk.co.q3c.v7.demo.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.view.VerticalViewBase;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ChameleonTheme;

public class PublicHomeView extends VerticalViewBase implements ClickListener {

	private Embedded logo;

	@Inject
	protected PublicHomeView() {
		super();
		buildMainLayout();
	}

	private void buildMainLayout() {
		// HTML snippet
		Panel p = new Panel();
		p.setSizeFull();
		p.setCaption("intro");
		p.addStyleName(ChameleonTheme.PANEL_BUBBLE);
		ThemeResource resource = new ThemeResource("html/homepage.html");
		BrowserFrame html = new BrowserFrame();
		html.setSource(resource);
		html.setSizeFull();
		p.setContent(html);
		this.addComponent(p);
		this.addComponent(addNavButton("Enter", "public/view2"));

	}

	@Override
	protected void processParams(List<String> params) {

	}

	protected Button addNavButton(String caption, String uri) {
		Button button = new Button(caption);
		button.setData(uri);
		button.setDescription(uri);
		button.addStyleName(ChameleonTheme.BUTTON_TALL);
		button.addClickListener(this);
		return button;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		String uri = (btn.getData() == null) ? null : btn.getData().toString();
		this.getScopedUI().getGuiceNavigator().navigateTo(uri);
	}

}
