package uk.co.q3c.v7.demo.ui;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.demo.view.components.FooterBar;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

public class SideBarUI extends BasicUI {

	@Inject
	protected SideBarUI(HeaderBar headerBar, FooterBar footerBar, String title, V7Navigator navigator,
			ErrorHandler errorHandler) {
		super(headerBar, footerBar, title, navigator, errorHandler);
	}

	@Override
	protected void doLayout() {
		// viewArea is the layout where Views will be placed

		HorizontalLayout centreSection = new HorizontalLayout();
		centreSection.setSizeFull();

		Panel sideBarPanel = new Panel("Sidebar");
		sideBarPanel.addStyleName(ChameleonTheme.PANEL_BUBBLE);
		VerticalLayout panelLayout = new VerticalLayout();
		sideBarPanel.setContent(panelLayout);
		TextArea textArea = new TextArea();
		textArea.setValue("This sidebar does nothing,except demonstrate the use of two UIs.  See class BasicProvider");
		textArea.setWidth("150px");
		panelLayout.addComponent(textArea);
		sideBarPanel.setSizeUndefined();
		sideBarPanel.setHeight("100%");

		centreSection.addComponent(sideBarPanel);
		centreSection.addComponent(getViewDisplayPanel());

		VerticalLayout screenLayout = new VerticalLayout(getHeaderBar(), centreSection, getFooterBar());
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(centreSection, 1);
		centreSection.setExpandRatio(getViewDisplayPanel(), 1);
		setContent(screenLayout);
	}

}