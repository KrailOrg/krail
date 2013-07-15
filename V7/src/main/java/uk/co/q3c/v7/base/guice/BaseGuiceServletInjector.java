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

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.shiro.DefaultShiroWebModule;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.useropt.DefaultUserOptionModule;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

public abstract class BaseGuiceServletInjector extends GuiceServletContextListener {
	protected static Injector injector;

	private final ThreadLocal<ServletContext> ctx = new ThreadLocal<ServletContext>();

	/**
	 * Module instances for the base should be added in {@link #getModules()}. Module instance for the app using V7
	 * should be added to {@link AppModules#appModules()}
	 * 
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	protected Injector getInjector() {

		injector = Guice.createInjector(new IniModule());

		injector = injector.createChildInjector(getModules());

		// The SecurityManager binding is in ShiroWebModule, and therefore
		// DefaultShiroWebModule. By default the binding
		// is to DefaultWebSecurityManager
		SecurityManager securityManager = injector.getInstance(SecurityManager.class);
		SecurityUtils.setSecurityManager(securityManager);

		return injector;
	}

	private List<Module> getModules() {
		// ini load is handled by the provider
		V7Ini ini = injector.getInstance(V7Ini.class);
		List<Module> baseModules = new ArrayList<>();

		if (ini.optionReadSiteMap()) {
			Sitemap sitemap = injector.getInstance(Sitemap.class);
			baseModules.add(new ApplicationViewModule(sitemap));
		} else {
			// module for Views must be in addAppModules()
		}

		baseModules.add(new ThreadScopeModule());
		baseModules.add(new UIScopeModule());

		baseModules.add(shiroWebModule(ctx.get(), ini));
		baseModules.add(shiroVaadinModule());
		baseModules.add(new ShiroAopModule());
		baseModules.add(userOptionsModule(ini));

		baseModules.add(baseModule());

		baseModules.add(new I18NModule());

		baseModules.add(standardViewModule());

		addAppModules(baseModules, ini);
		return baseModules;
	}

	private Module userOptionsModule(V7Ini ini) {
		return new DefaultUserOptionModule(ini);
	}

	/**
	 * Override this if you have sub-classed the {@link BaseModule}
	 * 
	 * @return
	 */
	protected Module baseModule() {
		return new BaseModule();
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
	 * Override this if you have sub-classed {@link StandardViewModule} to provide bindings to your own standard page
	 * views
	 */
	protected Module standardViewModule() {
		return new StandardViewModule();
	}

	/**
	 * Override this method if you have sub-classed {@link DefaultShiroWebModule} to provide bindings to your Shiro
	 * related implementations (for example, {@link Realm} and {@link CredentialsMatcher}
	 * 
	 * @param servletContext
	 * @param ini
	 * @return
	 */

	protected Module shiroWebModule(ServletContext servletContext, V7Ini ini) {
		return new DefaultShiroWebModule(servletContext);
	}

	/**
	 * Add as many application specific Guice modules as you wish by overriding this method.
	 * 
	 * @param baseModules
	 * @param ini
	 */
	protected abstract void addAppModules(List<Module> baseModules, V7Ini ini);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServletContext servletContext = servletContextEvent.getServletContext();
		ctx.set(servletContext);
		super.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// may need later for Quartz
		// try {
		// if (injector != null)
		// injector.getInstance(Scheduler.class).shutdown();
		// } catch (SchedulerException e) {
		// e.printStackTrace();
		// }
		// injector.getInstance(PersistService.class).stop();
		super.contextDestroyed(servletContextEvent);
		ctx.remove();
	}
}