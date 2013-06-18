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
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.shiro.DefaultShiroWebModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

public abstract class BaseGuiceServletInjector extends
		GuiceServletContextListener {
	protected static Injector injector;

	private final ThreadLocal<ServletContext> ctx = new ThreadLocal<ServletContext>();

	/**
	 * Module instances for the base should be added in {@link #getModules()}.
	 * Module instance for the app using V7 should be added to
	 * {@link AppModules#appModules()}
	 * 
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	protected Injector getInjector() {

		injector = Guice.createInjector(new IniModule(),
				new DefaultShiroWebModule(ctx.get()));

		injector = injector.createChildInjector(getModules());

		// The SecurityManager binding is in ShiroWebModule, and therefore
		// DefaultShiroWebModule. By default the binding
		// is to DefaultWebSecurityManager
		SecurityManager securityManager = injector
				.getInstance(SecurityManager.class);
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
		baseModules.add(new V7ShiroVaadinModule());
		baseModules.add(new ShiroAopModule());
		baseModules.add(new BaseModule());
		baseModules.add(new ThreadScopeModule());
		baseModules.add(new UIScopeModule());
		baseModules.add(new I18NModule());
		baseModules.add(new StandardViewModule());
		addAppModules(baseModules);
		return baseModules;
	}

	protected abstract void addAppModules(List<Module> baseModules);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServletContext servletContext = servletContextEvent
				.getServletContext();
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