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

package uk.q3c.krail.core.services;

/**
 * Implementation ensures that dependencies between services are managed appropriately.  The dependencies are defined by
 * configuring {@link ServicesGraph}.  {@link AbstractService} delegates calls to start() and stop() to this interface,
 * as should any Service implementation which continues to use this interface/
 * <p>
 * Created by David Sowerby on 24/10/15.
 */
public interface ServicesController {

    /**
     * Starts any dependencies which are required in order to start {@code service}.  These are a combination of {@link ServicesGraph#alwaysDependsOn
     * (ServiceKey, ServiceKey)} and {@link ServicesGraph#requiresOnlyAtStart(ServiceKey, ServiceKey)}.
     *
     * @param dependant
     *         the service to start the dependencies for
     *
     * @return true if all required dependencies attain a state of {@link Service.State#STARTED}, false if any dependency fails to do so
     */
    boolean startDependenciesFor(Service dependant);

    /**
     * Stops all dependants which have declared that {@code dependency} must be running in order for them to continue running (see {@link
     * ServicesGraph#alwaysDependsOn(ServiceKey, ServiceKey)}).
     *
     * @param dependency
     *         the dependency which requires its dependants to be stopped
     * @param dependencyFailed
     *         if true, the dependency has called this method because it failed, if false, the dependency has been stopped
     */
    void stopDependantsOf(Service dependency, boolean dependencyFailed);

    /**
     * Stops all services.  Usually only used during shutdown
     */
    void stopAllServices();
}
