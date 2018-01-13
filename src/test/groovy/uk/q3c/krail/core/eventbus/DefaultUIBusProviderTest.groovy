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

package uk.q3c.krail.core.eventbus

import net.engio.mbassy.bus.common.PubSubSupport
import spock.lang.Specification
import uk.q3c.krail.eventbus.BusMessage

/**
 * Created by David Sowerby on 17 Jan 2016
 */
class DefaultUIBusProviderTest extends Specification {

    PubSubSupport<BusMessage> uiBus = Mock(PubSubSupport)
    DefaultUIBusProvider provider

    def setup() {
        provider = new DefaultUIBusProvider(uiBus)
    }

    def "get and getUIBus"() {
        expect:
        provider.get() == uiBus
    }
}
