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

package testutil;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.eventbus.GlobalBus;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.UIBus;

/**
 * Overrides the error handlers to just log the errors, rather than rethrow exceptions
 * <p>
 * Created by David Sowerby on 13/04/15.
 */
public class TestEventBusModule extends EventBusModule {

    /**
     * All buses use the default error handler by default.  Override this method to provide alternative bindings.
     */
    @Override
    protected void bindPublicationErrorHandlers() {
        bind((IPublicationErrorHandler.class)).annotatedWith(UIBus.class)
                                              .to(TestEventBusErrorHandler.class);
        bind((IPublicationErrorHandler.class)).annotatedWith(SessionBus.class)
                                              .to(TestEventBusErrorHandler.class);
        bind((IPublicationErrorHandler.class)).annotatedWith(GlobalBus.class)
                                              .to(TestEventBusErrorHandler.class);
    }
}
