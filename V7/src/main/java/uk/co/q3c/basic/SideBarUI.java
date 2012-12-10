package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.navigate.ComponentContainerViewDisplay;
import uk.co.q3c.basic.guice.navigate.GuiceNavigator;
import uk.co.q3c.basic.guice.navigate.GuiceViewProvider;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

public class SideBarUI extends BasicUI {

	@Inject
	protected SideBarUI(HeaderBar headerBar, FooterBar footerBar, String title, GuiceViewProvider viewProvider,
			GuiceNavigator navigator, ComponentContainerViewDisplay display) {
		super(headerBar, footerBar, title, viewProvider, navigator, display);
	}

	@Override
	protected VerticalLayout doLayout() {
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

		VerticalLayout viewArea = new VerticalLayout();
		viewArea.setSizeFull();

		centreSection.addComponent(sideBarPanel);
		centreSection.addComponent(viewArea);

		VerticalLayout screenLayout = new VerticalLayout(getHeaderBar(), centreSection, getFooterBar());
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(centreSection, 1);
		centreSection.setExpandRatio(viewArea, 1);
		setContent(screenLayout);
		return viewArea;
	}

}
