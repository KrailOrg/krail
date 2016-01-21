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

package uk.q3c.krail.core.push

import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Specification
import uk.q3c.krail.core.config.ApplicationConfigurationModule
import uk.q3c.krail.core.persist.InMemoryModule
import uk.q3c.krail.core.services.ServicesModule
import uk.q3c.krail.testutil.*
import uk.q3c.krail.util.UtilsModule

/**
 * Created by David Sowerby on 19 Jan 2016
 */

class PushModuleTest extends Specification {

    def "bindings"() {
        when:
        Injector injector = Guice.createInjector(new PushModule(), new ApplicationConfigurationModule(), new TestUIScopeModule(), new UtilsModule(), new TestI18NModule(), new TestEventBusModule(), new TestVaadinSessionScopeModule(), new TestOptionModule(), new ServicesModule(), new InMemoryModule())


        then:
        injector.getInstance(PushMessageRouter.class) instanceof DefaultPushMessageRouter
        injector.getInstance(Broadcaster.class) instanceof DefaultBroadcaster
    }
}
