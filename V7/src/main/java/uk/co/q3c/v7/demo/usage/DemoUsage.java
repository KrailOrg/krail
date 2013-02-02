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
package uk.co.q3c.v7.demo.usage;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginStatusListener;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.demo.dao.DemoUsageLogDAO;

import com.vaadin.server.Page;
import com.vaadin.server.WebBrowser;

@UIScoped
public class DemoUsage implements LoginStatusListener, V7ViewChangeListener {
	private final Provider<DemoUsageLogDAO> daoPro;
	private final Provider<WebBrowser> browserPro;

	@Inject
	protected DemoUsage(V7SecurityManager securityManager, V7Navigator navigator, Provider<DemoUsageLogDAO> daoPro,
			Provider<WebBrowser> browserPro) {
		super();
		this.daoPro = daoPro;
		this.browserPro = browserPro;
		securityManager.addListener(this);
		navigator.addViewChangeListener(this);
	}

	@Override
	public void updateStatus(Subject subject) {
		if (subject.isAuthenticated()) {
			makeEntry("login");
		} else {
			makeEntry("logout");
		}

	}

	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		// do nothing but don't block later listeners
		return true;
	}

	@Override
	public void afterViewChange(V7ViewChangeEvent event) {
		makeEntry("view change to " + event.getViewName());

	}

	private void makeEntry(String eventType) {
		DemoUsageLogDAO dao = daoPro.get();
		DemoUsageLog entry = dao.newEntity();
		Page page = Page.getCurrent();
		WebBrowser b = page.getWebBrowser();
		WebBrowser browser = browserPro.get();
		entry.setDateTime(DateTime.now());
		if (browser.getLocale() == null)
			entry.setLocaleString("no locale set");
		else {
			entry.setLocaleString(browser.getLocale().toString());
		}
		if (browser.getAddress() == null)
			entry.setSourceIP("no IP address set");
		else {
			entry.setLocaleString(browser.getAddress());
		}
		entry.setEvent(eventType);
		dao.save(entry);
	}

}
