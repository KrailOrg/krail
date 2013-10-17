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

import uk.co.q3c.v7.base.guice.services.ServicesManager;
import uk.co.q3c.v7.base.guice.services.ServicesManagerModule;
import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.DefaultShiroModule;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.useropt.DefaultUserOptionModule;
import uk.co.q3c.v7.base.vaadin.VaadinModule;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.base.view.component.DefaultComponentModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

public abstract class BaseGuiceServletInjector extends
		GuiceServletContextListener {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaseGuiceServletInjector.class);

	protected static Injector injector;

	private ThreadLocal<ServletContext> ctx;

	protected BaseGuiceServletInjector() {
		super();
	}

	protected ThreadLocal<ServletContext> createThreadLocalServletContext() {
		return new ThreadLocal<ServletContext>();
	}

	protected void initInjector() {
		injector = Guice.createInjector(getModules());

		// The SecurityManager binding is in ShiroWebModule, and therefore
		// DefaultShiroWebModule. By default the binding
		// is to DefaultWebSecurityManager
		// FIXME: the module should initialize this
		SecurityUtils.setSecurityManager(injector.getInstance(SecurityManager.class));
	}

	/**
	 * Module instances for the base should be added in {@link #getModules()}.
	 * Module instance for the app using V7 should be added to
	 * {@link AppModules#appModules()}
	 * 
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	protected Injector getInjector() {
		if (injector == null) {
			throw new IllegalStateException(
					"The injector is not available, it may not yet been initialized.");
		}
		return injector;
	}

	private Iterable<Module> getModules() {
		List<Module> baseModules = new ArrayList<>();

		baseModules.add(new VaadinModule());
		baseModules.add(new I18NModule());
		baseModules.add(new ServicesManagerModule());
		
		baseModules.add(new ApplicationViewModule());
		
//		if (ini.optionReadSiteMap()) {
//			LOGGER.debug("ini sitemap option is true, loading sitemap");
//			Provider<Sitemap> sitemapPro = getInjector().getInstance(
//					SitemapProvider.class);
//			Sitemap sitemap = sitemapPro.get();
//			
//		} else {
//			// module for Views must be in addAppModules()
//			LOGGER.debug("ini sitemap option is false, not loading sitemap");
//		}
		
		baseModules.add(new ThreadScopeModule());
		baseModules.add(new UIScopeModule());

		baseModules.add(shiroModule(ctx.get()));
		baseModules.add(shiroVaadinModule());
		baseModules.add(new ShiroAopModule());
		baseModules.add(userOptionsModule());

		baseModules.add(baseModule());

		baseModules.add(standardViewModule());

		baseModules.add(componentModule());

		addAppModules(baseModules);
		return baseModules;
	}

	protected Module componentModule() {
		return new DefaultComponentModule();
	}

	protected Module userOptionsModule() {
		return new DefaultUserOptionModule();
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
	 * Override this method if you have sub-classed {@link ShiroVaadinModule} to
	 * provide your own bindings for Shiro related exceptions.
	 * 
	 * @return
	 */
	protected Module shiroVaadinModule() {
		return new ShiroVaadinModule();
	}

	/**
	 * Override this if you have sub-classed {@link StandardViewModule} to
	 * provide bindings to your own standard page views
	 */
	protected Module standardViewModule() {
		return new StandardViewModule();
	}

	/**
	 * Override this method if you have sub-classed {@link DefaultShiroModule}
	 * to provide bindings to your Shiro related implementations (for example,
	 * {@link Realm} and {@link CredentialsMatcher}
	 * 
	 * @param servletContext
	 * @param ini
	 * @return
	 */
	protected Module shiroModule(ServletContext servletContext) {
		return new DefaultShiroModule();
	}

	/**
	 * Add as many application specific Guice modules as you wish by overriding
	 * this method.
	 * 
	 * @param baseModules
	 * @param ini
	 */
	protected abstract void addAppModules(List<Module> baseModules);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ctx = createThreadLocalServletContext();
		final ServletContext servletContext = servletContextEvent
				.getServletContext();
		ctx.set(servletContext);

		initInjector();

		super.contextInitialized(servletContextEvent);

		getInjector().getInstance(ServicesManager.class).start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		getInjector().getInstance(ServicesManager.class).stop();

		super.contextDestroyed(servletContextEvent);
		ctx.remove();
	}

}
