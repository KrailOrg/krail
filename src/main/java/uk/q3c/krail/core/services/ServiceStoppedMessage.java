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

import uk.q3c.krail.core.eventbus.BusMessage;

/**
 * A bus message to indicate that a service has stopped.  Use particularly to manage dependencies between Service implementations
 * <p>
 * Created by David Sowerby on 11/03/15.
 */
public class ServiceStoppedMessage implements BusMessage {

    private final Service service;

    public ServiceStoppedMessage(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
