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
package uk.q3c.krail.core.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.services.AbstractServiceI18N;
import uk.q3c.krail.core.services.Service;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This service provides a mechanism which can be used to manage the whole application configuration. It uses the
 * Apache
 * Commons Configuration library, and specifically a {@link CompositeConfiguration} so that the configuration can be
 * extended in any way the developer wishes.
 * <p/>
 * We have a preference for using HierarchicalINIConfiguration to provide a good level of human readability (when the
 * key value pairs are stored in a file) but any of the Apache {@link Configuration} implementations could be used.
 * <p/>
 * See the {@link Service} javadoc for more detail about Services
 * <p/>
 * Once this service has been started, access the configuration by injecting {@link InheritingConfiguration}. Note that
 * the configuration can be legitimately empty if no configuration non-default settings are required, and all calls for
 * configuration values provide a valid default.
 * <p/>
 * When this service is stopped the {@link InheritingConfiguration} is cleared.
 *
 * @author David Sowerby
 */
@Singleton
public class DefaultApplicationConfigurationService extends AbstractServiceI18N implements
        ApplicationConfigurationService {

    private static Logger log = LoggerFactory.getLogger(DefaultApplicationConfigurationService.class);

    private final ApplicationConfiguration configuration;

    private final Map<Integer, IniFileConfig> iniFiles;

    @Inject
    protected DefaultApplicationConfigurationService(Translate translate, ApplicationConfiguration configuration,
                                                     Map<Integer, IniFileConfig> iniFiles) {
        super(translate);
        this.configuration = configuration;
        this.iniFiles = iniFiles;
        configure();
    }

    /**
     * Override this method if you want to change the name or description keys.
     */
    protected void configure() {
        setNameKey(LabelKey.Application_Configuration_Service);
        setDescriptionKey(DescriptionKey.Application_Configuration_Service);
    }

    /**
     * The {@link #iniFiles} map is processed in ascending key order. If a file does not exist or fails to load for any
     * reason, and it is not marked as optional in IniFileConfig, a {@link ConfigurationException} is thrown. If,
     * however, the file fails to load, and is optional, no exception is raised.
     *
     * @throws ConfigurationException
     *         if an error occurs while loading a file
     */
    @Override
    protected void doStart() throws ConfigurationException {
        Set<Integer> keySorter = new TreeSet<>(iniFiles.keySet());
        for (Integer k : keySorter) {
            IniFileConfig iniConfig = iniFiles.get(k);
            File file = new File(ResourceUtils.configurationDirectory(), iniConfig.getFilename());
            try {
                if (!file.exists()) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }
                HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(file);
                configuration.addConfiguration(config);
                log.debug("adding configuration from {} at index {}", file.getAbsolutePath(), k);
            } catch (Exception ce) {
                if (!iniConfig.isOptional()) {
                    String msg = ("Configuration Service failed to start, unable to load required configuration file:" +
                            " " + file.getAbsolutePath());
                    status = Status.FAILED_TO_START;
                    log.error(msg);
                    throw new ConfigurationException(ce);
                } else {
                    log.info("Optional configuration file not found at {}, but as it is optional, " +
                            "" + "continuing without it", file);

                }
            }
        }

        log.info("Application Configuration Service started");
    }

    /**
     * Clears the {@link ApplicationConfiguration}
     */
    @Override
    protected void doStop() {
        configuration.clear();
    }

}
