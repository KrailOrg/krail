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
package uk.co.q3c.v7.base.config;

import java.io.File;

import uk.co.q3c.v7.base.services.ServiceI18N;

public interface ApplicationConfigurationService extends ServiceI18N {

	/**
	 * Adds a configuration file. Key value pairs in {@code configuration} will override any matching keys
	 * configurations added previously. (<em>NOTE: </em>If the implementation is using the the Apache Commons
	 * Configuration library, the Apache interface works the other way round - the "overriding" configuration must be
	 * added first).
	 * <p>
	 * 
	 * This method may only be called when the service is in any state, but if the Service is not in the STOPPED state,
	 * the change will not take effect until the service is stopped and restarted.
	 * <p>
	 */
	void addConfiguration(File configurationFile);

	/**
	 * Adds a configuration file name. Key value pairs in {@code configuration} will override any matching keys
	 * configurations added previously. (<em>NOTE: </em>If the implementation is using the the Apache Commons
	 * Configuration library, the Apache interface works the other way round - the "overriding" configuration must be
	 * added first).
	 * <p>
	 * 
	 * This method may only be called when the service is in any state, but if the Service is not in the STOPPED state,
	 * the change will not take effect until the service is stopped and restarted.
	 * <p>
	 */
	void addConfiguration(String configurationFileName);
}
