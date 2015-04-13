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

package uk.q3c.krail.core.eventbus;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by David Sowerby on 10/03/15.
 */
public class DefaultEventBusErrorHandler implements IPublicationErrorHandler {

    private static Logger log = LoggerFactory.getLogger(DefaultEventBusErrorHandler.class);
    /**
     * Handle the given publication error.
     *
     * @param error
     *         The PublicationError to handle.
     */
    @Override
    public void handleError(PublicationError error) {
        throw new RuntimeException(error.getMessage(), error.getCause());
    }
}
