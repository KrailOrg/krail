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

import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapProvider;
import uk.co.q3c.v7.base.shiro.DefaultShiroModule;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.useropt.DefaultUserOptionModule;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.base.view.component.DefaultComponentModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

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
	protected Injector getInjector() {

		injector = Guice.createInjector(new IniModule(), new I18NModule());

		injector = injector.createChildInjector(getModules());

		// By default Shiro provides a binding to DefaultSecurityManager, but that is replaced by a binding to
		// V7SecurityManager in DefaultShiroModule#bindSecurityManager (or potentially to another security manager if
		// the developer overrides that method)
		SecurityManager securityManager = injector.getInstance(SecurityManager.class);
		SecurityUtils.setSecurityManager(securityManager);

		return injector;
	}

	private List<Module> getModules() {
		// ini load is handled by the provider
		V7Ini ini = injector.getInstance(V7Ini.class);
		List<Module> baseModules = new ArrayList<>();

		if (ini.optionReadSiteMap()) {
			log.debug("ini sitemap option is true, loading sitemap");
			Provider<Sitemap> sitemapPro = injector.getInstance(SitemapProvider.class);
			Sitemap sitemap = sitemapPro.get();
			baseModules.add(new ApplicationViewModule(sitemap));
		} else {
			// module for Views must be in addAppModules()
			log.debug("ini sitemap option is false, not loading sitemap");
		}

		baseModules.add(new ThreadScopeModule());
		baseModules.add(new UIScopeModule());

		baseModules.add(shiroModule(ctx.get(), ini));
		baseModules.add(shiroVaadinModule());
		baseModules.add(new ShiroAopModule());
		baseModules.add(userOptionsModule(ini));

		baseModules.add(baseModule());

		baseModules.add(standardViewModule());

		baseModules.add(componentModule());

		addAppModules(baseModules, ini);
		return baseModules;
	}

	protected Module componentModule() {
		return new DefaultComponentModule();
	}

	protected Module userOptionsModule(V7Ini ini) {
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
	 * Override this method if you have sub-classed {@link DefaultShiroModule} to provide bindings to your Shiro related
	 * implementations (for example, {@link Realm} and {@link CredentialsMatcher}
	 * 
	 * @param servletContext
	 * @param ini
	 * @return
	 */

	protected Module shiroModule(ServletContext servletContext, V7Ini ini) {
		return new DefaultShiroModule();
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
		ctx = createThreadLocalServletContext();
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