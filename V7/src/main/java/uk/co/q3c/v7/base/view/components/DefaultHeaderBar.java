package uk.co.q3c.v7.base.view.components;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@UIScoped
public class DefaultHeaderBar extends CustomComponent implements HeaderBar {
	private GridLayout mainLayout;

	private Label appLabel;

	private Embedded logo;

	private final LoginStatusPanel loginPanel;

	@Inject
	protected DefaultHeaderBar(LoginStatusPanel loginPanel) {
		setHeight("");
		this.loginPanel = loginPanel;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		configureComponents();
	}

	private void configureComponents() {

		mainLayout.setSpacing(true);
		mainLayout.setColumnExpandRatio(2, 1);

		appLabel.setContentMode(ContentMode.HTML);

	}

	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(false);
		mainLayout.setColumns(5);

		// top-level component properties
		setWidth("100.0%");
		setHeight("-1px");

		// logo
		logo = new Embedded();
		logo.setImmediate(false);
		logo.setWidth("-1px");
		logo.setHeight("-1px");
		logo.setSource(new ThemeResource("img/logosmall.png"));
		logo.setType(1);
		logo.setMimeType("image/png");
		mainLayout.addComponent(logo, 0, 0);

		// appLabel
		appLabel = new Label();
		appLabel.setImmediate(false);
		appLabel.setWidth("-1px");
		appLabel.setHeight("-1px");
		appLabel.setValue("<font size=7><b>V7</b></font>");
		mainLayout.addComponent(appLabel, 1, 0);

		// login panel
		mainLayout.addComponent(loginPanel, 4, 0);
		loginPanel.setWidth("200px");
		loginPanel.setHeight("100px");

		return mainLayout;
	}

}
