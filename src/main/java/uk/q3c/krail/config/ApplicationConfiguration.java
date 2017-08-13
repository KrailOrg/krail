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

package uk.q3c.krail.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 * Created by David Sowerby on 15 Jan 2016
 */
public interface ApplicationConfiguration extends Configuration {


    void addConfiguration(Configuration config);

    int getNumberOfConfigurations();

    SubnodeConfiguration getSection(String configSectionName);
}
