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

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.shiro.subject.Subject;

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginStatusEvent;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.shiro.LoginStatusListener;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

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
public class DefaultLoginStatusPanel extends Panel implements LoginStatusPanel, LoginStatusListener, ClickListener {

	private final Label usernameLabel;
	private final Button login_logout_Button;
	private final V7Navigator navigator;
	private final Provider<Subject> subjectPro;
	private final Translate translate;

	@Inject
	protected DefaultLoginStatusPanel(V7Navigator navigator, Provider<Subject> subjectProvider, Translate translate,
			LoginStatusHandler loginStatusHandler) {
		super();
		this.navigator = navigator;
		this.subjectPro = subjectProvider;
		this.translate = translate;
		loginStatusHandler.addListener(this);
		setSizeFull();
		addStyleName(ChameleonTheme.PANEL_BORDERLESS);
		// register with the security manager to monitor status changes
		usernameLabel = new Label();
		login_logout_Button = new Button();
		login_logout_Button.addClickListener(this);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.addComponent(usernameLabel);
		hl.addComponent(login_logout_Button);
		this.setContent(hl);

		// initialise
		Subject subject = subjectPro.get();
		update(subject.isAuthenticated(), subject);

	}
	
	protected void update(boolean authenticated, Subject subject) {
		String caption = (authenticated) ? translate.from(LabelKey.Log_Out) : translate.from(LabelKey.Log_In);
		login_logout_Button.setCaption(caption.toLowerCase());
		usernameLabel.setValue(formatSubjectName(subject));
	}

	@Override
	public void loginStatusChange(LoginStatusEvent event) {
		update(event.isSubjectAuthenticated(), event.getSubject());
	}
	
	protected String formatSubjectName(Subject subject) {
		if(subject.isAuthenticated()) {
			return subject.getPrincipal().toString() + (subject.isRemembered()?"?":"");
		}else{
			return translate.from(LabelKey.Guest);
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		assert event.getSource() == login_logout_Button;
		
		if (subjectPro.get().isAuthenticated()) {
			subjectPro.get().logout();
			navigator.navigateTo(StandardPageKey.Logout);
		} else {
			navigator.navigateTo(StandardPageKey.Login);
		}
	}

}
