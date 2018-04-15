/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.guice;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Modules;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.bval.guice.ValidationModule;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.config.bind.ApplicationConfigurationModule;
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.threadscope.ThreadScopeModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.KrailI18NModule;
import uk.q3c.krail.core.navigate.NavigationModule;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.SitemapModule;
import uk.q3c.krail.core.navigate.sitemap.StandardPagesModule;
import uk.q3c.krail.core.option.KrailOptionModule;
import uk.q3c.krail.core.push.PushModule;
import uk.q3c.krail.core.shiro.DefaultShiroModule;
import uk.q3c.krail.core.shiro.ShiroVaadinModule;
import uk.q3c.krail.core.shiro.aop.KrailShiroAopModule;
import uk.q3c.krail.core.ui.DataTypeModule;
import uk.q3c.krail.core.ui.DefaultUIModule;
import uk.q3c.krail.core.user.UserModule;
import uk.q3c.krail.core.vaadin.DataModule;
import uk.q3c.krail.core.validation.KrailValidationModule;
import uk.q3c.krail.core.view.ViewModule;
import uk.q3c.krail.core.view.component.DefaultComponentModule;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.i18n.bind.I18NModule;
import uk.q3c.krail.option.bind.OptionModule;
import uk.q3c.krail.persist.InMemory;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.startup.DefaultStartupModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;
import uk.q3c.util.guice.SerializationSupportModule;

import java.util.ArrayList;
import java.util.List;


/**
 * Collects together all the modules used for bindings. Groupings of modules is for clarity only - they do not have to be separate for any other reason.
 */
public class CoreBindingsCollator implements BindingsCollator {
    private static Logger log = LoggerFactory.getLogger(CoreBindingsCollator.class);
    private final ImmutableList<Module> additionalModules;

    public CoreBindingsCollator() {
        super();
        this.additionalModules = ImmutableList.of();
    }

    public CoreBindingsCollator(Module... modules) {
        this.additionalModules = ImmutableList.copyOf(modules);
    }

    @Override
    public List<Module> allModules() {
        List<Module> modules = new ArrayList<>(30);
        modules.addAll(additionalModules);


        modules.add(startupModule());
        modules.add(uiModule());
        modules.add(i18NModule());
        modules.add(applicationConfigurationModule());
        modules.add(sitemapModule());

        modules.add(new ThreadScopeModule());
        modules.add(new UIScopeModule());
        modules.add(new VaadinSessionScopeModule());

        modules.add(shiroModule());
        modules.add(shiroVaadinModule());
        modules.add(shiroAopModule());

        modules.add(servletModule());

        modules.add(standardPagesModule());

        modules.add(viewModule());

        modules.add(componentModule());

        modules.add(userModule());

        modules.add(optionModule());

        modules.addAll(eventBusModules());

        modules.add(navigationModule());

        modules.add(dataModule());
        modules.add(dataTypeModule());
        modules.add(pushModule());

        addUtilModules(modules);
        addValidationModules(modules);

        addAppModules(modules);
        addSitemapModules(modules);
        addPersistenceModules(modules);
        addSerializationSupport(modules);
        return modules;
    }

    private void addSerializationSupport(List<Module> modules) {
        modules.add(new SerializationSupportModule());
    }


    protected Module sitemapModule() {
        return new SitemapModule();
    }

    protected void addUtilModules(List<Module> coreModules) {
        coreModules.add(new UtilModule());
        coreModules.add(new UtilsModule());
    }

    protected Module shiroAopModule() {
        return new KrailShiroAopModule();
    }


    /**
     * Override this if you have provided your own {@link DataTypeModule} implementation
     *
     * @return a new {@link DataTypeModule} instance
     */
    protected Module dataTypeModule() {
        return new DataTypeModule();
    }


    /**
     * Override this if you have provided your own {@link PushModule} implementation
     *
     * @return a new {@link PushModule} instance
     */
    protected Module pushModule() {
        return new PushModule();
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
     * Override this if you have provided your own {@link VaadinEventBusModule} implementation
     *
     * @return a new {@link VaadinEventBusModule} instance
     */
    protected List<Module> eventBusModules() {
        return ImmutableList.of(new VaadinEventBusModule(), new EventBusModule());
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
     * @param modules the list used to collect modules for injector creation
     */
    protected void addValidationModules(List<Module> modules) {

        final Module validationModule = Modules.override(new ValidationModule())
                .with(new KrailValidationModule());
        modules.add(validationModule);
    }


    /**
     * Sets the default active source to read/write Option values from / to the in memory store
     * <p>
     * Override this if you have provided your own {@link OptionModule} or want to change the active source
     *
     * @return module instance
     */
    protected Module optionModule() {
        return new KrailOptionModule().activeSource(InMemory.class);
    }

    /**
     * Override this if you have provided your own {@link I18NModule}
     *
     * @return a Module for I18N
     */
    protected Module i18NModule() {
        return new KrailI18NModule();
    }

    /**
     * Override this if you have provided your own {@link ApplicationConfigurationModule}
     *
     * @return new instance of ApplicationConfigurationModule
     */

    protected Module applicationConfigurationModule() {
        return new KrailApplicationConfigurationModule();
    }

    /**
     * Modules used in the creation of the {@link MasterSitemap} do not actually need to be separated, this just makes a convenient way of seeing them as a
     * group
     *
     * @param modules the list used to collect modules for injector creation
     */
    @SuppressFBWarnings("ACEM_ABSTRACT_CLASS_EMPTY_METHODS")
    protected void addSitemapModules(List<Module> modules) {
    }

    protected Module componentModule() {
        return new DefaultComponentModule();
    }

    /**
     * Override this if you have provided your own {@link ServletModule}
     *
     * @return servlet module instance
     */
    protected Module servletModule() {
        return new BaseServletModule();
    }

    /**
     * Override this method if you have sub-classed {@link ShiroVaadinModule} to provide your own bindings for Shiro
     * related exceptions.
     *
     * @return a module for bindings which realte to Shiro wihtin a Vaadin environment
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
     * Override this method if you have sub-classed {@link DefaultShiroModule} to provide bindings to your Shiro
     * related implementations (for example, {@link Realm} and {@link CredentialsMatcher}
     *
     * @return a new {@link DefaultShiroModule} instance
     */

    protected Module shiroModule() {
        return new DefaultShiroModule();
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
     *
     * @param modules the list used to collect modules for injector creation
     */
    protected void addAppModules(List<Module> modules) {
    }

    /**
     * Add as many persistence related modules as needed.  These modules do not need to be separated, this just forms a convenient grouping for clarity
     *
     * @param modules the list used to collect modules for injector creation
     */
    protected void addPersistenceModules(List<Module> modules) {
        modules.add(new InMemoryModule().provideOptionDao()
                .providePatternDao());
    }

    /**
     * Override this if you have provided your own Startup module
     *
     * @return a Module for application startup
     */
    protected Module startupModule() {
        return new DefaultStartupModule();
    }

}