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
package uk.q3c.krail.core.config;

import uk.q3c.krail.i18n.LabelKey;

/**
 * The Guice module used to configure the application configuration service
 *
 * @author David Sowerby
 */
public class ApplicationConfigurationModule extends ConfigurationModuleBase {

    @Override
    protected void configure() {
        super.configure();
        bindApplicationConfigurationService();
        bindApplicationConfiguration();
        define();
    }

    /**
     * Override if you want to sub-class and define, rather than use {@link #addConfig(String, int, boolean)}
     */
    protected void define() {
        addConfig("krail.ini", 100, true);
    }

    @Override
    protected void registerServices() {
        registerService(LabelKey.Application_Configuration_Service, ApplicationConfigurationService.class);
    }

    @Override
    protected void defineDependencies() {
//There are none
    }

    /**
     * Override this method to provide our own implementation of {@link ApplicationConfigurationService}
     */
    protected void bindApplicationConfigurationService() {
        bind(ApplicationConfigurationService.class).to(DefaultApplicationConfigurationService.class);
    }

    /**
     * Override this method to provide our own implementation of {@link ApplicationConfiguration}
     */
    protected void bindApplicationConfiguration() {
        bind(ApplicationConfiguration.class).to(DefaultApplicationConfiguration.class);
    }

    @Override
    protected void bindConfigs() {
        getPrepIniFileConfigs().forEach((pty, cfg) -> getIniFileConfigs().addBinding(pty)
                                                                         .toInstance(cfg));
    }


}
