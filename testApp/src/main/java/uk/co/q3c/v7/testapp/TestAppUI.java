package uk.co.q3c.v7.testapp;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.push.Broadcaster;
import uk.co.q3c.v7.base.push.PushMessageRouter;
import uk.co.q3c.v7.base.ui.ApplicationTitle;
import uk.co.q3c.v7.base.ui.DefaultApplicationUI;
import uk.co.q3c.v7.base.view.component.ApplicationHeader;
import uk.co.q3c.v7.base.view.component.ApplicationLogo;
import uk.co.q3c.v7.base.view.component.ApplicationMenu;
import uk.co.q3c.v7.base.view.component.Breadcrumb;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;
import uk.co.q3c.v7.base.view.component.MessageBar;
import uk.co.q3c.v7.base.view.component.SubpagePanel;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;
import uk.co.q3c.v7.testapp.view.SessionObject;

import com.google.inject.Inject;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;

/**
 * The UI class used in this demo for the V7 application base
 * 
 * @author David Sowerby
 * 
 */
@Theme("chameleon")
@Push
public class TestAppUI extends DefaultApplicationUI {

	@Inject
	protected TestAppUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory,
			ApplicationLogo logo, ApplicationHeader header, LoginStatusPanel loginOut, ApplicationMenu menu,
			UserNavigationTree navTree, Breadcrumb breadcrumb, SubpagePanel subpage, MessageBar messageBar,
			Broadcaster broadcaster, PushMessageRouter pushMessageRouter, SessionObject sessionObject,
			@SuppressWarnings("rawtypes") @ApplicationTitle I18NKey applicationTitleKey, Translate translate) {
		super(navigator, errorHandler, converterFactory, logo, header, loginOut, menu, navTree, breadcrumb, subpage,
				messageBar, broadcaster, pushMessageRouter, applicationTitleKey, translate);

	}

	@Override
	protected void processBroadcastMessage(String group, String message) {
		super.processBroadcastMessage(group, message);
		getMessageBar().informationMessage(group + ":" + message);
	}

}