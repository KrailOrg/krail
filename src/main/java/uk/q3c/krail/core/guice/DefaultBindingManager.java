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
package uk.q3c.krail.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.apache.bval.guice.ValidationModule;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.config.ApplicationConfigurationModule;
import uk.q3c.krail.core.data.DataModule;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.threadscope.ThreadScopeModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.SitemapModule;
import uk.q3c.krail.core.navigate.sitemap.StandardPagesModule;
import uk.q3c.krail.core.services.ServiceModule;
import uk.q3c.krail.core.services.ServicesMonitor;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.shiro.StandardShiroModule;
import uk.q3c.krail.core.ui.DefaultUIModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.user.opt.OptionModule;
import uk.q3c.krail.core.validation.KrailValidationModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.i18n.I18NModule;

import javax.servlet.ServletContextEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class DefaultBindingManager extends GuiceServletContextListener {
    protected static Injector injector;
    private static Logger log = LoggerFactory.getLogger(DefaultBindingManager.class);

    protected DefaultBindingManager() {
        super();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("Stopping services");
        try {
            if (injector != null) {
                injector.getInstance(ServicesMonitor.class)
                        .stopAllServices();
            } else {
                log.debug("Injector has not been constructed, no call made to stop services");
            }
        } catch (Exception e) {
            log.error("Exception while stopping services", e);
        }
        //context may not have been crated, and super does not check for it
        if (servletContextEvent.getServletContext() != null) {
            super.contextDestroyed(servletContextEvent);
        }
    }

    /**
     * Module instances for the core should be added in {@link #getModules()}. Module instances for the app using Krail
     * should be added to {@link #addAppModules(List)}
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
        // KrailSecurityManager in DefaultShiroModule#bindSecurityManager (or potentially to another security manager if
        // the developer overrides that method)
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        SecurityUtils.setSecurityManager(securityManager);

    }

    private List<Module> getModules() {
        List<Module> coreModules = new ArrayList<>();

        coreModules.add(uiModule());
        coreModules.add(i18NModule());
        coreModules.add(applicationConfigurationModule());
        coreModules.add(new SitemapModule());

        coreModules.add(new ThreadScopeModule());
        coreModules.add(new UIScopeModule());
        coreModules.add(new VaadinSessionScopeModule());

        coreModules.add(new ServiceModule());

        coreModules.add(shiroModule());
        coreModules.add(shiroVaadinModule());
        coreModules.add(new ShiroAopModule());

        coreModules.add(servletModule());

        coreModules.add(standardPagesModule());

        coreModules.add(viewModule());

        coreModules.add(componentModule());

        coreModules.add(userModule());

        coreModules.add(optionModule());

        coreModules.add(eventBusModule());

        coreModules.add(navigationModule());

        coreModules.add(dataModule());

        addValidationModules(coreModules);

        addAppModules(coreModules);
        addSitemapModules(coreModules);
        return coreModules;
    }

    protected Module uiModule() {
        return new DefaultUIModule();
    }

    /**
     * Override this if you have provided your own {@link DataModule} implementation
     *
     * @return a new {@link DataModule} instance
     */
    protected Module dataModule() {
        return new DataModule();
    }

    /**
     * Override this if you have provided your own {@link EventBusModule} implementation
     *
     * @return a new {@link EventBusModule} instance
     */
    protected Module eventBusModule() {
        return new EventBusModule();
    }

    /**
     * Override this if you have provided your own {@link NavigationModule}
     *
     * @return new instance of ApplicationConfigurationModule
     */

    protected AbstractModule navigationModule() {
        return new NavigationModule();
    }

    /**
     * Override this method if you want to use an alternative implementation for the Krail validation integration.  You
     * will need to keep the Apache Bval {{@link ValidationModule} unless you replace the the javax validation
     * implementation.
     *
     * @param coreModules
     *
     * @return
     */
    protected void addValidationModules(List<Module> coreModules) {
        coreModules.add(new ValidationModule());
        coreModules.add(new KrailValidationModule());
    }


    /**
     * Override this if you have provided your own {@link OptionModule}
     *
     * @return
     */
    protected Module optionModule() {
        return new OptionModule();
    }

    /**
     * Override this if you have provided your own {@link I18NModule}
     *
     * @return
     */
    protected Module i18NModule() {
        return new I18NModule();
    }

    /**
     * Override this if you have provided your own {@link ApplicationConfigurationModule}
     *
     * @return new instance of ApplicationConfigurationModule
     */

    protected Module applicationConfigurationModule() {
        return new ApplicationConfigurationModule();
    }

    /**
     * Modules used in the creation of the {@link MasterSitemap} do not actually need to be separated, this just makes
     * a
     * convenient way of seeing them as a group
     *
     * @param baseModules
     */
    protected void addSitemapModules(List<Module> baseModules) {
    }

    protected Module componentModule() {
        return new DefaultComponentModule();
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
     * Override this if you have sub-classed {@link ViewModule} to provide bindings to your own standard page views
     */
    protected Module viewModule() {
        return new ViewModule();
    }

    /**
     * Override this method if you have sub-classed {@link StandardShiroModule} to provide bindings to your Shiro
     * related implementations (for example, {@link Realm} and {@link CredentialsMatcher}
     *
     * @return a new {@link StandardShiroModule} instance
     */

    protected Module shiroModule() {
        return new StandardShiroModule();
    }

    /**
     * Override this if you have sub-classed {@link UserModule} to provide bindings to your user related
     * implementations
     *
     * @return a new instance of {@link UserModule} or sub-class
     */
    protected UserModule userModule() {
        return new UserModule();
    }

    /**
     * Add as many application specific Guice modules as you wish by overriding this method.
     */
    protected abstract void addAppModules(List<Module> baseModules);
}