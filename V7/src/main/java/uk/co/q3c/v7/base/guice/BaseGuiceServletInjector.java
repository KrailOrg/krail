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

import javax.servlet.ServletContext;
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
import uk.co.q3c.v7.base.navigate.sitemap.DefaultStandardPagesModule;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapServiceModule;
import uk.co.q3c.v7.base.notify.DefaultUserNotificationModule;
import uk.co.q3c.v7.base.services.ServicesMonitor;
import uk.co.q3c.v7.base.services.ServicesMonitorModule;
import uk.co.q3c.v7.base.shiro.DefaultShiroModule;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.useropt.DefaultUserOptionModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.base.view.component.DefaultComponentModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public abstract class BaseGuiceServletInjector extends GuiceServletContextListener {
	private static Logger log = LoggerFactory.getLogger(BaseGuiceServletInjector.class);

	protected static Injector injector;

	private ThreadLocal<ServletContext> ctx;

	protected BaseGuiceServletInjector() {
		super();

	}

	protected ThreadLocal<ServletContext> createThreadLocalServletContext() {
		return new ThreadLocal<ServletContext>();
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
			throw new IllegalStateException("The injector is not available, it may not yet been initialized.");
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
		baseModules.add(new ApplicationConfigurationModule());
		baseModules.add(new SitemapServiceModule());

		baseModules.add(new ThreadScopeModule());
		baseModules.add(new UIScopeModule());
		baseModules.add(new ServicesMonitorModule());

		baseModules.add(shiroModule(ctx.get()));
		baseModules.add(shiroVaadinModule());
		baseModules.add(new ShiroAopModule());
		baseModules.add(userOptionsModule());

		baseModules.add(servletModule());

		baseModules.add(standardPagesModule());

		baseModules.add(standardViewsModule());

		baseModules.add(componentModule());

		baseModules.add(userNotificationModule());

		addAppModules(baseModules);
		addSitemapModules(baseModules);
		return baseModules;
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
		return new DefaultComponentModule();
	}

	protected Module userOptionsModule() {
		return new DefaultUserOptionModule();
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
		return new DefaultStandardPagesModule();
	}

	/**
	 * Override this if you have sub-classed {@link StandardViewModule} to provide bindings to your own standard page
	 * views
	 */
	protected Module standardViewsModule() {
		return new StandardViewModule();
	}

	/**
	 * Override this method if you have sub-classed {@link DefaultShiroModule} to provide bindings to your Shiro related
	 * implementations (for example, {@link Realm} and {@link CredentialsMatcher}
	 * 
	 * @param servletContext
	 * @param ini
	 * @return
	 */

	protected Module shiroModule(ServletContext servletContext) {
		return new DefaultShiroModule();
	}

	/**
	 * Override this if you have sub-classed {@link DefaultUserNotificationModule} to provide bindings to your own
	 * notifications implementations
	 */
	private Module userNotificationModule() {
		return new DefaultUserNotificationModule();
	}

	/**
	 * Add as many application specific Guice modules as you wish by overriding this method.
	 * 
	 * @param baseModules
	 * @param ini
	 */
	protected abstract void addAppModules(List<Module> baseModules);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ctx = createThreadLocalServletContext();
		final ServletContext servletContext = servletContextEvent.getServletContext();
		ctx.set(servletContext);
		createInjector();
		super.contextInitialized(servletContextEvent);

	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		log.info("Stopping services");
		getInjector().getInstance(ServicesMonitor.class).stopAllServices();
		super.contextDestroyed(servletContextEvent);
		ctx.remove();
	}
}