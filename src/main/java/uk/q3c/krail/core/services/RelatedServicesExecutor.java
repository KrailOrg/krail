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

package uk.q3c.krail.core.services;

/**
 * Executes changes of state on the services related to a {@link Service} defined by {@link #setService}. Specifically this means calling:<ol>
 * <li>start() on the dependencies of the selected Service</li>
 * <li>stop() on the dependants of the selected Service</li>
 * </ol>
 * <p>
 * Created by David Sowerby on 11 Jan 2016
 */
public interface RelatedServicesExecutor {

    enum Action {START, STOP}

    /**
     * The service to act on
     *
     * @param service The service to act on
     */
    void setService(Service service);

    /**
     * Execute the required {@code action}.  {@link Action#START} always operates on dependencies, {@link Action#STOP} always operates
     * on dependants.<br><br>
     * {@link Action#STOP}  is applied only to those dependants which are {@link Dependency.Type#ALWAYS_REQUIRED}.
     *
     * @param action the action to take
     * @param cause  the cause of the state change
     * @return true if ALL the executed services return the expected result.  Returns false if any does not achieve the expected state.  For service
     * starts, the result of attempting to start an {@link Dependency.Type#OPTIONAL} dependency is ignored.
     */
    boolean execute(Action action, Service.Cause cause);
}
