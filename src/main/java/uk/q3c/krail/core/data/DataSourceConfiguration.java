/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.data;

import uk.q3c.krail.config.ApplicationConfiguration;

/**
 * A common interface to provide configuration for a logical data source.  There may be multiple sources in an application - perhaps a Graph database for
 * user management, a relational database for OLTP, a NOSQL database for logging and a REST API for geolocation lookup.  Implementations of this interface
 * enable the configuration to carried out in a Guice module ({@link DataModule} by default).
 * <p>
 * You may also want to use the {@link ApplicationConfiguration} to enable configuration to be changed without recompiling.
 * <p>
 * The Krail core does not contain any implementations for this interface, they are provided by additional libraries, such as krail-jpa and krail-orient
 * <p>
 * Created by David Sowerby on 03/04/15.
 */
public interface DataSourceConfiguration {
}
