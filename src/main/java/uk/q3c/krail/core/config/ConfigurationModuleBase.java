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

import com.google.inject.multibindings.MapBinder;
import uk.q3c.krail.core.services.AbstractServiceModule;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A base class to define configuration files to be loaded into a {@link InheritingConfiguration} (for example
 * {@link ApplicationConfiguration}.
 * <p>
 * An integer index is used to specify the order in which the files are assess (see {@link InheritingConfiguration} for
 * an explanation)
 * <p>
 * You can use multiple modules based on this class (or create your own to populate an equivalent MapBinder) and Guice
 * will merge the map binders together. It is up to the developer to ensure that indexes are unique (but do not need to
 * bee contiguous).
 * <p>
 * Alternatively, it may be easier to use just one module and specify the files all in one place.
 *
 * @author David Sowerby
 */
public abstract class ConfigurationModuleBase extends AbstractServiceModule {
    private MapBinder<Integer, IniFileConfig> iniFileConfigs;
    private Map<Integer, IniFileConfig> prepIniFileConfigs = new HashMap<>();

    public MapBinder<Integer, IniFileConfig> getIniFileConfigs() {
        return iniFileConfigs;
    }

    public Map<Integer, IniFileConfig> getPrepIniFileConfigs() {
        return prepIniFileConfigs;
    }

    @Override
    protected void configure() {
        super.configure();
        iniFileConfigs = MapBinder.newMapBinder(binder(), Integer.class, IniFileConfig.class);
        bindConfigs();
    }

    /**
     * Override this with calls to {@link #addConfig(String, int, boolean)} to specify the configuration files to use.
     */
    protected abstract void bindConfigs();

    /**
     * Adds an ini file configuration at the specified index. A config will override properties with the same key from
     * a config at a lower index.
     *
     * @param filename the filename for the config file
     * @param priority the priority of this file (level 0 is at the 'top' - meaning it will override any properties of the same name which exist at 'lower'
     *                 levels
     * @param optional if false, a failure will occur if the file is not available / readable
     * @return this for fluency
     * @see InheritingConfiguration
     */
    protected ConfigurationModuleBase addConfig(String filename, int priority, boolean optional) {
        checkNotNull(filename);
        prepIniFileConfigs.put(priority, new IniFileConfig(filename, optional));
        return this;
    }

}
