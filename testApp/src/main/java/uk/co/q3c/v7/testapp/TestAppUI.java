package uk.co.q3c.v7.testapp;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.component.ApplicationHeader;
import uk.co.q3c.v7.base.view.component.ApplicationLogo;
import uk.co.q3c.v7.base.view.component.ApplicationMenu;
import uk.co.q3c.v7.base.view.component.Breadcrumb;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;
import uk.co.q3c.v7.base.view.component.MessageBar;
import uk.co.q3c.v7.base.view.component.SubpagePanel;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

import com.google.inject.Inject;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;

/**
 * The UI class used in this demo for the V7 application base
 * 
 * @author David Sowerby
 * 
 */
@Theme("chameleon")
@Push
public class TestAppUI extends ScopedUI {
	private VerticalLayout baseLayout;
	private final UserNavigationTree navTree;
	private final Breadcrumb breadcrumb;
	private final LoginStatusPanel loginOut;
	private final ApplicationMenu menu;
	private final SubpagePanel subpage;
	private final MessageBar messageBar;
	private final ApplicationLogo logo;
	private final ApplicationHeader header;

	@Inject
	protected TestAppUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
			ApplicationLogo logo, ApplicationHeader header, LoginStatusPanel loginOut, ApplicationMenu menu,
			UserNavigationTree navTree, Breadcrumb breadcrumb, SubpagePanel subpage, MessageBar messageBar,
			LoginStatusHandler loginStatusHandler, Broadcaster broadcaster, PushMessageRouter pushMessageRouter) {
		super(navigator, errorHandler, converterFactory, loginStatusHandler, broadcaster, pushMessageRouter);
		this.navTree = navTree;
		this.breadcrumb = breadcrumb;
		this.loginOut = loginOut;
		this.menu = menu;
		this.subpage = subpage;
		this.messageBar = messageBar;
		this.logo = logo;
		this.header = header;

	}

	@Override
	protected String pageTitle() {
		return "V7 Test Application";
	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		if (baseLayout == null) {

			setSizes();

			baseLayout = new VerticalLayout();
			baseLayout.setSizeFull();

			HorizontalLayout row0 = new HorizontalLayout(logo, header, loginOut);
			row0.setWidth("100%");
			baseLayout.addComponent(row0);
			baseLayout.addComponent(menu);
			HorizontalSplitPanel row2 = new HorizontalSplitPanel();
			row2.setWidth("100%");
			row2.setSplitPosition(200, Unit.PIXELS);

			row2.setFirstComponent(navTree);

			VerticalLayout mainArea = new VerticalLayout(breadcrumb, getViewDisplayPanel(), subpage);
			mainArea.setSizeFull();
			row2.setSecondComponent(mainArea);
			baseLayout.addComponent(row2);
			baseLayout.addComponent(messageBar);
			mainArea.setExpandRatio(getViewDisplayPanel(), 1f);

			row0.setExpandRatio(header, 1f);
			baseLayout.setExpandRatio(row2, 1f);

		}
		return baseLayout;
	}

	private void setSizes() {
		logo.setWidth("100px");
		logo.setHeight("100px");

		header.setHeight("100%");
		loginOut.setSizeUndefined();

		navTree.setSizeFull();
		breadcrumb.setSizeUndefined();

		menu.setSizeUndefined();
		menu.setWidth("100%");

		subpage.setSizeUndefined();
		// subpage.setWidth("100%");

		messageBar.setSizeUndefined();
		messageBar.setWidth("100%");

	}

	@Override
	protected void processBroadcastMessage(String group, String message) {
		super.processBroadcastMessage(group, message);
		messageBar.informationMessage(group + ":" + message);
	}

}