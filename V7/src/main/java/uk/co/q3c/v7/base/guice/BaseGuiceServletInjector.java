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
package uk.co.q3c.v7.base.guice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.ApplicationConfigurationModule;
import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapServiceModule;
import uk.co.q3c.v7.base.navigate.sitemap.StandardPagesModule;
import uk.co.q3c.v7.base.services.ServicesMonitor;
import uk.co.q3c.v7.base.services.ServicesMonitorModule;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.shiro.StandardShiroModule;
import uk.co.q3c.v7.base.user.UserModule;
import uk.co.q3c.v7.base.view.ViewModule;
import uk.co.q3c.v7.base.view.component.StandardComponentModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public abstract class BaseGuiceServletInjector extends GuiceServletContextListener {
	private static Logger log = LoggerFactory.getLogger(BaseGuiceServletInjector.class);

	protected static Injector injector;

	protected BaseGuiceServletInjector() {
		super();
	}

	/**
	 * Module instances for the base should be added in {@link #getModules()}. Module instance for the app using V7
	 * should be added to {@link AppModules#appModules()}
	 * 
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	public Injector getInjector() {
		if (injector == null) {
			createInjector();
		}
		return injector;
	}

	protected void createInjector() {
		injector = Guice.createInjector(getModules());
		log.debug("injector created");

		// By default Shiro provides a binding to DefaultSecurityManager, but that is replaced by a binding to
		// V7SecurityManager in DefaultShiroModule#bindSecurityManager (or potentially to another security manager if
		// the developer overrides that method)
		SecurityManager securityManager = injector.getInstance(SecurityManager.class);
		SecurityUtils.setSecurityManager(securityManager);

	}

	private List<Module> getModules() {
		List<Module> baseModules = new ArrayList<>();

		baseModules.add(new I18NModule());
		baseModules.add(applicationConfigurationModule());
		baseModules.add(new SitemapServiceModule());

		baseModules.add(new ThreadScopeModule());
		baseModules.add(new UIScopeModule());
		baseModules.add(new VaadinSessionScopeModule());

		baseModules.add(new ServicesMonitorModule());

		baseModules.add(shiroModule());
		baseModules.add(shiroVaadinModule());
		baseModules.add(new ShiroAopModule());

		baseModules.add(servletModule());

		baseModules.add(standardPagesModule());

		baseModules.add(viewModule());

		baseModules.add(componentModule());

		baseModules.add(userModule());

		addAppModules(baseModules);
		addSitemapModules(baseModules);
		return baseModules;
	}

	protected Module applicationConfigurationModule() {
		return new ApplicationConfigurationModule();
	}

	/**
	 * Modules used in the creation of the {@link Sitemap} do not actually need to be separated, this just makes a
	 * convenient way of seeing them as a group
	 * 
	 * @param baseModules
	 */
	protected void addSitemapModules(List<Module> baseModules) {
	}

	protected Module componentModule() {
		return new StandardComponentModule();
	}

	/**
	 * Override this if you have provided your own {@link ServletModule}
	 * 
	 * @return
	 */
	protected Module servletModule() {
		return new BaseServletModule();
	}

	/**
	 * Override this method if you have sub-classed {@link ShiroVaadinModule} to provide your own bindings for Shiro
	 * related exceptions.
	 * 
	 * @return
	 */
	protected Module shiroVaadinModule() {
		return new ShiroVaadinModule();
	}

	/**
	 * Override this if you have sub-classed {@link StandardPagesModule} to provide bindings to your own standard page
	 * views
	 */
	protected Module standardPagesModule() {
		return new StandardPagesModule();
	}

	/**
	 * Override this if you have sub-classed {@link ViewModule} to provide bindings to your own standard page
	 * views
	 */
	protected Module viewModule() {
		return new ViewModule();
	}

	/**
	 * Override this method if you have sub-classed {@link StandardShiroModule} to provide bindings to your Shiro
	 * related implementations (for example, {@link Realm} and {@link CredentialsMatcher}
	 * 
	 * @param servletContext
	 * @param ini
	 * @return
	 */

	protected Module shiroModule() {
		return new StandardShiroModule();
	}

	/**
	 * Override this if you have sub-classed {@link UserModule} to provide bindings to your user related implementations
	 */
	private Module userModule() {
		return new UserModule();
	}

	/**
	 * Add as many application specific Guice modules as you wish by overriding this method.
	 * 
	 * @param baseModules
	 * @param ini
	 */
	protected abstract void addAppModules(List<Module> baseModules);

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		log.info("Stopping services");
		try {
			getInjector().getInstance(ServicesMonitor.class).stopAllServices();
		} catch (Exception e) {
			log.error("Exception while stopping services", e);
		}
		super.contextDestroyed(servletContextEvent);
	}
}