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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.I18NKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.services.AbstractService;
import uk.q3c.krail.core.services.RelatedServicesExecutor;
import uk.q3c.krail.core.services.Service;
import uk.q3c.krail.util.ResourceUtils;

import javax.annotation.concurrent.ThreadSafe;
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
@ThreadSafe
public class DefaultApplicationConfigurationService extends AbstractService implements ApplicationConfigurationService {

    private static Logger log = LoggerFactory.getLogger(DefaultApplicationConfigurationService.class);

    private final ApplicationConfiguration configuration;

    private final Map<Integer, IniFileConfig> iniFiles;
    private ResourceUtils resourceUtils;

    @Inject
    protected DefaultApplicationConfigurationService(Translate translate, ApplicationConfiguration configuration, Map<Integer, IniFileConfig> iniFiles,
                                                     GlobalBusProvider globalBusProvider, ResourceUtils resourceUtils,
                                                     RelatedServicesExecutor servicesExecutor) {
        super(translate, globalBusProvider, servicesExecutor);
        this.configuration = configuration;
        this.iniFiles = iniFiles;
        this.resourceUtils = resourceUtils;
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
    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_HAS_CHECKED")
    @Override
    protected void doStart() {
        Set<Integer> keySorter = new TreeSet<>(iniFiles.keySet()).descendingSet();
        for (Integer k : keySorter) {
            IniFileConfig iniConfig = iniFiles.get(k);
            File file = new File(resourceUtils.configurationDirectory(), iniConfig.getFilename());
            try {
                if (!file.exists()) {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }
                HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(file);
                configuration.addConfiguration(config);
                log.debug("adding configuration from {} at index {}", file.getAbsolutePath(), k);
            } catch (Exception ce) {
                if (!iniConfig.isOptional()) {
                    String msg = "Configuration Service failed to start, unable to load required configuration file: {}";
                    log.error(msg, file.getAbsolutePath());
                    throw new ConfigurationException(msg + file.getAbsolutePath(), ce);
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


    @Override
    public synchronized I18NKey getNameKey() {
        return LabelKey.Application_Configuration_Service;
    }
}
