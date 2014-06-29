package uk.co.q3c.v7.base.ui;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.user.notify.UserNotifier;
import uk.co.q3c.v7.base.view.component.ApplicationHeader;
import uk.co.q3c.v7.base.view.component.ApplicationLogo;
import uk.co.q3c.v7.base.view.component.UserNavigationMenu;
import uk.co.q3c.v7.base.view.component.Breadcrumb;
import uk.co.q3c.v7.base.view.component.LocaleSelector;
import uk.co.q3c.v7.base.view.component.MessageBar;
import uk.co.q3c.v7.base.view.component.OtherSelector;
import uk.co.q3c.v7.base.view.component.SubpagePanel;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;
import uk.co.q3c.v7.base.view.component.UserStatusPanel;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NProcessor;
import uk.co.q3c.v7.i18n.MessageKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;

/**
 * A common layout for a business-type application. This is a good place to start even if you replace it eventually.
 *
 * @author David Sowerby
 *
 */
// @Theme("v7demo")
public class DefaultApplicationUI extends ScopedUI {

	private VerticalLayout baseLayout;
	private final UserNavigationTree navTree;
	private final Breadcrumb breadcrumb;
	private final UserStatusPanel userStatus;
	private final UserNavigationMenu menu;
	private final SubpagePanel subpage;
	private final MessageBar messageBar;
	private final ApplicationLogo logo;
	private final ApplicationHeader header;
	private final LocaleSelector localeSelector;
	private final OtherSelector otherSelector;
	private Button refreshButton;
	private final UserNotifier userNotifier;

	@Inject
	protected DefaultApplicationUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
			ApplicationLogo logo, ApplicationHeader header, UserStatusPanel userStatusPanel, UserNavigationMenu menu,
			UserNavigationTree navTree, Breadcrumb breadcrumb, SubpagePanel subpage, MessageBar messageBar,
			Broadcaster broadcaster, PushMessageRouter pushMessageRouter, ApplicationTitle applicationTitle,
			Translate translate, CurrentLocale currentLocale, I18NProcessor translator, LocaleSelector localeSelector,
			OtherSelector otherSelector, UserNotifier userNotifier) {
		super(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitle, translate,
				currentLocale, translator);
		this.navTree = navTree;
		this.breadcrumb = breadcrumb;
		this.userStatus = userStatusPanel;
		this.menu = menu;
		this.subpage = subpage;
		this.messageBar = messageBar;
		this.logo = logo;
		this.header = header;
		this.localeSelector = localeSelector;
		this.otherSelector = otherSelector;
		this.userNotifier = userNotifier;
	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		if (baseLayout == null) {
			refreshButton = new Button("refresh");
			refreshButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					userNotifier.notifyInformation(MessageKey.LocaleChange, currentLocale.getLocale().getDisplayName());
				}
			});
			setSizes();

			baseLayout = new VerticalLayout();
			baseLayout.setSizeFull();

			HorizontalLayout row0 = new HorizontalLayout(refreshButton, localeSelector.getComponent(), header,
					otherSelector.getSample(), userStatus);
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
		userStatus.setSizeUndefined();

		navTree.setSizeFull();
		breadcrumb.setSizeUndefined();

		menu.setSizeUndefined();
		menu.setWidth("100%");

		subpage.setSizeUndefined();

		messageBar.setSizeUndefined();
		messageBar.setWidth("100%");

	}

	public MessageBar getMessageBar() {
		return messageBar;
	}

	public VerticalLayout getBaseLayout() {
		return baseLayout;
	}

	public UserNavigationTree getNavTree() {
		return navTree;
	}

	public Breadcrumb getBreadcrumb() {
		return breadcrumb;
	}

	public UserStatusPanel getUserStatus() {
		return userStatus;
	}

	public UserNavigationMenu getMenu() {
		return menu;
	}

	public SubpagePanel getSubpage() {
		return subpage;
	}

	public ApplicationLogo getLogo() {
		return logo;
	}

	public ApplicationHeader getHeader() {
		return header;
	}

}