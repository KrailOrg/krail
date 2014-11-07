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
package uk.q3c.krail.base.config;

/**
 * When an application is built on Krail, or a library is added, it is possible that a configuration value from another
 * part of Krail may need to be overridden with a new value. This class, combined with {@link InheritingConfiguration}
 * enable this to happen, by defining any configuration files this one should override.
 * <p/>
 * 'Overriding' only occurs if both files have a property of the same key
 * <p/>
 * The filename is relative to the {@link ResourceUtils#configurationDirectory()}
 *
 * @author David Sowerby
 */
public class IniFileConfig {

    private final String filename;
    private final boolean optional;

    public IniFileConfig(String filename, boolean optional) {
        this.filename = filename;
        this.optional = optional;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isOptional() {
        return optional;
    }
}
