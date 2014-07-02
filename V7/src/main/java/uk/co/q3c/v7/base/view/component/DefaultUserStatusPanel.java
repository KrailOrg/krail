/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.base.view.component;

import java.util.Locale;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.StandardPageKey;
import uk.co.q3c.v7.base.shiro.SubjectIdentifier;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.user.status.UserStatus;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ChameleonTheme;

/**
 * Represents the "logged in" status of the current {@link Subject}.
 * <p>
 *
 *
 *
 * @author David Sowerby 16 Jan 2013
 *
 */
// TODO I18N
public class DefaultUserStatusPanel extends Panel implements UserStatusPanel, ClickListener {
	private static Logger log = LoggerFactory.getLogger(DefaultUserStatusPanel.class);
	private final Label usernameLabel;
	private final Button login_logout_Button;
	private final V7Navigator navigator;
	private final Provider<Subject> subjectProvider;
	private final Translate translate;
	private final SubjectIdentifier subjectIdentifier;
	private final UserStatus userStatus;

	@Inject
	protected DefaultUserStatusPanel(V7Navigator navigator, SubjectProvider subjectProvider, Translate translate,
			SubjectIdentifier subjectIdentifier, UserStatus userStatus, CurrentLocale currentLocale) {
		super();
		this.navigator = navigator;
		this.subjectProvider = subjectProvider;
		this.translate = translate;
		this.subjectIdentifier = subjectIdentifier;
		this.userStatus = userStatus;
		currentLocale.addListener(this);
		userStatus.addListener(this);
		setSizeFull();
		addStyleName(ChameleonTheme.PANEL_BORDERLESS);
		usernameLabel = new Label();
		login_logout_Button = new Button();
		login_logout_Button.addClickListener(this);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.addComponent(usernameLabel);
		hl.addComponent(login_logout_Button);
		this.setContent(hl);
		setIds();
		userStatusChanged();

	}

	private void setIds() {
		setId(ID.getId(this));
		login_logout_Button.setId(ID.getId(this, login_logout_Button));
		usernameLabel.setId(ID.getId(this, usernameLabel));
	}

	@Override
	public String getActionLabel() {
		return login_logout_Button.getCaption();
	}

	@Override
	public String getUserId() {
		return usernameLabel.getValue();
	}

	public Button getLogin_logout_Button() {
		return login_logout_Button;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		boolean authenticated = subjectProvider.get().isAuthenticated();
		if (authenticated) {
			subjectProvider.get().logout();
			navigator.navigateTo(StandardPageKey.Log_Out);
			userStatus.statusChanged();
		} else {
			navigator.navigateTo(StandardPageKey.Log_In);
		}

	}

	@Override
	public void userStatusChanged() {
		log.debug("login status change");
		build();

	}

	@Override
	public void localeChanged(Locale locale) {
		log.debug("locale change");
		build();
	}

	private void build() {
		boolean authenticated = subjectProvider.get().isAuthenticated();
		String caption = (authenticated) ? translate.from(LabelKey.Log_Out) : translate.from(LabelKey.Log_In);
		login_logout_Button.setCaption(caption.toLowerCase());
		usernameLabel.setValue(subjectIdentifier.subjectName());
	}

}
