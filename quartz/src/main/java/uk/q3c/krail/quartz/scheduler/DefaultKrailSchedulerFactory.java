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
package uk.q3c.krail.quartz.scheduler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.MapConfiguration;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.base.config.ApplicationConfiguration;
import uk.q3c.krail.base.config.InheritingConfiguration;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.quartz.service.QuartzService;

import java.util.Properties;

/**
 * Enables the use of {@link KrailScheduler} to provide I18N support, but is otherwise the same as StdSchedulerFactory
 *
 * @author David Sowerby
 */
public class DefaultKrailSchedulerFactory extends StdSchedulerFactory implements KrailSchedulerFactory {
    private static Logger log = LoggerFactory.getLogger(DefaultKrailSchedulerFactory.class);
    private final Translate translate;
    private final Provider<InheritingConfiguration> combinedConfigurationProvider;
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    protected DefaultKrailSchedulerFactory(Translate translate, ApplicationConfiguration applicationConfiguration,
                                           Provider<InheritingConfiguration> combinedConfigurationProvider) {
        super();
        this.translate = translate;
        this.combinedConfigurationProvider = combinedConfigurationProvider;
        this.applicationConfiguration = applicationConfiguration;
    }

    /**
     * Overrides the instantiate method to create a {@link KrailScheduler} instance
     *
     * @see org.quartz.impl.StdSchedulerFactory#instantiate(org.quartz.core.QuartzSchedulerResources,
     * org.quartz.core.QuartzScheduler)
     */
    @Override
    protected Scheduler instantiate(QuartzSchedulerResources rsrcs, QuartzScheduler qs) {
        Scheduler scheduler = new KrailScheduler(translate, qs);
        return scheduler;
    }

    /**
     * Quartz uses getScheduler() to create a scheduler if none currently exist. The call to
     * {@link #instantiate(QuartzSchedulerResources, QuartzScheduler)} is buried deep in a long and complex private
     * instantiate method. this means that we have to use getScheduler() here, which also means that if this method is
     * called a second time, the first instance will be returned, in exactly the same way as getScheduler().
     * <p/>
     * If two calls to createScheduler are made with a configuration holding the same name, the second call will return
     * the instance created by the first call. This is true even if the two calls are made from different instances of
     * {@link DefaultKrailSchedulerFactory}.
     * <p/>
     * Note that this method (delegated to {@link #composeConfiguration(SchedulerConfiguration)}) also provides the
     * logic to combined potential configuration sources as described in the javadoc for {@link
     * SchedulerConfiguration}.
     * The dependency on the ApplicationConfigurationService being started is managed through the {@link QuartzService}
     *
     * @see KrailSchedulerFactory#createScheduler(uk.q3c.krail.quartz.scheduler
     * .SchedulerConfiguration)
     */
    @Override
    public KrailScheduler createScheduler(SchedulerConfiguration configuration) throws SchedulerException {
        Properties properties = composeConfiguration(configuration);
        initialize(properties);
        Scheduler scheduler = getScheduler();
        log.debug("Scheduler '{}' created", scheduler.getMetaData()
                                                     .getSchedulerName());
        return (KrailScheduler) scheduler;
    }

    /**
     * Combines potential configuration sources as described in the javadoc for {@link SchedulerConfiguration}
     *
     * @param configuration
     *
     * @return
     */
    protected Properties composeConfiguration(SchedulerConfiguration configuration) {
        // Guice module configuration only (contained in configuration)
        if (configuration.getConfigSectionName() == null && configuration.getPropertyFileName() == null) {
            return configuration.getProperties();
        }
        // else combine all sources
        InheritingConfiguration ic = combinedConfigurationProvider.get();
        Properties guiceProperties = configuration.getProperties();
        MapConfiguration guiceConfig = new MapConfiguration(guiceProperties);
        ic.addConfiguration(guiceConfig);

        if (configuration.getConfigSectionName() != null) {
            applicationConfiguration.getSection(configuration.getConfigSectionName());
        }

        Properties properties = ConfigurationConverter.getProperties(ic);
        return properties;
    }

}
